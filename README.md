# SearchHosts

#### 介绍
更加便捷地更改hosts 文件以访问类似于 "[Github.com](https://github.com)"、"www.python.org" 的网站

感谢站长之家（[tool.chinaz.com/dns](https://tool.chinaz.com/dns)）提供的搜索接口支持——尽管他们不知道我在这个程序里用了他们的网站


#### 软件架构
java 项目

#### 安装教程
?

#### 使用说明
联网查询规则位于"org.apache.fraud.search.rules" 包
新增规则请继承"org.apache.fraud.search.base.BaseParser" ，并重写"getResult()"方法。
执行时请使用exec()方法

#### 参与贡献

1. Fork 本仓库
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request


#### 码云特技

1. 使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2. 码云官方博客 [blog.gitee.com](https://blog.gitee.com)
3. 你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解码云上的优秀开源项目
4. [GVP](https://gitee.com/gvp) 全称是码云最有价值开源项目，是码云综合评定出的优秀开源项目
5. 码云官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6. 码云封面人物是一档用来展示码云会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)