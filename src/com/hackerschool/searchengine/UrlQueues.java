package com.hackerschool.searchengine;

import java.util.concurrent.ConcurrentLinkedQueue;

public class UrlQueues {

	private ConcurrentLinkedQueue<String>[] allQueues;
	private final int NUM_QUEUES;
	
	@SuppressWarnings("unchecked")
	public UrlQueues(int num){
		this.NUM_QUEUES = num;
		this.allQueues = new ConcurrentLinkedQueue[num];
		for (int i = 0; i < num; i++){
			this.allQueues[i] = new ConcurrentLinkedQueue<String>();
		}
	}
	
	public void add(String url){
		int index = hashUrl(url);
		this.allQueues[index].add(url);
	}
	
	private int hashUrl(String url){
		return url.hashCode() % this.NUM_QUEUES;
	}

	public int getNumQueues(){
		return NUM_QUEUES;
	}
}
