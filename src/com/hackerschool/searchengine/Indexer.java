package com.hackerschool.searchengine;

import java.util.concurrent.LinkedBlockingQueue;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

public class Indexer implements Runnable {
	private LinkedBlockingQueue<UrlJob> indexQ;
	private IndexMap indexMap;
	private String[] stupidWords;
	
	public Indexer(LinkedBlockingQueue<UrlJob> indexQ, IndexMap indexMap){
		this.indexQ = indexQ;
		this.indexMap = indexMap;
		this.stupidWords = new String[]{"and", "or", "if", "can", "the", "but", "into", "in"};
	}
	
	public void run() {
		while(true){
			try {
				UrlJob job = indexQ.take();
				System.out.println(Thread.currentThread().getName() + " picked up a job from index Q for url: " + job.getUrl());
				visitPageAndIndex(job);
			} catch (InterruptedException e) {
			}
		}
	}
	
	private void visitPageAndIndex(UrlJob job) {
		String content = filteredContents(job);
		for (String word : content.split("\\s+")) {
			if (word.length() > 3) {
				indexMap.insertWord(word, job.getUrl());
			}
		}
	}

	private String filteredContents(UrlJob job) {
		String result = "";
		try {
			Element body = Jsoup.connect(job.getUrl()).get().body();
			result = body.text().toLowerCase();
			result = result.replaceAll("[^a-z]", " ");
			for (String word : this.stupidWords){
				result = result.replaceAll(word, " ");	
			}
			System.out.println(Thread.currentThread().getName() + "resulting is " + result);
			return result;
		} catch (Exception e) {
			System.out.println("INDEXING FAILURE...");
		}
		return result;
	}

}
