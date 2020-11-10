package org.example.work.main;

import org.apache.commons.collections.set.SynchronizedSet;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @Classname MyThread
 * @Description
 * @Date 2020/11/10 20:26
 * @Created by shuaif
 */
public class MyThread extends Thread{
    private final int serial_number;
    private final String website;
    private Set<String> urls;

    public MyThread(int serial_number, String website) {
        this.serial_number = serial_number;
        this.website = website;
        this.urls = new CopyOnWriteArraySet<>();
    }

    @Override
    public void run() {
        super.run();

    }
}
