package org.example;

import net.sf.json.JSONObject;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.example.auxiliary.FilePath;
import org.example.kit.FileKit;
import org.example.sql.mapper.MatchMapper;
import org.example.sql.pojo.*;
import org.example.work.main.MyThread;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Classname MyBatisTest
 * @Description
 * @Date 2020/11/15 11:38
 * @Created by shuaif
 */
public class MyBatisTest {
    private InputStream in ;
    private SqlSessionFactory factory;
    private SqlSession session;
    private MatchMapper matchMapper;

    @Before
    public void init() throws Exception{
        //1.读取配置文件
        in = Resources.getResourceAsStream("MybatisConfig.xml");
        //2.创建 SqlSessionFactory 的构建者对象
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        //3.使用构建者创建工厂对象 SqlSessionFactory
        factory = builder.build(in);
        //4.使用 SqlSessionFactory 生产 SqlSession 对象
        session = factory.openSession();
        //5.使用 SqlSession 创建 dao 接口的代理对象
        matchMapper = session.getMapper(MatchMapper.class);
    }

    @After // 在测试方法之哦户执行资源释放
    public void destroy() throws Exception{
        session.commit();
        //释放资源
        session.close();
        in.close();
    }

    @Test
    public void insertFingerprintTest() {
        Fingerprint fingerprint = new Fingerprint();
        fingerprint.setLastUpdate(new Timestamp(System.currentTimeMillis()));
        fingerprint.setPageId(1);
        fingerprint.setFpdata(new byte[]{});
        fingerprint.setSimilarity(1.0);
        matchMapper.insertFingerprints(Collections.singletonList(fingerprint));
    }

    @Test
    public void insertEigenword() {
        InvertedIndex eigenWord = new InvertedIndex();
        eigenWord.setIndex(0);
        eigenWord.setPageId(1);
        eigenWord.setFrequency(10);
        eigenWord.setWord(100000);
        matchMapper.insertFeatureWords(Collections.singletonList(eigenWord));
    }

    @Test
    public void insertPagetoUrl() {
        PagetoUrl pagetoUrl = new PagetoUrl();
        pagetoUrl.setPageId(0);
        pagetoUrl.setUrl("http://sdfsdfsdf.sdfsdf/");
        matchMapper.insertPagetoUrl(Collections.singletonList(pagetoUrl));
    }

    @Test
    public void getCandidateword() {
        int threshold = 5;
//        long long word = 2377900603251621120;
//        long[] words = { 2377900603251621120, 2377900603251620352,
//                6989586621670960384,
//                2377900603251613696,
//                2377900603251034368,
//                6989586621670960384,
//                2377900603248680704,
//                2377900603244552960,
//                2377900603244681216,
//                2377900601393890560,
//                6989586621670960384};
        List<Long> wordsTarget = new ArrayList<>();
//        wordsTarget.add()
        List<IndexResult> candidate = matchMapper.getCandidateSetByWords(wordsTarget, wordsTarget.size() > 2 ? wordsTarget.size() / 2 : null);
    }

    @Test
    public void readDataAndInsert() {
        List<JSONObject> jsonList = new ArrayList<>();
        String filePath = FilePath.ROOT_PATH + "index.data";
        FileKit.readPacket(jsonList,filePath,0,1);
        MyThread test = new MyThread();
        JSONObject jo = jsonList.get(0);
        String url = jo.getString("url");
        byte[] data = jo.getString("data").getBytes();
        test.doParseAndExtract(url,data,0);
    }

    @Test
    public void buildFpAndWordsLib() {
        MyThread myThread = new MyThread();
        myThread.buildFpAndWordsLib();
    }

    @Test
    public void buildFpAndWordsLib_new() {
        MyThread myThread = new MyThread();
        myThread.buildFpAndWordsLib_new();
    }


    @Test
    public void buildIptoHostLib() {
        MyThread myThread = new MyThread();
        myThread.buildIpAndHostLib();
    }

    @Test
    public void insertIptoHost() {
        IptoHost iptoHost = new IptoHost();
        iptoHost.setIp("0.0.0.1");
        iptoHost.setHost("www.shuai.com");
        matchMapper.insertIptoHost(iptoHost);
    }

    @Test
    public void findHostByIp() {
        String ip = "0.0.0.1";
        System.out.println(matchMapper.selectHostByIp(ip));
    }
}
