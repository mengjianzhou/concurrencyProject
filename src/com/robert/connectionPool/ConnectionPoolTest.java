package com.robert.connectionPool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionPoolTest {
	
	static ConnectionPool pool = new ConnectionPool(10);
	//��֤����Connection Runner �ܹ�ͬʱ��ʼ
	static CountDownLatch start = new CountDownLatch(1);
	//main�߳̽���ȴ�����ConnectionRunner��������ܼ���ִ��
	static CountDownLatch end;
	
	public static void main(String[] args) {
		//�߳������������޸� �������й۲�
		int threadCount = 10;
		end = new CountDownLatch(threadCount);
		
		int count = 20;
		AtomicInteger got = new AtomicInteger();
		AtomicInteger notGot = new AtomicInteger();
		
		for(int i=0;i<threadCount;i++){
			Thread thread = new Thread(new ConnectionRunner(count,got,notGot), "ConnectionRunnerThread");
			thread.start();
		}
	}
	
	static class ConnectionRunner implements Runnable{
		
		int count;
		AtomicInteger	got;
		AtomicInteger	notGot;
		
		public ConnectionRunner(int count,AtomicInteger got, AtomicInteger notGot){
			this.count = count;
			this.got = got;
			this.notGot = notGot;
		}
		
		@Override
		public void run() {
			try {
				start.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			while(count > 0){
				//���̳߳��л�ȡ����,���1000ms���޷���ȡ�������᷵��null
				//�ֱ�ͳ�����ӻ�ȡ������got��δ��ȡ������notGot
				Connection connection = null;
				try {
					connection = pool.fetchConnection(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				if(connection != null){
					try {
						connection.createStatement();
						connection.commit();
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						pool.releaseConnection(connection);
						got.incrementAndGet();
					}
				} else{
					notGot.incrementAndGet();
				}
			}
			end.countDown();
		}
	}
}
