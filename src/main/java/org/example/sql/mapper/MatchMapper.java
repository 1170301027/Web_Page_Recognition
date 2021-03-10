package org.example.sql.mapper;

import org.apache.ibatis.annotations.Param;
import org.example.sql.pojo.*;
import org.example.work.eigenword.EigenWord;

import java.util.List;

public interface MatchMapper extends BaseMapper {

    List<IndexResult> getCandidateSetByWords(@Param("list") List<Long> words, @Param("threshold") Integer threshold);

    List<Fingerprint> selectFingerprintsByPageIds(@Param("list") List<Integer> pageIds);

    List<InvertedIndex> selectFeatureWordsByPageID(int page_id);

    String selectHostByIp(@Param("ip") String ip);

    String selectUrlByPageID(@Param("id") int pageId);

    void insertFingerprints(@Param("list") List<Fingerprint> fps);

    void insertFeatureWords(@Param("list") List<InvertedIndex> fps);

    void insertIptoHost(IptoHost iptoHost);

    void insertPagetoUrl(@Param("list") List<PagetoUrl> pagetoUrls);
}
