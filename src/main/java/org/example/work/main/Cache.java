package org.example.work.main;

import org.example.auxiliary.FilePath;
import org.example.kit.FileKit;

import java.io.IOException;
import java.util.*;
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
    private int serial_number = 0;

    private final List<String> websites;
    public Cache(int threshold) {
        this.current_thread_pool_size = 0;
        this.threshold = threshold;
        this.websites = new ArrayList<>();
    }

    public void doParse() {
        List<String> websites = new ArrayList<>();
        try {
            List<String> all_lines = FileKit.getAllLines(FilePath.ALL_WEBSITE);
            for (String line : all_lines) {
                websites.add(line.split(",")[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void run() {

    }
}
