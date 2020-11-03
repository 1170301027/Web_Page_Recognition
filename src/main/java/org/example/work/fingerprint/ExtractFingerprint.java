package org.example.work.fingerprint;

import org.example.auxiliary.Keys;
import org.example.kit.entity.ByteArray;

import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Classname ExtractFingerprint
 * @Description 提取指纹
 * @Date 2020/11/2 19:36
 * @Created by shuaif
 */
public class ExtractFingerprint {
    private static final int REQUEST_TAG = 0;
    private static final int RESPONSE_TAG = 1;
    private static final int HTML_HEAD_TAG = 2;
    private static final int HTML_BODY_TAG = 3;
    /**
     * BKDR算法实现，将字节数组映射为一个一个字节
     * @param byteArray 字节数组对应的字符串
     * @return 字节
     */
    private static byte hashTo1Byte(String byteArray){
        int hash = 1;
        for (byte b : byteArray.getBytes()) {
            hash = 31 * hash + b;
        }
        hash ^= hash >> 16;
        hash ^= hash >> 8;
        return (byte)hash;
    }

    /**
     * 构造指纹：指纹头（4bits标志位，12bits指纹体长度） + 指纹体
     * @param choice 标志位（0,1,2,3）
     * @param origin_fingerprint 原始指纹
     * @return 指纹
     */
    public static byte[] constructFingerprint(int choice,byte[] origin_fingerprint){
        byte flag = (byte)(choice << 4);
        byte length;
        if (origin_fingerprint.length < 0xFF) {
            length = (byte) (origin_fingerprint.length);
        } else {
            length = (byte) 0xFF;
            flag = (byte) (flag ^ (origin_fingerprint.length >> 8));
        }
        byte[] result = new byte[origin_fingerprint.length + 2];
        result[0] = flag;
        result[1] = length;
        System.arraycopy(origin_fingerprint,0,result,2,origin_fingerprint.length);
        return result;


    }

    /**
     * 提取请求报头部指纹，主要针对cookie名字做处理
     * @param cookie 请求头部的COOKIE字段值
     */
    public static byte[] handleRequestHeader(String cookie){
        //TODO 爬虫获取网页响应的时候并没有提供cookie怎么获取。
        byte[] result;
        String[] key_value = cookie.split(";");
        result = new byte[key_value.length];
        int i = 0;
        for (String key : key_value) {
            result[i] = hashTo1Byte(key);
            i++;
        }
        return constructFingerprint(REQUEST_TAG,result);
    }

    /**
     * 提取响应报文头部指纹，针对响应头部的键值对，对需要提取指纹的部分（包括key和value做指纹提取
     * @param response_header 请求头部键值对
     */
    public static byte[] handleResponseHeader(String response_header){
        // 头部字段处理
        List<String> all_keys_list = new ArrayList<>(Arrays.asList(Keys.RESPONSE_HEADERS));
        List<String> value_used_list = new ArrayList<>(Arrays.asList(Keys.RESPOSE_VALUE_USED));
        List<String> key_not_used_list = new ArrayList<>(Arrays.asList(Keys.RESPONSE_KEY_NOT_USED));

        // 获取头部键值对，用“：”分割
        byte[] result = new byte[1024];
        int i = 0; // index索引

        String[] key_value = response_header.split("\r\n");
        for (String s : key_value) {
            if (!s.contains("HTTP/")) {
                String[] split = s.split(":");
                String key = split[0];
                String value = split[1];
                if (all_keys_list.contains(key)) {
                    if (!key_not_used_list.contains(key)) { // 所有不提取key字段的也不提取value字段
                        result[i++] = hashTo1Byte(key);
                        if (value_used_list.contains(key)) { // 判断是否需要提取首部值
                            result[i++] = hashTo1Byte(value);
                        }
                    }
                } else { // 扩展首部使用key
                    result[i++] = hashTo1Byte(key);
                }
            }
        }
        return constructFingerprint(RESPONSE_TAG,new ByteArray(result,0,i).getBytes());
    }

    /**
     * 获取网页head部分的指纹，html_head部分类似于request_header部分的键值对
     * @param html_head head部分的DOM
     */
    public static byte[] handleHtmlHeader(String html_head){
        return null;

    }

    public static byte[] handleHtmlBody(String html_body){
        return null;
    }
}
