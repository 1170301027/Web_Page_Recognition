package org.example.sql.mapper;

import org.apache.ibatis.annotations.Param;
import org.example.sql.pojo.Fingerprint;
import org.example.sql.pojo.IndexResult;
import org.example.sql.pojo.InvertedIndex;

import java.util.List;

public interface MatchMapper extends BaseMapper {

    List<IndexResult> getCandidateSetByWords(@Param("list") List<Long> words, @Param("threshold") Integer threshold);

    List<Fingerprint> selectFingerprintsByPageIds(@Param("list") List<Integer> pageIds);

    void insertFingerprints(@Param("list") List<Fingerprint> fps);

    void insertFeatureWords(@Param("list") List<InvertedIndex> fps);
}
