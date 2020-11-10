package org.example.work.main;

import org.example.work.crawl.WebCrawl;
import org.example.work.parse.Parser;
import org.example.work.parse.nodes.Document;

import java.io.IOException;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @Classname Cache
 * @Description 线程池
 * @Date 2020/11/10 21:47
 * @Created by shuaif
 */
public class Cache {
    private int current_thread_pool_size; // 当前线程池的大小，判断是否可以新建线程
    private final int threshold; // 线程池的阈值
    private Set<MyThread> threads = new CopyOnWriteArraySet<>();
    private Queue<String> urls_to_crawl = new ConcurrentLinkedQueue<>(); // 待爬取队列
    private int serial_number = 0;

    private final String website;
    private final String host_url;

    public Cache(int threshold, String website) {
        this.threshold = threshold;
        this.website = website;
        this.host_url = "http://"+this.website+"/";
        init();
    }

    private void init() {
        try {
            Parser parser = new Parser((Objects.requireNonNull(WebCrawl.getHttpPacketLoadedWithHTML(this.host_url))).second());
            Document document = parser.parse();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void run() {
        while ()
    }
}
