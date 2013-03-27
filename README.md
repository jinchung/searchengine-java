As of Wed Mar 27:
Extremely naive implementation of search engine which involves:
- multiple scraper threads given an initial seed of Hacker News, extracts URLs from page
- single filter thread that filters any URLs already seen, otherwise it pushes to scraper thread to crawl
- single indexer thread that downloads HTML content for given URL and indexes naively

Known Crappiness:
- Bugginess in properly shutting down the threads after a certain number of URLs have been visited.

Magic starts in WebCrawler.java
