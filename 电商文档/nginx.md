# nginx 反向代理(使用域名访问本地项目)

#### 1. 统一环境

​		我们现在访问页面使用的是：http://localhost:9001

​		有没有什么问题？

​		实际开发中，会有不同的环境：

​					  -开发环境：自己的电还好不是脑

​                      -测试环境：提供给测试人员使用的环境

​                      -预发布环境：数据是和生成环境的数据一致，运行最新的项目代码进去测试

​                      -生产环境：项目最终发布上线的环境

> ---------------------------------------------------------------------------------------------------------------------------

​	    如果不同环境使用不同的ip去访问，可能会那么，当我们在浏览器输入一个域名时，浏览器是如何找到对应服务的ip和端口的呢？一些问题。为了保证所有环境的一致，         	    我们会在各种环境下都使用域名来访问。

​		我们将使用以下域名：

​					-主域名是：www.mrshop.com，

​					-管理系统域名：manage.mrshop.com

​					-网关域名：api.mrshop.com

​					 ... ...

​		最终，我们希望这些域名指向的还是我们本机的某个端口。

​		那么，当我们在浏览器输入一个域名时，浏览器是如何找到对应服务的ip和端口的呢

#### 2. 域名解析

​		一个域名一定会被解析为一个或多个ip。这一般会包含两步：

​				⚑ 本地域名解析:

​						浏览器会首先在本机的hosts文件中查找域名映射的IP地址，如果查找到就返回IP ，没找到则进行

​						域名服务器解析，一般本地解析都会失败，因为默认这个文件是空的。

​								♡ Windows下的hosts文件地址：C:/Windows/System32/drivers/etc/hosts

​								♡ Linux下的hosts文件所在路径：/etc/hosts

​				⚑ 域名服务器解析

​						本地解析失败，才会进行域名服务器解析，域名服务器就是网络中的一台计算机，里面记录了所有

​						注册备案的域名和ip映射关系，一般只要域名是正确的，并且备案通过，一定能找到。

#### 3. 解决域名解析问题

​		我们不可能去购买一个域名，因此我们可以伪造本地的hosts文件，实现对域名的解析。

​		修改本地的host为：				

```
127.0.0.1 api.mrshop.com 
127.0.0.1 manage.mrshop.com
```

​		这样就实现了域名的关系映射了。

​		🈲注意: 有可能出现不能修改文件的问题:

​    									1.用户权限的问题

​										2.文件有只读属性

​		添加了两个映射关系：

​				✦ 127.0.0.1 api.mrshop.com ：我们的网关Zuul

​				✦ 127.0.0.1 manage.shop.com：我们的后台系统地址

​		现在，ping一下域名试试是否畅通：

​			<img src="E:\淘宝项目\image\screenshot_20210105_213409.png" style="zoom: 33%;" />

#### 4. 测试

- 启动后端VUE项目

- 解压 `mrshop-manage-web.rar` 到 vue 工作空间

- 使用 vue 打开项目

  ​	<img src="E:\淘宝项目\image\screenshot_20210105_214557.png" style="zoom:33%;" />

浏览器访问 manage.mrshop.com:9001

​		<img src="E:\淘宝项目\image\screenshot_20210106_113423.png" style="zoom:33%;" />

输入域名 + 9001端口号出现上述错误

打开build文件夹下 `webpack.dev.conf.js` 文件在 devServer 下加入

```
disableHostCheck: true,
```

<img src="E:\淘宝项目\image\screenshot_20210106_113706.png" style="zoom:50%;" />

再次编译项目...

![](E:\淘宝项目\image\screenshot_20210106_113906.png)

😜 域名解析成功 !

#### 5. nginx解决端口问题

​			我们希望的是直接域名访问： http://manage.mrshop.com 。这种情况下端口默认是80，

​			如何才能把请求转移到 9001 端口呢？

​			这里就要用到反向代理工具：Nginx

------

##### 	5.1 什么是Nginx

​			<img src="E:\淘宝项目\image\screenshot_20210106_114504.png" style="zoom:50%;" />

​					NIO：not-blocking-io 非阻塞IO

​					BIO：blocking-IO 阻塞IO

​					nginx可以作为web服务器，但更多的时候，我们把它作为网关，因为它具备网关必备的功能：

​									⚑ 反向代理

​									⚑ 负载均衡

​									⚑ 动态路由

​									⚑ 请求过滤

------

##### 		5.2 nginx作为web服务器

​					Web服务器分2类：

​							-» web应用服务器，如：

​										tomcat.  resin.  jetty.

