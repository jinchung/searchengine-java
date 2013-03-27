package com.hackerschool.searchengine;

import java.util.HashSet;
import java.util.concurrent.LinkedBlockingQueue;

public class Filterer implements Runnable {

	private LinkedBlockingQueue<UrlJob> filterQ;
	private LinkedBlockingQueue<UrlJob> scrapeQ;
	private LinkedBlockingQueue<UrlJob> indexQ;
	private HashSet<UrlJob> filter;
	
	public Filterer(LinkedBlockingQueue<UrlJob>  filterQ, LinkedBlockingQueue<UrlJob>  scrapeQ, LinkedBlockingQueue<UrlJob>  indexQ){
		this.filterQ = filterQ;
		this.scrapeQ = scrapeQ;
		this.indexQ = indexQ;
		this.filter = new HashSet<UrlJob>();
	}
	
	public void run() {
		while(true){
			try {
				UrlJob job = filterQ.take();
				filter(job);					
			} catch (InterruptedException e) {
				break;
			}
		}
		
	}
	
	public void filter(UrlJob url){
		if (!filter.contains(url)){
			try {
				System.out.println(Thread.currentThread().getName()  + " sending ok url back to be scraped " + url.getUrl());
				scrapeQ.put(url);
				indexQ.put(url);
				filter.add(url);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}			
		} 
	}

}
