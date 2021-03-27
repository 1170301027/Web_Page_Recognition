package org.example.work.match;

import org.example.sql.pojo.Fingerprint;
import org.example.sql.pojo.InvertedIndex;
import org.example.work.eigenword.EigenWord;

import java.util.List;

/**
 * @Classname PageRecord
 * @Description 网页记录
 * @Date 2021/2/28 11:56
 * @Created by shuaif
 */
public class PageRecord {
    private int pageID;
    private Fingerprint fp;
    private List<InvertedIndex> words;

    public void setPageID(int pageID) {
        this.pageID = pageID;
    }

    public void setFp(Fingerprint fp) {
        this.fp = fp;
    }

    public void setWords(List<InvertedIndex> words) {
        this.words = words;
    }

    public int getPageID() {
        return pageID;
    }

    public Fingerprint getFp() {
        return fp;
    }

    public List<InvertedIndex> getWords() {
        return words;
    }

    public int getCount() {
        return this.words.size();
    }
}
