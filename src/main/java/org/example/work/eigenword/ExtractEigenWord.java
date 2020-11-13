package org.example.work.eigenword;

import org.example.kit.entity.ByteArray;
import org.example.kit.io.ByteBuilder;
import org.example.kit.security.MD5;
import org.example.work.parse.nodes.Element;
import org.example.work.parse.nodes.Node;
import sun.rmi.runtime.Log;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * @Classname ExtractEigenWord
 * @Description 提取网页特征词相关
 * @Date 2020/11/11 16:10
 * @Created by shuaif
 */
public class ExtractEigenWord {

    /**
     * 计算子序列的hash值 基于MD5设计
     * @param arr
     * @return
     */
    public static long hashTo60Bits(byte[] arr) {
        long hash = 0;
        MD5 md5 = new MD5();
        md5.update(arr,0,arr.length);
        byte[] hash_bytes = md5.digest();
        int half_length = hash_bytes.length / 2;
        for (int i = 0; i < half_length ; i++) {
            hash = hash | (hash_bytes[i] ^ hash_bytes[i + half_length]);
            hash = hash << 8;
        }
        hash = hash ^ ((hash >> 60 ) << 56);
        return hash;
    }

    /**
     * Q-gram算法，针对线性指纹序列，提取特征词，
     * @param s
     * @param tag
     * @return
     */
    public static List<Long> qGram(byte[] s, long tag) {
        if (s.length < 8) {
            return new ArrayList<>();
        }
        int win_size,step; // 定义滑动窗口的大小和窗口移动步长
        if (s.length < 14) {
            step = 3;
            win_size = 10;
        } else {
            step = 4;
            win_size = 12;
        }
        List<Long> words = new ArrayList<>();
        int start_index = --step;
        byte[] son_seq = null;
        for (int i = (s.length - win_size) / step; i >= 0; i--) {
            start_index += step;
            son_seq = new ByteArray(s,start_index,start_index + win_size - 1).getBytes();
            words.add(hashTo60Bits(son_seq) ^ (tag << 60));
        }
        if (start_index + win_size < s.length) {
            son_seq = new ByteArray(s, s.length - win_size,s.length - 1).getBytes();
            words.add(hashTo60Bits(son_seq) ^ (tag << 60));
        }
        return words;
    }

    /**
     * 提取线性指纹序列的特征词
     * @param linear_seq 线性指纹序列
     * @return 特征词列表
     */
    public static List<Long> getLinearFingerprintEigenWord(byte[] linear_seq) {
        return null;
    }

    /**
     * 提取HTML 网页body部分的特征词，
     * 由于body树部分的指纹不是线性序列，因此此处的特征词提取从两个方面进行，层次和路径
     * 对于每一层的节点，提取前n个节点的指纹序列进行特征词（在层次指纹提取的过程中进行特征词提取 in ExtractFingerprint）
     * 对于最后一层节点，选取六条指纹不同的路径，对每一条路径指纹序列，进行特征词的提取。
     * @param html_body body树的根节点
     * @param leaf_nodes 最大解析层的全部节点
     * @param max_parse_depth 最大解析深度
     * @return 特征词序列
     */
    public static List<Long> getBodyTreeEigenWord(Element html_body, Node[] leaf_nodes, int max_parse_depth) {
        if (leaf_nodes == null || leaf_nodes.length == 0) {
            for(int i = max_parse_depth - 1; i > 3; i--){
                leaf_nodes = getNodesByLevel(html_body, i);
                if(leaf_nodes != null && leaf_nodes.length > 0)
                    break;
            }
            if(leaf_nodes == null || leaf_nodes.length == 0)
                return null;
        }

        Node cur_parent = null; // 父节点，用于判断是否是一个节点的子节点
        int count = 0;
        for (Node node : leaf_nodes) { // 删除指纹重复的兄弟节点
            if (node.getParent() == cur_parent) {
                continue;
            }
            cur_parent = node.getParent();
            ByteBuilder builder = new ByteBuilder((node.getDepth() - html_body.getDepth()) * 3);
            while (node.getDepth() > html_body.getDepth()) {
                int hash = node.hashCode();
                builder.write(hash >> 16);
                builder.write(hash >> 8);
                if ((hash & 0xFE) != 0) {
                    builder.write(hash);
                }
                node = node.getParent();
            }

            count++;
            if (count >= 6)
                break;
        }
        // 选取的路径阈值为6
        if (leaf_nodes.length <= 6)
            return getPathWords(html_body,leaf_nodes,max_parse_depth);
        return null;
    }

    /**
     * 获取一条路径上面的指纹的特征词列表
     * @param html_body body 元素
     * @param leaf_nodes 叶子节点列表
     * @param max_parse_depth 最大解析深度
     * @return
     */
    private static List<Long> getPathWords(Element html_body, Node[] leaf_nodes, int max_parse_depth) {
        List<Long> words = new ArrayList<>();
        for (Node leaf_node : leaf_nodes) {
            ByteBuilder ser = new ByteBuilder(1024);
            int index = 0;
            while (!leaf_node.getParent().equals(html_body)) {
                ser.write(leaf_node.toFpString().getBytes());
                index += leaf_node.toFpString().getBytes().length;
                leaf_node = leaf_node.getParent();
            }
            long word = hashTo60Bits(ser.subBytes(0,index));
        }
        return words;
    }

    /**
     * 获取指定层的全部节点
     * @param root 树的根节点
     * @param level 层数
     */
    private static Node[] getNodesByLevel(Element root, int level) {
        return null;
    }
}
