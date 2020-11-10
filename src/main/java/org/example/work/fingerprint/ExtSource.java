package org.example.work.fingerprint;

public enum ExtSource {
    REQUEST_HEADER(0L),
    RESPONSE_HEADER(1L),    // 指纹、特征词
    HTML_HEAD(2L),          // 指纹、特征词
    HTML_BODY(3L),          // 指纹

    LINK_TAG_URL(3L),
    STYLESHEET_EMBED(4L),
    JAVASCRIPT_IN_HEAD(5L),
    JAVASCRIPT_IN_BODY(6L),
    HYPERLINK(7L),
    HTML_BODY_TREE_PATH(8L),
    HTML_BODY_TREE_LEVEL(9L),
    HTML_IMG(10L),
    HTML_FORM(11L),
    //
    ;

    private long id;
    ExtSource(long id){
        this.id = id;
    }
    public long id(){
        return id;
    }
}
