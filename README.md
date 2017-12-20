###功能点：
 - 使用高德地图实现地理围栏功能
 - 可设置手机号和短信间隔，设置后间隔发送当前位置
 - 可地图选点设置地理围栏
 - 离开进入地理围栏会有相应短信提醒（设置手机号并获取权限后）

[![zbMon.md.png](https://s1.ax2x.com/2017/12/20/zbMon.md.png)](https://simimg.com/i/zbMon)
[![zbdB3.md.png](https://s1.ax2x.com/2017/12/20/zbdB3.md.png)](https://simimg.com/i/zbdB3)
[![zb4za.md.png](https://s1.ax2x.com/2017/12/20/zb4za.md.png)](https://simimg.com/i/zb4za)
[![zbbdE.md.png](https://s1.ax2x.com/2017/12/20/zbbdE.md.png)](https://simimg.com/i/zbbdE)
[![zbG1G.md.png](https://s1.ax2x.com/2017/12/20/zbG1G.md.png)](https://simimg.com/i/zbG1G)

###注意：
如果下载代码后运行地图没有显示，请在[高德开放平台](http://lbs.amap.com/)注册帐号并申请应用后替换项目中清单文件**AndroidManifest.xml**高德地图的key重新打包运行即可；

```
<meta-data
            android:name="com.amap.api.v2.apikey"
            android:value="替换成自己申请的key" />
```
