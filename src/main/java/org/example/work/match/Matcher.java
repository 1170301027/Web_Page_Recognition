package org.example.work.match;

import org.aspectj.weaver.loadtime.Aj;
import org.example.kit.entity.ByteArray;
import org.example.sql.conn.ConnectToMySql;
import org.example.sql.mapper.MatchMapper;
import org.example.sql.pojo.Fingerprint;
import org.example.sql.pojo.IndexResult;
import org.example.sql.pojo.InvertedIndex;
import org.example.work.eigenword.EigenWord;
import sun.security.krb5.internal.PAEncTSEnc;

import java.util.*;

/**
 * @Classname Matcher
 * @Description 网页匹配
 * @Date 2021/3/2 18:16
 * @Created by shuaif
 */
public class Matcher {
    private List<EigenWord> candidateEigenWords; // 特征词候选集
    private ConnectToMySql conn = new ConnectToMySql();
    private MatchMapper matchMapper = conn.getMatchMapper();

    /**
     * 执行匹配。
     * @param identifiedPage -待识别网页
     * @return 反馈匹配结果
     */
    public MatchResult match(MatchTask identifiedPage) {
        List<Long> wordsTarget = new ArrayList<>();
        for (EigenWord eigenWord : identifiedPage.getEigenWords()) {
            wordsTarget.add(eigenWord.getWord());
        }
        // 倒排索引，根据特征词获取出现该词的网页
        // 查询完成后统计每个网页包含目标特征词的个数，并过滤掉次数在阈值(目标特征词个数的一半)及以下的网页
        List<IndexResult> candidate = matchMapper.getCandidateSetByWords(wordsTarget, wordsTarget.size() > 2 ? wordsTarget.size() / 2 : null);

        MatchResult matchResult = new MatchResult();
        matchResult.setTarget(identifiedPage);
        if (candidate.size() == 0) { // 无匹配项目，
            matchResult.setSuccess(false);
            return matchResult;
        }
        // 过滤候选网页集。
        filterByTargetWords(wordsTarget,candidate);
        filterByFeatureVector(Arrays.asList(identifiedPage.getEigenWords()),candidate);
        // 网页候选集按特征词数量排序
        Collections.sort(candidate);
        List<Integer> pageIds = new ArrayList<>();
        for (IndexResult indexResult : candidate) {
            pageIds.add(indexResult.getPageId());
        }
        List<Fingerprint> fps = matchMapper.selectFingerprintsByPageIds(pageIds);

        computeSimilarityAndSort(identifiedPage.getFingerprint(),fps);

        Fingerprint target = fps.get(0);
        if(target.getSimilarity() > 0.80){ // 阈值
            matchResult.setSuccess(true);
            matchResult.setWebPageId(target.getPageId());
        }

        return matchResult;
    }

    /**
     * （一轮过滤）根据目标特征词过滤网页候选集：根据网页候选集中目标特征词排序过滤掉明显不符合的网页
     * @param words_target -目标网页特征词列表
     * @param candidate_page - 候选网页集
     */
    private void filterByTargetWords(List<Long> words_target, List<IndexResult> candidate_page) {
        List<List<InvertedIndex>> candidate_words = new ArrayList<>();
        for (IndexResult indexResult : candidate_page) {
            int page_id = indexResult.getPageId();
            List<InvertedIndex> words = conn.getMatchMapper().selectFeatureWordsByPageID(page_id);
            // sort by target_word
            List<InvertedIndex> result_words = new ArrayList<>();
            for (Long target_word : words_target) {
                for (InvertedIndex candidate_word : words) {
                    if (candidate_word.getWord() == target_word) {
                        result_words.add(candidate_word);
                        break;
                    }
                }
            }
            // sort by index
            Collections.sort(result_words);
            candidate_words.add(result_words);
        }
        // 对每一组网页的特征向量和目标网页的特征向量进行相似度匹配。
        for (int i = 0; i < candidate_words.size(); i++) {
            int count = 0, maxEnd = 0; // 以POS j,a 结尾的最长公共子序列
            for () {
                // 二分查找maxEnd中第一个大于POS j,a 的元素，记录二分区间【left,right】

            }
            // 限定阈值，排除明显不符合的网页，
            if (count < (3 * count) / 4)
                candidate_page.remove(i);
        }
    }

