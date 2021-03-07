package org.example.sql.mapper;

import org.apache.ibatis.annotations.Param;
import org.example.sql.pojo.*;

import java.util.List;

public interface MatchMapper extends BaseMapper {

    List<IndexResult> getCandidateSetByWords(@Param("list") List<Long> words, @Param("threshold") Integer threshold);

    List<Fingerprint> selectFingerprintsByPageIds(@Param("list") List<Integer> pageIds);

    String selectHostByIp(@Param("ip") String ip);

    String selectUrlByPageID(@Param("id") int pageId);

    void insertFingerprints(@Param("list") List<Fingerprint> fps);

    void insertFeatureWords(@Param("list") List<InvertedIndex> fps);

    void insertIptoHost(@Param("list") List<IptoHost> ips);

    void insertPagetoUrl(@Param("list") List<PagetoUrl> pagetoUrls);
}
