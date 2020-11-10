package org.example;

import org.example.work.crawl.WebCrawl;
import org.example.work.parse.Parser;
import org.junit.Test;

import java.io.IOException;
import java.util.Objects;

/**
 * @Classname ParserTest
 * @Description
 * @Date 2020/10/30 10:05
 * @Created by shuaif
 */
public class ParserTest {
    private final String host = "google.com";

    @Test
    public void testParser() {
//        Document document = WebCrawl.webCrawl("http://google.com");
        try {
            Parser parser = new Parser(Objects.requireNonNull(WebCrawl.getHttpPacketLoadedWithHTML(host)).second());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
