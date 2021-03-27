package org.example.work.match;

import org.aspectj.lang.annotation.Before;
import org.example.sql.conn.ConnectToMySql;
import org.example.sql.pojo.Fingerprint;
import org.example.sql.pojo.InvertedIndex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @CLassname NewMatcher
 * @Description TODO
 * @Date 2021/3/16 11:03
 * @Created by lenovo
 */
public class NewMatcher extends Matcher{
    private static final ConnectToMySql conn = new ConnectToMySql();
    private static final List<Fingerprint> ALL_FINGERPRINTS = conn.getMatchMapper().selectFingerprint();
    private static final List<InvertedIndex> ALL_WORDS = conn.getMatchMapper().selectFeatureWords();
    private static final List<PageRecord> pages = new ArrayList<>();

    static  {
        ALL_WORDS.sort(Comparator.comparingInt(InvertedIndex::getPageId));
        ALL_FINGERPRINTS.sort(Comparator.comparingInt(Fingerprint::getPageId));
        Collections.sort(ALL_FINGERPRINTS);
        for (int i = 0; i < ALL_FINGERPRINTS.size(); i++) {
            for (int j = 0; j < ALL_WORDS.size(); j++) {
                int page_id = ALL_FINGERPRINTS.get(i).getPageId();
                List<InvertedIndex> words = new ArrayList<>();
                if (ALL_WORDS.get(j).getPageId() != page_id) {
                    PageRecord pageRecord = new PageRecord();
                    pageRecord.setPageID(page_id);
                    pageRecord.setFp(ALL_FINGERPRINTS.get(i));
                    pageRecord.setWords(words);
                    pages.add(pageRecord);
                    words = new ArrayList<>();
                    i++;
                    page_id = ALL_FINGERPRINTS.get(i).getPageId();
                }
                words.add(ALL_WORDS.get(j));
            }
        }
    }


    @Override
    public MatchResult match(MatchTask identifiedPage) {
        return null;
    }
}
