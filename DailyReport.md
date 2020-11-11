# 日报
## 2020/10/28
尝试系统主动获取部分，即爬虫程序，两种获取方式（HttpConnection，HTTPClient，Jsoup）
，内网正常访问获取数据，尝试访问外网(google.com)失败，系统显示java.net连接失败，浏览器正常访问.

HttpConnection获取网页数据93ms，未进行解析

Jsoup爬取加解析整个网页(baidu.com)历时2s977ms 

HttpClient 弃用

## 2020/10/29
尝试python爬虫简单爬取外网网页，使用代理的情况下成功获取google.com的响应，但是可能遇到反爬机制

Java设置系统代理也成功，但是会不会封ip我还不知道，爬虫阶段暂时还未想好如何获取一个网站相关的所有URL。

`
System.setProperty("http.proxyHost","127.0.0.1");
System.setProperty("http.proxyPort","10809");
`

## 2020/11/3
针对请求报文头部和响应报文头部提取网页指纹，解析部分还没搞清楚具体怎么设计DOM树存储结构，

## 2020/11/10
初步完成网页解析和网页HEAD和网页BODY部分的指纹提取，未进行测试，在研究并发编程和网站URL的提取，

## 2020/11/11
完成指纹构造阶段的调度，尝试理解并使用线程池，