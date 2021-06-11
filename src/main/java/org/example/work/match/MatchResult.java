package org.example.work.match;


/**
 * @Classname MatchResult
 * @Description 匹配结果
 * @Date 2021/3/3 18:52
 * @Created by shuaif
 */
public class MatchResult {
    private int id;
    private MatchTask target;
    private String url;
    private boolean success;
    private double sim;

    private int page_id; // 识别成功网页ID
    private PageRecord webPage;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public MatchTask getTarget() {
        return target;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPage_id(int page_id) {
        this.page_id = page_id;
    }

    public void setTarget(MatchTask target) {
        this.target = target;
        this.url  = target.getHost() + "/" + target.getPath();
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setPage_id(Integer pageId) {
        this.page_id = pageId;
    }
    public int getPage_id() {
        return page_id;
    }

    public void setWebPageId(int webPageId) {
        this.page_id = webPageId;
    }

    public PageRecord getWebPage() {
        return webPage;
    }

    public void setWebPage(PageRecord webPage) {
        this.webPage = webPage;
    }

    public double getSim() {
        return sim;
    }

    public void setSim(double sim) {
        this.sim = sim;
    }
}
