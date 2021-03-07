package org.example;

import org.example.auxiliary.FilePath;
import org.example.kit.FileKit;
import org.example.work.crawl.WebCrawl;
import org.example.kit.entity.BiSupplier;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * @Classname WebCrawlTest
 * @Description
 * @Date 2020/10/28 18:20
 * @Created by shuaif
 */
public class WebCrawlTest {
    private final String url = "http://google.com";

    @Test
    public void testGetHttpPackageWithHtml() throws IOException {

        BiSupplier<URL,byte[]> biSupplier = WebCrawl.getHttpPacketLoadedWithHTML(url);
        System.out.println(biSupplier.first());
        System.out.println(new String(biSupplier.second()));
        FileKit.writePacket(url,biSupplier.second());

    }

    @Test
    public void testJsoup(){
        String htmlDoc = WebCrawl.webCrawl(url).outerHtml();
        String filename = FilePath.SOURCE_PATH + url.hashCode();
        try {
            FileKit.writeAllLines(Collections.singletonList(htmlDoc),filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
    }
}
