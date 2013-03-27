package com.hackerschool.searchengine;

import java.util.HashMap;

public class IndexMap {

	private HashMap<String, HashMap<String,Integer>> indexMap;
	
	public IndexMap(){
		indexMap = new HashMap<String, HashMap<String,Integer>>();
	}

	public synchronized void insertWord(String word, String url){
		HashMap<String, Integer> urlToInt = indexMap.get(word);
		if (urlToInt != null){
			Integer val = urlToInt.get(url);
			val = (val != null) ? (val + 1) : 1; 
			urlToInt.put(url, val);
		} else {
			HashMap<String, Integer> urlMap = new HashMap<String, Integer>();
			urlMap.put(url, 1);
			indexMap.put(word, urlMap);
		}
	}
	
}
