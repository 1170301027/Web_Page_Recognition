package org.example.work.main;

import org.example.kit.entity.ByteArray;
import org.example.sql.conn.ConnectToMySql;
import org.example.work.parse.Parser;
import org.example.work.parse.nodes.Document;
import org.example.work.parse.nodes.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @Classname Before
 * @Description 预处理，处理指纹提取和特征词提取之前的必要变量，主要负责网页解析相关的配置。
 * @Date 2020/11/14 18:08
 * @Created by shuaif
 */
public class Before {
    private Document document;
    private Parser parser;
    private final ByteArray html_source;
    private int max_parse_depth;
    private String url;
    private ConnectToMySql sql;

    private List<Node> resources = new ArrayList<>();                 // 网页引用的资源：CSS文件、JavaScript文件、图片、音频、视频等等，iframe需要特殊分析
    private List<Node> hyper_links = new ArrayList<>(25); // 网页中的超链接：开头为 "http://"、"https://"、"/"、"../"、"//"等
    private List<Node> forms = new ArrayList<>(4);       // 网页中的表单

    public Document getDocument() {
        return document;
    }

    public Parser getParser() {
        return parser;
    }

    public ByteArray getHtml_source() {
        return html_source;
    }

    public int getMax_parse_depth() {
        return max_parse_depth;
    }

    public List<Node> getResources() {
        return resources;
    }

    public List<Node> getHyper_links() {
        return hyper_links;
    }

    public List<Node> getForms() {
        return forms;
    }

    public Before(ByteArray html_source, String url) {
        this.html_source = html_source;
        this.url = url;
        parseHTML();
        this.sql = new ConnectToMySql();
    }

    /**
     * 网页解析期间根据标签做出相应的动作
     */
    private void parseHTML(){
        html_source.handleUnicodeIdentifier();
        parser = new Parser(html_source);

        parser.addAction("a", hyper_links::add);
        parser.addAction("link", hyper_links::add);

        parser.addAction("form", forms::add);

        Consumer<Node> resAct = resources::add;

        parser.addAction("img", resAct);
        parser.addAction("script", a -> {
            resources.add(a);
        });

        document = parser.parse();
        max_parse_depth = parser.getMaxParsingDepth();
    }

}
