package org.example.work.fingerprint;


import org.example.kit.entity.ByteArray;
import org.example.kit.security.MD5;

import java.util.function.Consumer;

class ExtUtils {

    /**
     * 根据原序列长度，自适应地使用 7-gram/8-gram 提取特征词，
     * 当序列长度较小时，忽略特征词提取
     *
     * 当 length 大于 7 时，可提取的子序列数量为：
     * <pre>
     *        1 + (length - winSize) / step + ((length - winSize) % step == 0 ? 0 : 1)
     * </pre>
     *
     * @param length   序列长度
     * @param consumer 根据子序列做出相应的操作
     */
    static void qGram(int length, Consumer<int[]> consumer){
        if(length < 8)
            return;
        int winSize, step; // 滑动窗口大小和窗口移动步长
        if(length < 14){
            step = 3;
            winSize = 10;
        }else{
            step = 4;
            winSize = 12;
        }
        int startIndex = -step;
        for(int i = (length - winSize) / step; i >= 0; i--){
            startIndex += step;
            consumer.accept(new int[]{startIndex, startIndex + winSize});
        }
        if(startIndex + winSize < length)
            consumer.accept(new int[]{length - winSize, length});
    }

    static int hash(ByteArray data){
        if(data == null)
            return 0;
        return data.hashCode();
    }

    static byte byteHash(byte[] data){
        if(data == null)
            return 0;
        int result = 1;
        for(byte element : data)
            result = 31 * result + element;
        result ^= result >>> 16;
        result ^= result >>> 8;
        return (byte)result;
    }

    static byte byteHash(ByteArray data){
        if(data == null)
            return 0;
        int hash = data.hashCode();
        hash ^= hash >>> 16;
        hash ^= hash >>> 8;
        return (byte)hash;
    }

    static byte byteHash(long longHash){
        longHash ^= longHash >>> 32;
        longHash ^= longHash >>> 16;
        longHash ^= longHash >>> 8;
        return (byte)longHash;
    }

    static byte byteHash(int hash){
        hash ^= hash >>> 16;
        hash ^= hash >>> 8;
        return (byte)hash;
    }

    static long longHash(byte[] a){
        return longHash(a, 0, a.length);
    }

    static long longHash(ByteArray a){
        if(a == null)
            return 0;
        return longHash(a.getParentBytes(), a.from(), a.length());
    }

    static long setFeatureWordTag(long word, ExtSource source){
        return (word & (-1L >>> 4)) | (source.id() << 60);
    }

    static short setFpHead(ExtSource head, int length){
        if(length < 0 || length > 2 << 12 - 1)
            throw new IllegalArgumentException("指纹长度[" + length + "]非法");
        return (short)((head.id() << 12) | length);
    }

    static long urlLongHash(ByteArray url, byte[] host){
        int index = url.indexOf(host);
        if(index < 0)
            return longHash(url.getParentBytes(), url.from(), url.length());
        byte[] newURL = url.replace(host, new byte[]{});
        return longHash(newURL, 0, newURL.length);
    }

    static long longHash(byte[] bytes, int offset, int length){
        MD5 md5 = new MD5();
        md5.update(bytes, offset, length);
        return md5.digestLong();
    }

    static long intHash(byte[] bytes, int offset, int length){
        MD5 md5 = new MD5();
        md5.update(bytes, offset, length);
        return md5.digestInt();
    }

    static long byteHash(byte[] bytes, int offset, int length){
        return byteHash(intHash(bytes, offset, length));
    }
}