    /**
     * （二轮过滤） 以词频做向量建立网页特征向量,计算网页候选集和 目标网页的特征向量之间的余弦相似度，选出相似度最大的网页，
     * @param target_words-目标网页特征向量。
     * @param candidate_page -网页候选集
     */
    private void filterByFeatureVector(List<EigenWord> target_words,List<IndexResult> candidate_page) {
        List<List<Integer>> candidate_vectors = new ArrayList<>();
        List<Integer> target_vector = new ArrayList<>();
        for (EigenWord target_word : target_words) {

            target_vector.add(target_word.getFrequency());
        }
        // 提取网页候选集的特征向量，
        for (IndexResult indexResult : candidate_page) {
            int page_id = indexResult.getPageId();
            List<InvertedIndex> words = conn.getMatchMapper().selectFeatureWordsByPageID(page_id);
            // sort by index
            Collections.sort(words);
            List<Integer> source_vector = new ArrayList<>();
            for (InvertedIndex word : words) {
                source_vector.add(word.getFrequency());
            }
            candidate_vectors.add(source_vector);
        }
        List<Double> page_sims = new ArrayList<>(); // 记录向量相似度值
        double max_sim = 0.0;
        // 特征向量相似度比较
        for (List<Integer> candidate_vector : candidate_vectors) {
            double sim = sim(candidate_vector,target_vector);
            if (max_sim < sim) max_sim = sim;
            page_sims.add(sim);
        }
        // 保留相似度最大的选项
        for (int i = 0; i < page_sims.size(); i++) {
            if (page_sims.get(i) < max_sim) {
                candidate_page.remove(i);
            }
        }
    }

    private double sim(List<Integer> source, List<Integer> target) {

    }

    /**
     * 获取网页指纹各部分指纹: [4bits tag][8bits length][content]
     * @param fingerprint - 网页指纹
     * @return part of FP
     */
    private List<ByteArray> getPartOfFingerprint(byte[] fingerprint) {
        List<ByteArray> result = new ArrayList<>();
        int index = 0;
        int count = 0;
        while (count < 3)  {
            count ++;
            byte first_byte = fingerprint[index];
            byte second_byte = fingerprint[index+1];
            int length = (first_byte ^ 0x0F) << 8 + second_byte ^ 0xFF;
            int begin = index + 1;
            int end = index + length + 1;
            result.add(new ByteArray(fingerprint, begin, end));
            index = end;
        }
        return result;
    }

    /**
     * 计算线性指纹相似度，LCS算法
     * @param source -
     * @param target -
     * @return 相似度
     */
    private double linerSimilarity(byte[] source, byte[] target) {
        int[][] c = new int[source.length][target.length];
        for (int i = 0; i < source.length; i++) {
            for (int j = 0; j < target.length; j++) {
                if (i == 0 || j == 0) c[i][j] = 0;
                else if (source[i] == target[j]) {
                    c[i][j] = c[i-1][j-1] + 1;
                } else {
                    c[i][j] = Math.max(c[i-1][j],c[i][j-1]);
                }
            }
        }
        return c[source.length - 1][target.length - 1]/(double)(Math.max(source.length,target.length));
    }
    /**
     * 计算指纹相似度并排序，
     * @param fingerprint -目标网页指纹
     * @param fps -网页指纹候选集
     */
    private void computeSimilarityAndSort(byte[] fingerprint, List<Fingerprint> fps) {
        List<ByteArray> target_fp_parts = getPartOfFingerprint(fingerprint);
        for (Fingerprint fp : fps) {
            List<ByteArray> source_fp_parts = getPartOfFingerprint(fp.getFpdata());
            double sim1,sim2,sim3;
            sim1 = linerSimilarity(source_fp_parts.get(0).getBytes(),target_fp_parts.get(0).getBytes());
            sim2 = linerSimilarity(source_fp_parts.get(1).getBytes(),target_fp_parts.get(1).getBytes());
            sim3 = linerSimilarity(source_fp_parts.get(2).getBytes(),target_fp_parts.get(2).getBytes());
            double sim = 0.5 * sim1 + 0.3 * sim2 + 0.2 * sim3;
            fp.setSimilarity(sim);
        }
        Collections.sort(fps);
        Collections.reverse(fps);
    }
}
