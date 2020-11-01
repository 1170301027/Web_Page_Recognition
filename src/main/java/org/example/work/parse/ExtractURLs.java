package org.example.work.parse;

import org.example.work.crawl.WebCrawl;
import org.jsoup.nodes.Document;

import java.util.HashSet;
import java.util.Set;

/**
 * @Classname ExtractURLs
 * @Description 根据网站获取网站相关的URLs
 * @Date 2020/10/29 11:36
 * @Created by shuaif
 */
public class ExtractURLs {
    /**
     * 根据网站获取网站相关的URL
     * @param website 网站域名
     * @return URLs
     */
    public static Set<String> extractURLsFromWebsite(String website){
        Set<String> resultSet = new HashSet<>();
        String websiteURL = website;
        if (!websiteURL.contains("http")) {
            websiteURL = "http://" + website;
        }
        Document document = WebCrawl.webCrawl(websiteURL);


        return resultSet;
    }
}
