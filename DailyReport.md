# 日报
## 2020/10/28
尝试系统主动获取部分，即爬虫程序，两种获取方式（HttpConnection，HTTPClient，Jsoup）
，内网正常访问获取数据，尝试访问外网(google.com)失败，系统显示java.net连接失败，浏览器正常访问.

HttpConnection获取网页数据93ms，未进行解析

Jsoup爬取加解析整个网页(baidu.com)历时2s977ms 

HttpClient 弃用