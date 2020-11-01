package org.example.mapper;

import com.staui.wpi.web.pojo.Fingerprint;
import com.staui.wpi.web.pojo.IndexResult;
import com.staui.wpi.web.pojo.InvertedIndex;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MatchMapper extends BaseMapper {

    List<IndexResult> getCandidateSetByWords(@Param("list") List<Long> words, @Param("threshold") Integer threshold);

    List<Fingerprint> selectFingerprintsByPageIds(@Param("list") List<Integer> pageIds);

    void insertFingerprints(@Param("list") List<Fingerprint> fps);

    void insertFeatureWords(@Param("list") List<InvertedIndex> fps);
}
