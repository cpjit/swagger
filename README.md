# cpj-swagger
`cpj-swagger`通过与[`swagger ui`](http://swagger.io/)一起快速为您的web项目产生接口文档，并且支持在线测试接口。`cpj-swagger`可以很方便的与`struts2`、`spring mvc`、`servlet`集成使用，下面的教程将详细说明如何使用cpj-swagger。

## 目录
[`一. 加入依赖JAR文件`](#一-加入依赖JAR文件)<br />
[`二. 配置`](#二-配置)<br />
[`三. 标注你的接口`](#三-标注你的接口)<br />
[`四. 访问接口文档`](#四-访问接口文档)<br />

## 一. 加入依赖JAR文件
* 增加依赖配置：
```xml
<dependency>
	<groupId>com.github.3cpj</groupId>
	<artifactId>cpj-swagger</artifactId>
	<version>1.2.1</version>
</dependency>
```

## 二. 配置
### 1. 选择使用方式
您可以通过三种方式来使用`cpj-swagger`。
* 与strut2集成</br>
如果您的web项目是基于`strut2`的，您可以在您的strut2配置文件中加入如下代码来快速集成`cpj-swagger`：
```xml
   <constant name="struts.enable.DynamicMethodInvocation" value="true" />
   <constant name="struts.devMode" value="true" />

	<package name="api" namespace="/api" extends="struts-default" >
		<action name="*" class="com.cpj.swagger.support.struts2.ApiAction" method="{0}">
		</action>
	</package>
```
* 与spring mvc集成 </br>
如果您的web项目是基于`spring mvc`的，您可以在您的spring mvc配置文件中加入如下代码来快速集成`cpj-swagger`：
```xml
 <context:component-scan base-package="com.cpj.swagger.support.springmvc">
    	<context:include-filter type="annotation" expression="org.springframework.stereotype.Controller"/>
 </context:component-scan>
```
* 与servlet集成</br>
如果您的web项目是基于`servlet`的，您可以在您的web.xml配置文件中加入如下代码来快速集成`cpj-swagger`：
```xml
<servlet>
    <servlet-name>apiServlet</servlet-name>
    <servlet-class>com.cpj.swagger.support.servlet.ApiServlet</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>apiServlet</servlet-name>
    <url-pattern>/api/*</url-pattern>
</servlet-mapping>
```
### 2. 修改配置项
您需要在项目的源文件目录下添加一个`swagger.properties`文件，并加入以下配置项：
```java
packageToScan=com.cpj.swagger.action
apiDescription=Swagger Demo
apiTitle=Swagger Demo
apiVersion=1.0.0
teamOfService=www.cpj.com
devMode=true
```
`cpj-swagger`的配置信息都必须写在`swagger.properties`文件里面。具体的配置项及其说明如下：
<table>
	<thead>
		<tr><th>键</th><th>说明</th></tr>
	</thead>
	<tbody>
		<tr><th>apiFile</th><th>扫描生成json文件的存放路径</th></tr>
		<tr><th>packageToScan</th><th>扫描的包</th></tr>
		<tr><th>apiDescription</th><th>接口文档描述</th></tr>
		<tr><th>apiTitle</th><th>接口文档标题</th></tr>
		<tr><th>apiVersion</th><th>接口版本</th></tr>
		<tr><th>teamOfService</th><th>服务团队</th></tr>
		<tr><th>apiHost</th><th>接口主机地址</th></tr>
		<tr><th>apiBasePath</th><th>接口基路径</th></tr>
		<tr><th>devMode</th><th>是否启用开发模式，如果开启则每次获取接口文档的时候都会扫描类</th></tr>
		<tr><th>suffix</th><th>接口请求地址后缀，如.action</th></tr>
	</tbody>
</table>

## 三. 标注你的接口
接下来需要用cpj-swagger提供的注解来标明你的接口，下面以spring mvc为例来说明如何标注接口，其他方式请参考[`示例程序`](#四-示例程序)。
```java
@Controller
@APIs("/demo")
@RequestMapping("/demo")
public class DemoController {
	@API(value="login", summary="示例1", parameters={
			@Param(name="username", description="用户名", type="string"),
			@Param(name="password", description="密码", type="string", format="password"),
			@Param(name="image" , description="图片", type="file", format="binary")
	})
	@RequestMapping(value="login", method=RequestMethod.POST)
	public void login(HttpServletResponse response, String username, String password, MultipartFile image)
												throws Exception {
		response.setContentType("application/json;charset=utf-8");
		JSONWriter writer = new JSONWriter(response.getWriter());
		Map<String, String> user = new HashMap<String, String>();
		user.put("username", username);
		user.put("password", password);
		writer.writeObject(user);
		writer.flush();
		writer.close();
	}
}
```
## 四. 访问接口文档
在完成前面的工作之后，您可以部署好你的web项目，然后在浏览器输入以下地址就可以访问接口文档了：
http://127.0.0.1:8080/您的项目名/api/index
