package org.example.work.main;

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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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

    public MyThread(int serial_number,String website) {
        this.serial_number = serial_number;
        this.website = website;
        this.urls = new LinkedList<>();
    }

    private void crawl(String url) {
        System.out.println(url);
        try {
            BiSupplier<URL,byte[]> response = Objects.requireNonNull(WebCrawl.getHttpPacketLoadedWithHTML(url));
            byte[] data = response.second();

            ByteArray content_encoding = null;
            if (WebCrawl.content_encoding != null) {
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
            extractFingerprintAndEigenWord(null,responseHeader,before);
            for (String new_url : before.getParser().getUrls()) {
                if (new_url.startsWith("/")) {
                    new_url = "http://" + website + new_url;
                }
                if (!urls.contains(new_url)) {
                    urls.offer(new_url);
                }
            }
        } catch (IOException | URISyntaxException e) {
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
        super.run();
        String host_url = "http://" + this.website + "/";
        this.urls.offer(host_url);
        while (this.urls != null) {
            String url = this.urls.poll();
            crawl(url);

        }
     }
}
