package org.example.work.match;


/**
 * @Classname MatchResult
 * @Description 匹配结果
 * @Date 2021/3/3 18:52
 * @Created by shuaif
 */
public class MatchResult {
    private MatchTask target;
    private boolean success;

    private int webPageId; // 识别成功网页ID

    public MatchTask getTarget() {
        return target;
    }

    public void setTarget(MatchTask target) {
        this.target = target;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setWebPageId(Integer pageId) {
    }
}
