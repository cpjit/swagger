/*
 * Copyright 2011-2017 CPJIT Group.
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package com.cpjit.swagger4j.util;

import org.apache.commons.lang3.StringUtils
import java.io.FileNotFoundException
import java.io.IOException
import java.net.URISyntaxException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

/**
 * 反射工具集。
 *
 * @author yonghaun
 * @since 1.2.2
 */
object ReflectUtils {

    /**
     * 扫描指定包并获取包中的全部类。
     *
     * @param packNames
     *            待扫描的包
     * @return 指定包下面的全部类，永远都不返回null。
     * 		当出现以下情况的时候将返回长度为0的列表：
     * 		<li>参数packNames的长度为0</li>
     * 		<li>指定的包中没有类（或接口）</li>
     *
     * @throws FileNotFoundException 如果指定的包不存在就抛出该异常
     * @throws IllegalArgumentException 如果包名对应的文件不是文件夹就抛出该异常
     * @throws ClassNotFoundException 如果类不存在就抛出该异常
     *
     * @since 1.0.0
     */
    @Throws(FileNotFoundException::class, IllegalArgumentException::class, ClassNotFoundException::class)
    fun scanClazzs(packNames: List<String>): List<Class<*>> {
        if (packNames.isEmpty()) { // 未指定包名
            return Collections.emptyList();
        }

        var clazzs = HashSet<Class<*>>();
        for (packName in packNames) {
            var clazzNames = scanClazzName(packName);
            for (clazzName in clazzNames) { // 获取包下的全部类
                var clazz = Class.forName(clazzName);
                clazzs.add(clazz);
            }
        }
        if (clazzs.isEmpty()) { // 未搜索到类
            return Collections.emptyList();
        }
        return ArrayList<Class<*>>(clazzs);
    }

    /**
     * 扫描指定包并获取包中的全部类。
     *
     * @param packageNames
     *            待扫描的包
     * @param recursion
     *            是否递归扫描子包
     * @return 指定包下面的全部类，永远都不返回null。
     * 		当出现以下情况的时候将返回长度为0的列表：
     * 		<li>参数packageNames的长度为0</li>
     * 		<li>当参数recursion为false时，指定的包中没有类（或接口）</li>
     * 		<li>当参数recursion为true时，指定的包及其子包中没有类（或接口）</li>
     *
     * @throws FileNotFoundException 如果指定的包不存在就抛出该异常
     * @throws IllegalArgumentException 如果包名对应的文件不是文件夹就抛出该异常
     * @throws ClassNotFoundException 如果类不存在就抛出该异常
     *
     * @since 1.0.0
     */
    @Throws(FileNotFoundException::class, IllegalArgumentException::class, ClassNotFoundException::class)
    fun scanClazzs(packageNames: List<String>,
                   recursion: Boolean): List<Class<*>> {
        if (packageNames.isEmpty()) { // 未指定包名
            return Collections.emptyList();
        }
        var packNames = scanPackages(packageNames, recursion)
                .map { it.name }
                .toList();
        return scanClazzs(packNames);
    }

    /**
     * 扫描包及其子包。
     *
     * @param basePackage basePackage
     * @param recursion
     *            是否递归扫描子包
     * @return    搜索到的包信息，返回值永远都不为null。
     * 			以下情况将返回长度为0的列表：
     * 			<li>所有指定的包都不存在</li>
     * 			<li>指定的包中没有package-info.java文件并且指定的包中没有任何类（或接口）</li>
     * 			<li>参数basePackage为空串或全为空白字符</li>
     * @since 1.0.0
     */
    fun scanPackage(basePackage: String, recursion: Boolean): List<Package> {
        if (StringUtils.isBlank(basePackage)) {
            return Collections.emptyList();
        }

        var packages = HashSet<Package>();
       // var pack2path = basePackage.replace("\\.", "/");
        var pack2path = StringUtils.replaceAll(basePackage,"\\.", "/");
        var url = ResourceUtil.getResource(pack2path);
        if (url == null) { // 包不存在
            return Collections.emptyList();
        }

        var pkFile: Path;
        try {
            pkFile = Paths.get(url.toURI());
        } catch (e: Exception) {
            throw  IllegalArgumentException(basePackage + "不是合法的包名", e);
        }
        if (!Files.isDirectory(pkFile)) {
            throw IllegalArgumentException(basePackage + "不是合法的包名");
        }

        try {
            packages.add(getPackage(basePackage));
        } catch (ignored: Exception) {
        }

        if (recursion && StringUtils.isNotBlank(basePackage)) { // 递归扫描子包
            var childs = Files.newDirectoryStream(pkFile)
            try {
                childs.forEach {
                    if (Files.isDirectory(it)) {
                        var childPackage = basePackage + "." + it.getFileName().toString();
                        if (StringUtils.isBlank(basePackage)) { // 默认包
                            childPackage = it.getFileName().toString();
                        }
                        packages.addAll(scanPackage(childPackage, true));
                    }
                };
            } catch (e: Exception) {
                throw IllegalStateException(e);
            }
        }
        if (packages.isEmpty()) { // 未搜索到包信息
            return Collections.emptyList();
        }

        return ArrayList<Package>(packages);
    }

