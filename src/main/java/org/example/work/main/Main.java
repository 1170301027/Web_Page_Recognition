package org.example.work.main;

/**
 * @Classname Main
 * @Description 主程序入口
 * @Date 2020/11/9 21:31
 * @Created by shuaif
 */
public class Main {

    private static void doCrawl() {
        ThreadPool threadPool = new ThreadPool(12);
        threadPool.run();
    }

    public static void doCrawl_1() {
        ThreadPool threadPool = new ThreadPool(12);
        threadPool.run_crawl_url_list();
    }

    public static void main(String[] args) {
//        doCrawl();
        doCrawl_1();
    }
}
