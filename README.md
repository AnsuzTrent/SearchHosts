# SearchHosts

#### 介绍
更加便捷地更改hosts 文件以访问类似于 "[Github](https://github.com)"、"[Python 官网]("https://www.python.org")" 这种比较难以访问的网站。

可以输入网址获取单个IP，也可以更新整个hosts 文件。

感谢：
- [站长之家](https://tool.chinaz.com)
- [站长之家(手机版)](https://mtool.chinaz.com)
- [IP138](https://site.ip138.com)
- [ToolFK](https://www.toolfk.com)

提供的搜索支持——尽管他们不知道我在这个程序里用了他们的网站。

如果想加速游戏软件，为什么不试试`UsbEAm Hosts Editor`呢？他们比我们专业的多，功能和界面也友好的多。

但请注意，使用本软件更新hosts后，`UsbEAm Hosts Editor`将检测不到其曾经写入的内容——它的注释我们给忽略了

#### 软件架构
有单例模式，没了

#### 安装教程
首先电脑上必须要有JRE 环境——Java 8做exe 文件挺麻烦的，或者是我们不会做

启动时请使用`run.bat`文件，目前仍不支持Linux

请注意`Hosts.jar`文件同级目录下最好要有`rules.json` 文件，否则只会使用内置的站长之家PC 版进行搜索，而且必须要有`lib`目录——那里面是本软件用到的依赖，显而易见的，我们把依赖包放在了外面以减少体积。

#### 使用说明
新增规则请在rules.json 文件中增加，需要的信息有：
- 网站名称
- get 提交方式的网址（用"`${website}`"代替要查询的网站）
- 可被识别的CSS选择器
- 去除多余字符的正则式（可以没有值，但必须有这个属性）

一个例子如下：
``` json
{
    "name": "站长之家PC 版",
    "url": "http://tool.chinaz.com/dns/?type=1&host=${website}&ip=",
    "cssQuery": "div.w60-0.tl",
    "replaceRegex": "(\[(.+?)]|-)"
}
```

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