# twitterCrawler
A small crawler by extracting links from tweets using Twitter API

## Introduction
This project contains a simple focused crawler by extracting links from Twitter. Using the Twitter API (https://developer.twitter.com/), a hashtag is used as a query and then used to extract links from Twitter.

## Description
 The main aim is to extract tweets that contain links and use those links as seed for a crawler. Each page that contains a link is stored and followed using crawling techniques that take the number of links and depth as 2 main parameters. About 20000 links have been extracted and normalized. The following statistics have been computed from it as a result:

- Number of unique links extracted
- Frequency distribution by domain
- Breakdown of links by type (e.g., text, image, video)
- Average link depth
- For each crawled page, the number of incoming and outgoing links. And also the top-25 pages with the highest number of incoming and outgoing links.

## How to compile and run the code:
The below library dependencies were used to compile this project:
- oauth.signpost:signpost-commonshttp4:1.2.1.2
- org.apache.httpcomponents:httpcore:4.3
- org.apache.httpcomponents:httpclient:4.3-alpha1
- oauth.signpost:signpost-core:1.2.1.2
- commons-logging:commons-logging:1.1.3
- commons-io:commons-io:2.4
- commons-codec:commons-codec:1.6
- org.json:json:20180813
- org.jsoup:jsoup:1.11.3

 The data.txt contains the tweets that were extracted using the API and the query word “#oscar”. Please include the txt file in the same folder to start crawling from the extracted links.
 The tweet extractor code is in searchTweets() method. 
 The crawler method contains the crawler code and keeps track of the incoming and outgoing links from a particular URL.

