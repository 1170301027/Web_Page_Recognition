package org.example.work.main;

import org.example.auxiliary.FilePath;
import org.example.kit.FileKit;
import org.example.kit.entity.BiSupplier;
import org.example.kit.entity.ByteArray;
import org.example.kit.io.ByteBuilder;
import org.example.work.crawl.WebCrawl;
import org.example.work.eigenword.EigenWord;
import org.example.work.eigenword.ExtractEigenWord;
import org.example.work.fingerprint.ExtractFingerprint;
import org.example.work.parse.nodes.Document;
import org.example.work.parse.nodes.Element;
import org.example.work.parse.nodes.Node;
import org.springframework.util.Assert;

import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 * @Classname MyThread
 * @Description 线程，
 * @Date 2020/11/10 20:26
 * @Created by shuaif
 */
public class MyThread extends Thread{
    private final int serial_number;
    private final String website;
    private Queue<String> urls;
    private int threshold = 100; // 指定一个网站爬取网页的最大数量。
    private int count = 0;

    public MyThread(int serial_number,String website) {
        this.serial_number = serial_number;
        this.website = website;
        this.urls = new LinkedList<>();
    }

    /**
     * Crawl page and store to file (index.data)
     * @param url
     */
    private void crawlPages(String url) {
        try {
            BiSupplier<URL,byte[]> response = Objects.requireNonNull(WebCrawl.getHttpPacketLoadedWithHTML(url));
            FileKit.writePacket(url,response.second());
            count++ ;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 单独处理一个URL：爬取+储存原始报文+解析+提取指纹特征
     * @param url 网页 URL
     */
    private void crawl(String url) {
        if (url == null) return ;
        System.out.println(url);
        try {
            BiSupplier<URL,byte[]> response = Objects.requireNonNull(WebCrawl.getHttpPacketLoadedWithHTML(url));
            FileKit.writePacket(url,response.second());
            count++ ;
            byte[] data = response.second();

            ByteArray content_encoding = null;
            if (WebCrawl.content_encoding != null) { // 压缩格式，
                content_encoding = WebCrawl.content_encoding;
            }

            String head = "HTTP/1.1 200 OK\r\n";
            ByteBuilder builder = new ByteBuilder(data.length + head.length());
            builder.write(head.getBytes());
            builder.write(data);
            ByteArray resp = new ByteArray(builder.getBytes());
            URI uri = new URI(url);
            int spIndex = resp.indexOf(new byte[]{'\r', '\n', '\r', '\n'});
            Assert.isTrue(spIndex >= 0, "错误的 HTTP 报文格式");
            ByteArray responseHeader = resp.subByteArray(0, spIndex);
            ByteArray responseBody = resp.subByteArray(spIndex + 4);

            Before before = new Before(responseBody, url, content_encoding);
//            extractFingerprintAndEigenWord(null,responseHeader,before);
            for (String new_url : before.getParser().getUrls()) {
                if (new_url.startsWith("/")) {
                    new_url = "https://" + website + new_url;
                }
                if (new_url.startsWith("http")) {
                    urls.offer(new_url);
                }
            }
        } catch (IOException e) {
            System.out.println("爬取返回结果为空");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * 提取指纹以及网页特征向量
     * @param requestHeader 请求头部
     * @param responseHeader 响应头部
     * @param before 网页预处理结果
     */
    public void extractFingerprintAndEigenWord(ByteArray requestHeader, ByteArray responseHeader, Before before) {
        Document document = before.getDocument();
        ByteBuilder fingerprint;
        List<EigenWord> vector = new ArrayList<>();
        byte[] request_fingerprint = new byte[0], response_fingerprint = new byte[0], html_head_fingerprint = new byte[0], html_body_fingerprint = new byte[0];
        if (requestHeader != null) {
            // TODO 提取cookie字段
        }
        if (responseHeader != null) {
            response_fingerprint = ExtractFingerprint.handleResponseHeader(new String(responseHeader.getBytes()));
            vector.addAll(ExtractEigenWord.getLinearFingerprintEigenWord(response_fingerprint,ExtractEigenWord.RESPONSE_HEADER_TAG));
        }
        if (document != null) {
            html_head_fingerprint = ExtractFingerprint.handleHtmlHeader(document.getHtml().childElement("head"));
            vector.addAll(ExtractEigenWord.getLinearFingerprintEigenWord(html_head_fingerprint,ExtractEigenWord.HEAD_HTML_TAG));
            html_body_fingerprint = ExtractFingerprint.handleHtmlBody(document.getHtml().childElement("body"),vector);
        }
        // 提取静态特征
        ExtractEigenWord.getStaticFeatureEigenWord(before,vector);
        // 拼接指纹
        int length = request_fingerprint.length + response_fingerprint.length + html_head_fingerprint.length + html_body_fingerprint.length;
        fingerprint = new ByteBuilder(length);
        fingerprint.write(request_fingerprint);
        fingerprint.write(response_fingerprint);
        fingerprint.write(html_head_fingerprint);
        fingerprint.write(html_body_fingerprint);

        for (int i = 0; i < vector.size(); i++) {
            vector.get(i).setIndex(i);
        }

        for (byte b : fingerprint.getBytes()) {
            System.out.printf("%02x ",b);
        }
        System.out.println();
        System.out.println(vector.size());
        for (EigenWord eigenWord : vector) {
            System.out.printf(" %x , %d\n",eigenWord.getWord(), eigenWord.getFrequency());
        }

//        Fingerprint fp =  new Fingerprint();
//        fp.setLastUpdate(new Timestamp(System.currentTimeMillis()));
//
//        fp.setFpdata(fingerprint.getBytes());
    }

    @Override
    public void run() {
        String host_url = "https://" + this.website + "/";
        this.urls.offer(host_url);
        while (this.urls.size()!= 0 && count < 100) {
            String url = this.urls.poll();
            long start = System.currentTimeMillis();
            crawl(url);
            long end = System.currentTimeMillis();
            System.out.println("count = " + count + ", timespan = " + (end - start) + ", website = " + website + ", url = " + url);
            // ip -> host 记录
            if (count == 1) {
                try {
                    URL current_url = new URL(host_url);
                    String host = current_url.getHost();
                    InetAddress address = InetAddress.getByName(host);
                    String ip = address.getHostAddress();
                    InetAddress[] IP = InetAddress.getAllByName(host);
                    List<String> ipList = new ArrayList<>();
                    for (InetAddress inetAddress : IP) {
                        ipList.add(inetAddress.getHostAddress() + "," + host);
                    }
                    FileKit.writeAllLines(ipList, FilePath.ALL_IPS);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("serial:" + serial_number + " is over.");
    }
}
