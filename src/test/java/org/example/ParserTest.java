package org.example;

import org.example.kit.entity.BiSupplier;
import org.example.kit.entity.ByteArray;
import org.example.kit.io.ByteBuilder;
import org.example.work.crawl.WebCrawl;
import org.example.work.fingerprint.ExtractFingerprint;
import org.example.work.main.Before;
import org.example.work.parse.Parser;
import org.example.work.parse.nodes.Document;
import org.junit.Test;
import org.springframework.util.Assert;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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
            Document doc = parser.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_extract_fingerprint() {
        String url = "http://" + host;
        String url_news = "http://today.hit.edu.cn/article/2019/02/28/64283";
        try {
            BiSupplier<URL,byte[]> response = Objects.requireNonNull(WebCrawl.getHttpPacketLoadedWithHTML(url_news));
            byte[] data = response.second();
            String head = "HTTP/1.1 200 OK\r\n";
            ByteBuilder builder = new ByteBuilder(data.length + head.length());
            builder.write(head.getBytes());
            builder.write(data);
            ByteArray resp = new ByteArray(builder.getBytes());
            URI uri = new URI(url_news);
            int spIndex = resp.indexOf(new byte[]{'\r', '\n', '\r', '\n'});
            Assert.isTrue(spIndex >= 0, "错误的 HTTP 报文格式");
            ByteArray responseHeader = resp.subByteArray(0, spIndex);
            ByteArray responseBody = resp.subByteArray(spIndex + 4);
            Before before = new Before(responseBody,url_news);
            Document document = before.getDocument();
            byte[] fingerprint = ExtractFingerprint.extractFingerprint(null,responseHeader,document);
            for (byte b : fingerprint) {
                System.out.printf("%02x ",b);
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
