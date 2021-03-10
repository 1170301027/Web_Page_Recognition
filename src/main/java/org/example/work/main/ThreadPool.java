package org.example.work.main;

import org.example.auxiliary.FilePath;
import org.example.kit.FileKit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Classname ThreadPool
 * @Description 线程池
 * @Date 2020/11/10 21:47
 * @Created by shuaif
 */
public class ThreadPool {
    private int current_thread_pool_size; // 当前线程池的大小，判断是否可以新建线程
    private final int threshold; // 线程池的阈值
    private Set<MyThread> threads = new CopyOnWriteArraySet<>();
    private int serial_number = 0;
    private ExecutorService cache;

    private final List<String> websites;
    public ThreadPool(int threshold) {
        this.current_thread_pool_size = 0;
        this.threshold = threshold;
        this.websites = new ArrayList<>();
    }

    public void doParse() {
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
        doParse();
        System.out.println("完成读取website数据 :" + websites.size());
        int count = 0;
        serial_number = 50000;// 12500
        while (serial_number < websites.size()) {
            while (count < threshold && serial_number < websites.size()) {
                count++;
                System.out.println("当前count : " + count  + ", 创建新线程 ： " + serial_number);
                MyThread newThread = new MyThread(serial_number, websites.get(serial_number));
                this.threads.add(newThread);
                newThread.start();
                serial_number++;
            }
            try {
                Thread.sleep(2*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //检查活性
            for (MyThread thread : threads) {
                if (!thread.isAlive()) {
                    count--;
                    threads.remove(thread);
                }
            }
            if (serial_number > 800000) {
                break;
            }
        }
    }

    public void run2() {
        doParse();
        System.out.println("完成读取website数据 :" + websites.size());
        int count = 0;
        serial_number = 750000;// 12500
        while (serial_number < websites.size()) {
            while (count < threshold && serial_number < websites.size()) {
                count++;
                serial_number++;
                System.out.println("当前count : " + count  + ", 创建新线程 ： " + serial_number);
                MyThread newThread = new MyThread(serial_number, websites.get(serial_number));
                this.threads.add(newThread);
                newThread.start();
            }
            try {
                Thread.sleep(2*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //检查活性
            for (MyThread thread : threads) {
                if (!thread.isAlive()) {
                    count--;
                    threads.remove(thread);
                }
            }
            if (serial_number > 800000) {
                break;
            }
        }
    }
}
