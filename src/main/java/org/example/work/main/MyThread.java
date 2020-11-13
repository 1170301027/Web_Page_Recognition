package org.example.work.main;

import org.example.kit.entity.BiSupplier;
import org.example.kit.entity.ByteArray;
import org.example.kit.io.ByteBuilder;
import org.example.sql.pojo.Fingerprint;
import org.example.work.crawl.WebCrawl;
import org.example.work.fingerprint.ExtractFingerprint;
import org.example.work.parse.Parser;
import org.example.work.parse.nodes.Document;
import org.example.work.parse.nodes.Element;
import org.example.work.parse.nodes.Node;
import org.springframework.util.Assert;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.*;

/**
 * @Classname MyThread
 * @Description
 * @Date 2020/11/10 20:26
 * @Created by shuaif
 */
public class MyThread extends Thread{
    private final int serial_number;
    private Set<String> urls;

    public MyThread(int serial_number, Set<String> urls) {
        this.serial_number = serial_number;
        this.urls = urls;
    }

    private void crawl(String url) {
        try {
            BiSupplier<URL,byte[]> response = Objects.requireNonNull(WebCrawl.getHttpPacketLoadedWithHTML(url));
            byte[] data = response.second();
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
            Parser parser = new Parser(responseBody);
            Document document = parser.parse();
            ExtractFingerprint.extractFingerprint(null,responseHeader,document);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public Set<String> getNewUrls(URI uri, Node[] as){
        if(as.length == 0)
            return Collections.emptySet();
        Set<String> urls = new HashSet<>();
        Map<Integer,String> map = new HashMap<>();
        for(Node node : as){
            Element a = (Element)node;
            if(a.attr("href") == null)
                continue;
            String s = a.attr("href").toStr().trim();
            if(s.startsWith("https://"))
                s = s.replaceFirst("https://", "http://");
            else if(!s.startsWith("/") || s.startsWith("//") || s.endsWith(".pdf"))
                continue;
            s = s.indexOf('?') == -1 ? s : s.substring(0, s.indexOf('?'));
            int index = s.indexOf('/', 8), count = 0;
            while(index >= 0){
                count++;
                index = s.indexOf('/', index + 1);
            }
            if(s.startsWith("/") && count > 1 && !map.containsKey(count)){
                try{
                    map.put(count, uri.resolve(s).toString());
                }catch(Exception e){
                }
            }else if(count == 0 || (count == 1 && s.endsWith("/"))){
                try{
                    String newu = uri.resolve(s).getHost();
                    if(!newu.equals(uri.getHost()) && newu.contains(uri.getHost().replace("www.", "")))
                        urls.add(s);
                }catch(Exception e){
                }
            }
        }
        List<Map.Entry<Integer,String>> es = new ArrayList<>(map.entrySet());
        es.sort((a, b) -> -Integer.compare(a.getKey(), b.getKey()));
        for(Map.Entry<Integer,String> e : es){
            urls.add(e.getValue());
        }
        return urls;
    }

//    private void extractFingerprint(ByteArray requestHeader, ByteArray responseHeader, Document document) {
//        ByteBuilder fingerprint;
//        byte[] request_fingerprint = new byte[0], response_fingerprint = new byte[0], html_head_fingerprint = new byte[0], html_body_fingerprint = new byte[0];
//        if (requestHeader != null) {
//            // TODO 提取cookie字段
//        }
//        if (responseHeader != null) {
//            response_fingerprint = ExtractFingerprint.handleResponseHeader(new String(responseHeader.getBytes()));
//        }
//        if (document != null) {
//            html_head_fingerprint = ExtractFingerprint.handleHtmlHeader(document.getHtml().childElement("head"));
//            html_body_fingerprint = ExtractFingerprint.handleHtmlBody(document.getHtml().childElement("body"));
//        }
//        int length = request_fingerprint.length + response_fingerprint.length + html_head_fingerprint.length + html_body_fingerprint.length;
//        fingerprint = new ByteBuilder(length);
//        fingerprint.write(request_fingerprint);
//        fingerprint.write(response_fingerprint);
//        fingerprint.write(html_head_fingerprint);
//        fingerprint.write(html_body_fingerprint);
//
////        Fingerprint fp =  new Fingerprint();
////        fp.setLastUpdate(new Timestamp(System.currentTimeMillis()));
////
////        fp.setFpdata(fingerprint.getBytes());
//    }

    @Override
    public void run() {
        super.run();

    }
}
