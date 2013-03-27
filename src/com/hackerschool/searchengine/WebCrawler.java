package com.hackerschool.searchengine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class WebCrawler {

	private LinkedBlockingQueue<UrlJob> scrapeQ;
	private LinkedBlockingQueue<UrlJob> filterQ;
	private LinkedBlockingQueue<UrlJob> indexQ;
	private List<String> seeds;
	private IndexMap indexMap;
	private String HACKER_NEWS = "https://news.ycombinator.com/news";
	private static int SCRAPE_THREAD_COUNT = 4;
	private static int FILTER_THREAD_COUNT = 1;
	private static int INDEX_THREAD_COUNT = 1;
	
	public WebCrawler(){
		seeds = new ArrayList<String>();
		seeds.add(HACKER_NEWS);
		indexMap = new IndexMap();
	}

	public void execute(){
		scrapeQ = new LinkedBlockingQueue<UrlJob>();
		filterQ = new LinkedBlockingQueue<UrlJob>();
		indexQ = new LinkedBlockingQueue<UrlJob>();
		UrlExtractor scraper = new UrlExtractor(scrapeQ, filterQ);
		Filterer filterer = new Filterer(filterQ, scrapeQ, indexQ);
		Indexer indexer = new Indexer(indexQ, indexMap);
		
		for (int i = 0; i<SCRAPE_THREAD_COUNT; i++){
			Thread scraperT = new Thread(scraper);
			scraperT.setName("Scraper " + i);
			scraperT.start();
		}
		
		for (int i = 0; i< FILTER_THREAD_COUNT; i++){
			Thread filterT = new Thread(filterer);
			filterT.setName("Filterer " + i);
			filterT.start();
		}
		
		for (int i = 0; i<INDEX_THREAD_COUNT; i++){
			Thread indexT = new Thread(indexer);
			indexT.setName("Indexer " + i);
			indexT.start();
		}

		for (String url : seeds){
			try {
				scrapeQ.put(new UrlJob(url));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
		
	public static void main(String[] args){
		WebCrawler crawl = new WebCrawler();
		crawl.execute();
	}
	
}
