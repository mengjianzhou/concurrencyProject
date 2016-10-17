package com.robert.concurrency;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConditionTest {
	
	public static void main(String[] args) {
		
		Lock lock = new ReentrantLock();
		Condition condition = lock.newCondition();
		try {
			condition.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		condition.signal();
	}
}
