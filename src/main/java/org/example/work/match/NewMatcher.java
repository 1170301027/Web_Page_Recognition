package org.example.work.match;

import org.example.sql.conn.ConnectToMySql;
import org.example.sql.pojo.Fingerprint;
import org.example.sql.pojo.InvertedIndex;

import java.util.ArrayList;
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
    private static final List<List<InvertedIndex>> PAGE_WORDS = new ArrayList<>();

    static {
    }
    @Override
    public MatchResult match(MatchTask identifiedPage) {
        return null;
    }
}
