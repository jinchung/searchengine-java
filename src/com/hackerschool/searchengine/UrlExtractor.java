package com.hackerschool.searchengine;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.concurrent.LinkedBlockingQueue;

public class UrlExtractor implements Runnable {

	private LinkedBlockingQueue<UrlJob> scrapeQ;
	private LinkedBlockingQueue<UrlJob> filterQ;
	private volatile int count = 0;

	public UrlExtractor(LinkedBlockingQueue<UrlJob>  scrapeQ, LinkedBlockingQueue<UrlJob>  filterQ){
		this.scrapeQ = scrapeQ;
		this.filterQ = filterQ;
	}
	
	public void run() {
		while(count < 20){
			try {
				UrlJob job = scrapeQ.take();
				System.out.println(Thread.currentThread().getName()  + " picked up a job from scrape Q for url: " + job.getUrl());
				count++;
				visit(job);
			} catch (InterruptedException e) {
				break;
			}
		}
	}
	
	public void visit(UrlJob job){
		StringBuilder sb = new StringBuilder();
		URLConnection urlConn = null;
		InputStreamReader in = null;
		try{
			URL url = new URL(job.getUrl());
			urlConn = url.openConnection();
			if (urlConn != null && urlConn.getInputStream() != null){
				in = new InputStreamReader(urlConn.getInputStream(),
                        Charset.defaultCharset());
                BufferedReader bufferedReader = new BufferedReader(in);
                if (bufferedReader != null) {
                    int cp;
                    while ((cp = bufferedReader.read()) != -1) {
                        sb.append((char) cp);
                    }
                    bufferedReader.close();
                }
			}
			in.close();
		}
		catch(Exception e) {
			throw new RuntimeException("Exception while calling URL:" + job.getUrl(), e);
		}
		String contents = sb.toString();
		extractUrls(contents);
	}
	
	public void extractUrls(String content){
		int first, second = 0;
		int i = 0;
		String url = "";
		String href = "<a href=";
		String start = "\"http";
		String end = "\"";
		while (i >= 0){
			i = content.indexOf(href, i);
			if (i != -1){
				first = content.indexOf(start, i);
				second = content.indexOf(end, first + 1);
				url = content.substring(first + 1, second);
				count++; 
				System.out.println(Thread.currentThread().getName() + " scraping found a new url to send to filter: " + url);
				sendUrlToFilter(url);
				i = second;
			}
		}
	}
	
	private void sendUrlToFilter(String url) {
		try {
			if (count < 20){ //TODO this is super crappy
				filterQ.put(new UrlJob(url));				
			}
		} catch (InterruptedException e) {
			System.out.println("Interrupted Exception when sending URL to filter Q");
		}
	}

}
