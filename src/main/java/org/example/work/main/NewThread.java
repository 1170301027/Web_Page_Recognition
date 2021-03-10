package org.example.work.main;

/**
 * @CLassname NewThread
 * @Description TODO
 * @Date 2021/3/10 10:29
 * @Created by lenovo
 */
public class NewThread extends MyThread{
    private String url;
    private int serial;
    private int base = 1000500;
    public NewThread(String url,int serial) {
        this.url = url;
        this.serial = serial;
    }
    @Override
    public void run() {
        long start = System.currentTimeMillis();
        crawl_new(url, base + serial);
        long end = System.currentTimeMillis();
        System.out.println("serial = " + serial + ", timespan = " + (end - start) + ", url = " + url);
    }
}
