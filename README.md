# cpj-swagger
`cpj-swagger`通过与[`swagger ui`](http://swagger.io/)一起快速为您的web项目产生接口文档，并且支持在线测试接口。`cpj-swagger`可以很方便的与`struts2`、`spring mvc`、`servlet`集成使用，下面的教程将详细说明如何使用cpj-swagger。

## 目录
[`一. 加入依赖JAR文件`](#一-加入依赖JAR文件)<br />
[`二. 配置`](#二-配置)<br />
[`三. 标注你的接口`](#三-标注你的接口)<br />
[`四. 访问接口文档`](#四-访问接口文档)<br />
[`五. 核心API`](#五-核心api)<br />

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
在完成前面的工作之后，您可以部署好您的web项目，然后在浏览器输入以下地址就可以访问接口文档了：
http://127.0.0.1:8080/您的项目名/api/index

## 五. 核心API
### 1. 注解 @com.cpj.swagger.annotation.APIs
该注解放在一个类上面，用来表明类中包含作为HTTP接口的方法（被注解[`@com.cpj.swagger.annotation.API`](#@com.cpj.swagger.annotation.api)标注了的方法）。
该注解的`value`用来设置接口的前缀，这和`struts2`的命名空间很像。使用示例如下：
```java
@APIs("/demo")
public class DemoController {
  
}
```
### 2. 注解 @com.cpj.swagger.annotation.API
该注解放在一个方法上面，用来表明方法是HTTP接口方法，注解的属性说明如下：
#### `value`
与注解[`@com.cpj.swagger.annotation.APIs`](#@com.cpj.swagger.annotation.apis)的`value`属性一起来指定接口的地址，例如有如下设置：
```java
@APIs("/demo")
public class DemoController {
  	@API(value="login"})
	public void login()
	}
}
```
那么`login`方法对应的接口地址为： youhost/demo/login

#### `parameters`
用来指定接口的请求参数，详情参见注解[`Param`](#3-注解-@com.cpj.swagger.annotation.param)的说明。
#### `summary`
接口功能简述。
#### `description`
接口功能详细说明。
#### `method`
请求方式，默认是POST。
#### `consumes`

允许的请求MIME，比如：multipart/form-data、application/xml、application/json默认是application/json; charset=utf-8。
<p>特别说明：</p>
	当为 `multipart/form-data` 时，[`Param`](#3-注解 @com.cpj.swagger.annotation.Param)
	的[`in`](#in)属性必须为`formData`，但是in()为path、header时[`Param`](#3-注解-@com.cpj.swagger.annotation.Param)不用遵循此规则。
### 3. 注解 @com.cpj.swagger.annotation.Param
用来说明请求参数，例如：
```java
@API(value="login", summary="示例1", parameters={
		@Param(name="username", description="用户名", type="string"),
		@Param(name="password", description="密码", type="string", format="password")})
@RequestMapping(value="login", method=RequestMethod.POST)
public void login(HttpServletResponse response, String username, String password) throws Exception {
}
```
这表明该接口需要两个请求参数，及`username`、`password`。
注解`@com.cpj.swagger.annotation.Param`的属性说明如下：
#### `name`
参数名
#### `in`
输入参数类型，可取如下值：
<ul>
  <li>query - 参数拼接到url中</li>
  <li>body - 参数直接放入请求体中</li>
  <li>path - restful风格的参数传递</li>
  <li>header - 参数放在请求头中</li>
  <li>formData - 参数通过form表单提交</li>
 </ul> 
特别说明：
<ul>
 <li>当前请求方式为POST的时候，默认值为formData</li>
 <li>请求方式为非POST的时候，默认值为query</li>
</ul>
#### `type` 
数据类型, 与[`format`](#format)一起指定请求参数的数据类型。
	 `type` 和 `format` 的可选值如下：
	   <table border="0" cellpadding="1" cellspacing="0">
		<thead>
		<tr bgcolor="#EFEFEF" style="border: 1px solid #cccccc;">
		<th align="left">通用名</th>
		<th align="left"><a href="#dataTypeType"><code>type</code></a></th>
		<th align="left"><a href="#dataTypeFormat"><code>format</code></a></th>
		<th align="left">备注</th>
		</tr>
		</thead>
		<tbody>
		<tr bgcolor="#FFFFFF">
		<td>integer</td>
		<td><code>integer</code></td>
		<td><code>int32</code></td>
		<td>signed 32 bits</td>
		</tr>
		<tr bgcolor="#EFEFEF">
		<td>long</td>
		<td><code>integer</code></td>
		<td><code>int64</code></td>
		<td>signed 64 bits</td>
		</tr>
		<tr bgcolor="#FFFFFF">
		<td>float</td>
		<td><code>number</code></td>
		<td><code>float</code></td>
		<td></td>
		</tr>
		<tr bgcolor="#EFEFEF">
		<td>double</td>
		<td><code>number</code></td>
		<td><code>double</code></td>
		<td></td>
		</tr>
		<tr bgcolor="#FFFFFF">
		<td>string</td>
		<td><code>string</code></td>
		<td></td>
		<td></td>
		</tr>
		<tr bgcolor="#EFEFEF">
		<td>byte</td>
		<td><code>string</code></td>
		<td><code>byte</code></td>
		<td>base64 encoded characters</td>
		</tr>
		<tr bgcolor="#FFFFFF">
		<td>binary</td>
		<td><code>string</code></td>
		<td><code>binary</code></td>
		<td>any sequence of octets</td>
		</tr>
		<tr bgcolor="#EFEFEF">
		<td>boolean</td>
		<td><code>boolean</code></td>
		<td></td>
		<td></td>
		</tr>
		<tr bgcolor="#FFFFFF">
		<td>date</td>
		<td><code>string</code></td>
		<td><code>date</code></td>
		<td>As defined by <code>full-date</code> - <a href="http://xml2rfc.ietf.org/public/rfc/html/rfc3339.html#anchor14">RFC3339</a></td>
		</tr>
		<tr bgcolor="#EFEFEF">
		<td>dateTime</td>
		<td><code>string</code></td>
		<td><code>date-time</code></td>
		<td>As defined by <code>date-time</code> - <a href="http://xml2rfc.ietf.org/public/rfc/html/rfc3339.html#anchor14">RFC3339</a></td>
		</tr>
		<tr bgcolor="#FFFFFF">
		<td>password</td>
		<td><code>string</code></td>
		<td><code>password</code></td>
		<td>Used to hint UIs the input needs to be obscured.</td>
		</tr>
		</tbody>
	 	</table>
#### `format`
数据格式，[`type`](#type)一起指定请求参数的数据类型。
#### description
参数说明
#### `required`
是否是必须参数， 默认是false
