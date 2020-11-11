package org.example.work.main;

import org.example.auxiliary.FilePath;
import org.example.kit.FileKit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Classname Main
 * @Description 主程序入口
 * @Date 2020/11/9 21:31
 * @Created by shuaif
 */
public class Main {

    public static void doParse() {
        List<String> websites = new ArrayList<>();
        try {
            List<String> all_lines = FileKit.getAllLines(FilePath.ALL_WEBSITE);
            for (String line : all_lines) {
                 websites.add(line.split(",")[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String website : websites) {
            Cache cache = new Cache(12,website);
            cache.run();
        }
    }

    public static void program1() {
        // TODO 第一个程序
    }

    public static void program2() {
        // TODO 第二程程序
    }

    public static void main(String[] args) {
        program1();
        //program2();
    }
}
