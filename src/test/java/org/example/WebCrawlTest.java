package org.example;

import org.example.crawl.WebCrawl;
import org.example.kit.entity.BiSupplier;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

/**
 * @Classname WebCrawlTest
 * @Description
 * @Date 2020/10/28 18:20
 * @Created by shuaif
 */
public class WebCrawlTest {
    private final String url = "http://baidu.com";

    @Test
    public void testGetHttpPackageWithHtml() throws IOException {

        BiSupplier<URL,byte[]> biSupplier = WebCrawl.getHttpPacketLoadedWithHTML(url);
        assert biSupplier != null;
        System.out.println(biSupplier.first());
        System.out.println(Arrays.toString(biSupplier.second()));

    }

    @Test
    public void testJsoup(){
        System.out.println(WebCrawl.webCrawl(url));
    }
}
