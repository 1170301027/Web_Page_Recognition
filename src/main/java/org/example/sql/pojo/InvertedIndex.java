package org.example.sql.pojo;


import org.example.sql.annotation.Table;

import java.io.Serializable;

@Table
public class InvertedIndex implements Serializable {

    private static final long serialVersionUID = -1L;

    private long word;
    private Integer pageId;
    private int index;
    private int frequency;

    public long getWord(){
        return word;
    }
    public void setWord(long word){
        this.word = word;
    }
    public Integer getPageId(){
        return pageId;
    }
    public void setPageId(Integer pageId){
        this.pageId = pageId;
    }
    public int getIndex(){
        return index;
    }
    public void setIndex(int index){
        this.index = index;
    }
    public int getFrequency(){
        return frequency;
    }
    public void setFrequency(int frequency){
        this.frequency = frequency;
    }
}
