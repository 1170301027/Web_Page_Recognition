package org.example;

import org.example.work.crawl.WebCrawl;
import org.jsoup.nodes.Document;
import org.junit.Test;

/**
 * @Classname ParserTest
 * @Description
 * @Date 2020/10/30 10:05
 * @Created by shuaif
 */
public class ParserTest {

    @Test
    public void testParser() {
        Document document = WebCrawl.webCrawl("http://google.com");
    }
}
