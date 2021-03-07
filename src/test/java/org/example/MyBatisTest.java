package org.example;

import net.sf.json.JSONObject;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.example.auxiliary.FilePath;
import org.example.kit.FileKit;
import org.example.kit.entity.ByteArray;
import org.example.kit.io.ByteBuilder;
import org.example.sql.mapper.MatchMapper;
import org.example.sql.pojo.Fingerprint;
import org.example.work.main.MyThread;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

import java.io.InputStream;
import java.net.URI;
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
    public void readDataAndInsert() {
        List<JSONObject> jsonList = new ArrayList<>();
        String filePath = FilePath.ROOT_PATH + "index.data";
        FileKit.readPacket(jsonList,filePath,0,1);
        MyThread test = new MyThread();
        JSONObject jo = jsonList.get(0);
        String url = jo.getString("url");
        byte[] data = jo.getString("data").getBytes();
        test.doParseAndExtract(url,data,110);
    }

    @Test
    public void buildFpAndWordsLib() {
        MyThread myThread = new MyThread();
        myThread.buildFpAndWordsLib();
    }
}