​							-» web服务器，如：

​										Apache服务器.  Nginx.  IIs.

​					区分：web服务器不能解析jsp等页面，只能处理js、css、html等静态资源。

​					并发：web服务器的并发能力远高于web应用服务器。

##### 		5.3 nginx作为反向代理

​					什么是反向代理:

​			                         ✦ 代理：通过客户机的配置，实现让一台服务器代理客户机，客户的所有请求都交给代理服务器处理。

​									 ✦ 反向代理：用一台服务器，代理真实服务器，用户访问时，不再是访问真实服务器，而是代理服务器。

​					nginx可以当做反向代理服务器来使用：

​									 ✦ 我们需要提前在nginx中配置好反向代理的规则，不同的请求，交给不同的真实服务器处理

​									 ✦ 当请求到达nginx，nginx会根据已经定义的规则进行请求的转发，从而实现路由功能

##### 		5.4 安装和使用

​					解压nginx-1.16.1.zip

​					安装成功

------

​					nginx可以通过命令行来启动，操作命令：

​								• 启动： `start nginx.exe`

​								• 停止:  `nginx.exe -s stop`  

​								• 重新加载:  `nginx.exe -s reload`

​			<img src="E:\淘宝项目\image\screenshot_20210106_142033.png" style="zoom:50%;" />

​					😊通过上图--> 打开命令窗口--> 输入命令启动即可

​						注意: 启动之前需要配置一下反向代理配置

​									🈲修改nginx/conf/nginx.conf文件:						

```
#user nobody;
worker_processes 1;

#error_log logs/error.log;
#error_log logs/error.log notice;
#error_log logs/error.log info;

#pid logs/nginx.pid;

events {
	worker_connections 1024;
}
http {
	include mime.types;
	default_type application/octet-stream;
	
	sendfile on;
	keepalive_timeout 65;
	
	server {
		listen 80;
		server_name manage.mrshop.com;
		
		proxy_set_header X-Forwarded-Host $host;
		proxy_set_header X-Forwarded-Server $host;
		proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
		
		location / {
			proxy_pass http://127.0.0.1:9001;
			proxy_connect_timeout 600;
			proxy_read_timeout 600;
		}
	}
	server {
		listen 80;
		server_name api.mrshop.com;

		proxy_set_header X-Forwarded-Host $host;
		proxy_set_header X-Forwarded-Server $host;
		proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;

		location / {
			proxy_pass http://127.0.0.1:8088;
			proxy_connect_timeout 600;
			proxy_read_timeout 600;
		}
		
		# 上传路径的映射 
		# 只要包含/api-xxx/upload 都会把请求映射到8200服务 
		# rewrite "^/api-xxx/(.*)$" /$1 break; 
		# 将/api-xxx 替换成/ 
		
		location /api-xxx/upload { 
			proxy_pass http://127.0.0.1:8200;
			proxy_connect_timeout 600; 
			proxy_read_timeout 600; 
			
			rewrite "^/api-xxx/(.*)$" /$1 break;
		} 

		
	}
	server {
		listen 80;
		server_name image.mrshop.com;
		
		proxy_set_header X-Forwarded-Host $host;
		proxy_set_header X-Forwarded-Server $host;
		proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
		location ~ .*\.(gif|jpg|pdf|jpeg|png)$ {
			#root D:/nginx-1.15.5/temp/images/;
			#指定图片存放路径(可以放在nginx文件夹路 径里也可以放其他p盘) 
			root D:\images; 
		}
		location / {
			root html;
			index index.html index.htm; 
		} 
	}
}
```

##### 5.5 测试

​		浏览器输入 http://manage.mrshop.com/

<img src="E:\淘宝项目\image\screenshot_20210106_142546.png" style="zoom: 33%;" />

​		出现上图效果即可

​		现在实现了域名访问网站了，中间的流程是怎样的呢？

​						1. 浏览器准备发起请求，访问 http://mamage.mrshop.com，但需要进行域名解析

​						2. 优先进行本地域名解析，因为我们修改了hosts，所以解析成功，得到地址：127.0.0.1

​						3. 请求被发往解析得到的ip，并且默认使用80端口：http://127.0.0.1:80

​								本机的nginx一直监听80端口，因此捕获这个请求

​						4. nginx中配置了反向代理规则，将manage.mrshop.com代理到127.0.0.1:9001，因此请求被转发

​						5. 后台系统的webpack server监听的端口是9001，得到请求并处理，完成后将响应返回到nginx

​						6. nginx将得到的结果返回到浏览器

------

