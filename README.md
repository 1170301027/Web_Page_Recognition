# Web_Page_Recognition
网页识别系统构建

## 介绍
该项目为maven项目，IDE为Intellij IDEA
系统实现，爬取互联网上的排名靠前的网站的所有有效网页，进行网页解析与指纹提取，建立指纹和特征库用于网页相似度的比较

## 项目说明
项目源码存放于src.main.java.org.example文件夹下
auxiliary包存储辅助文件，类似文件存储路径，网页指纹提取相关键值
data包存放原始数据，Alexa网站排名
kit包为工具包，存储一些相关工具类（非本人）
sql包存储Mybatis相关配置，用于连接数据库
work包存储网页爬虫，预处理，网页解析，指纹提取，特征提取，网页快照等相关类

## 程序运行逻辑
从文件中读取要爬取的网站host，构造初始URL并创建线程（MyThread），执行网页爬取（WebCrawl）和网页预处理（Before，包括解析网页获取DOM树以及获取网页上的超链接），
对预处理结果进行指纹（ExtractFingerprint）和特征词的提取（ExtractEigenword）



