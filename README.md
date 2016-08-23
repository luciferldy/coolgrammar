__项目已经不再维护，百度翻译的接口和SAE都已经改版，下载题库和在线翻译功能不能使用__

酷爽英语语法
===========

### 简介

一款英语初级语法学习APP，带有题库，支持网络题库下载。

[百度手机助手下载地址](http://shouji.baidu.com/soft/item?docid=7201034&from=landing&f=search_app_%E9%85%B7%E7%88%BD%E8%AF%AD%E6%B3%95%40list_1_title%401%40header_all_input)

[最新版本下载地址](https://gitcafe.com/MaybeMercy/AppLibrary/raw/master/Coolgrammer.apk)

### 依赖包

* android-support-v7-appcompat.jar
* android-support-v4-appcompat.jar
* [PagerSlidingTabStrip](https://github.com/astuetz/PagerSlidingTabStrip)

### 截图

主界面

![github](screenshot/tab.png "主屏幕")

查询界面

![查询界面](screenshot/search.png "查询")

### 技术细节

* TAB切换使用开源框架[PagerSlidingTabStrip](https://github.com/astuetz/PagerSlidingTabStrip)
* 语法知识和题库用本地SQLite存储
* 单词查询和英汉互译使用[百度词典API](http://developer.baidu.com/wiki/index.php?title=%E5%B8%AE%E5%8A%A9%E6%96%87%E6%A1%A3%E9%A6%96%E9%A1%B5/%E7%99%BE%E5%BA%A6%E7%BF%BB%E8%AF%91/%E7%99%BE%E5%BA%A6%E8%AF%8D%E5%85%B8API%E4%BB%8B%E7%BB%8D)
* 服务器使用PHP写成，架设在SAE上