    /**
     * 扫描包信息（不会递归扫描子包）
     *
     * @param packageName packageName
     * @return    搜索到的包信息，返回值永远都不为null。
     * 			以下情况将返回长度为0的列表：
     * 			<li>所有指定的包都不存在</li>
     * 			<li>指定的包中没有package-info.java文件并且指定的包中没有任何类（或接口）</li>
     * 			<li>参数basePackage为空串或全为空白字符</li>
     * @since 1.0.0
     */
    fun scanPackage(packageName: String): List<Package> {
        return scanPackage(packageName, false);
    }

    /**
     * 根据包名扫描包信息。
     *
     * @param packageNames packageNames
     * @return    搜索到的包信息，返回值永远都不为null。
     * 			以下情况将返回长度为0的列表：
     * 			<li>所有指定的包都不存在</li>
     * 			<li>包中没有package-info.java文件并且包中没有任何类（或接口）</li>
     * 			<li>参数basePackage为空串或全为空白字符</li>
     * @since 1.0.0
     */
    fun scanPackages(packageNames: List<String>): List<Package> {
        return scanPackages(packageNames, false);
    }

    /**
     * 根据包名扫描包信息。
     *
     * @param packageNames packageNames
     * @param recursion
     *            是否递归扫描子包
     * @return    搜索到的包信息，返回值永远都不为null。
     * 			以下情况将返回长度为0的列表：
     * 			<li>所有指定的包都不存在</li>
     * 			<li>指定的包中没有package-info.java文件并且指定的包中没有任何类（或接口）</li>
     * 			<li>参数basePackage为空串或全为空白字符</li>
     * @since 1.0.0
     */
    fun scanPackages(packageNames: List<String>, recursion: Boolean): List<Package> {
        if (packageNames.isEmpty()) { // 未指定包名
            return emptyList();
        }
        return packageNames.flatMap({ scanPackage(it, recursion) })
    }

    /**
     * 扫描包下所有类的类全名。
     *
     * @param packageName packageName
     * @return 指定包下面的全部类名，永远都不返回null。
     * 		当出现以下情况的时候将返回长度为0的列表：
     * 		<li>参数packNames的长度为0</li>
     * 		<li>指定的所有类（或接口）在包中都不存在</li>
     * @throws FileNotFoundException 如果指定的包不存在就抛出该异常
     * @throws IllegalArgumentException 如果包名对应的文件不是文件夹就抛出该异常
     * @since 1.0.0
     */
    @Throws(FileNotFoundException::class, IllegalArgumentException::class)
    fun scanClazzName(packageName: String): List<String> {
      //  var pack2path = packageName.replace("\\.", "/");
        var pack2path = StringUtils.replaceAll(packageName,"\\.", "/");
        if (StringUtils.isBlank(packageName)) {
            pack2path = "";
        }

        var fullPath = ResourceUtil.getResource(pack2path);
        if (fullPath == null) {
            throw  FileNotFoundException("包[" + packageName + "]不存在");
        }
        var pack: Path
        try {
            pack = Paths.get(fullPath.toURI());
        } catch (e: URISyntaxException) {
            throw  IllegalArgumentException(e);
        }
        if (!Files.isDirectory(pack)) {
            throw IllegalArgumentException("[" + packageName + "]不是合法的包名");
        }

        var clazzNames = HashSet<String>();
        var prefix = when (StringUtils.isBlank(packageName)) {true -> ""
            else -> packageName + "."
        };
        // 获取包下的Java源文件
        var clazzs = Files.newDirectoryStream(pack, "*.class")
        try {
            clazzs.forEach {
                // 获取包下的全部类名
                var clazzName = prefix + it.getFileName().toString().replace(".class", "");
                clazzNames.add(clazzName);
            };
        } catch (e: IOException) {
            throw IllegalArgumentException(e);
        }

        if (clazzNames.isEmpty()) { // 未查找到类信息
            return emptyList();
        }
        return ArrayList<String>(clazzNames);
    }

    /**
     * 根据包名获取包信息。
     * <p>
     * 当包中没有package-info.java文件并且包中没有任何类（或接口）的时候会返回{@code null}。
     * </p>
     * @since 1.2.2
     */
    @Throws(FileNotFoundException::class, IllegalArgumentException::class)
    fun getPackage(packageName: String): Package {
        var pkg = Package.getPackage(packageName);
        if (pkg == null) { // 包内不存在package-info.class文件
            var pkgs = scanClazzName(packageName).map { Class.forName(it).getPackage() }
            if (pkgs.isNotEmpty()) {
                return pkgs.get(0);
            }
        }
        return pkg;
    }
}
