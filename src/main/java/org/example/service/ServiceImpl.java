package org.example.service;

import org.apache.ibatis.annotations.Result;
import org.example.result.RestResult;
import org.example.sql.conn.ConnectToMySql;
import org.example.sql.mapper.MatchMapper;
import org.example.sql.pojo.Fingerprint;
import org.example.sql.pojo.InvertedIndex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @CLassname ServiceImpl
 * @Description TODO
 * @Date 2021/5/27 11:53
 * @Created by lenovo
 */
@Service
public class ServiceImpl {
//    @Resource
//    private MatchMapper matchMapper = null;
    private ConnectToMySql conn = new ConnectToMySql();
    private MatchMapper matchMapper = conn.getMatchMapper();

    public RestResult details(int page_id) {
        Fingerprint fp = this.matchMapper.selectFingetprintByPageId(page_id);
        List<InvertedIndex> words = this.matchMapper.selectFeatureWordsByPageID(page_id);
        RestResult rest = new RestResult();
        rest.setFingerprint(fp);
        rest.setContent(fp.getFpdata());
        rest.setUrl(this.matchMapper.selectUrlByPageID(page_id));
        rest.setWords(words);
        return rest;
    }
}
