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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

/**
 * 反射工具集。
 * 
 * @author yonghaun
 * @since 1.2.2
 */
public final class ReflectUtils {
	private ReflectUtils() {
		throw new AssertionError("不允许实例化 " + ReflectUtils.class.getName());
	}

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
	public static List<Class<?>> scanClazzs(List<String> packNames) throws FileNotFoundException, IllegalArgumentException, ClassNotFoundException {
		if(packNames.size() < 1) { // 未指定包名
			return Collections.emptyList();
		}
		
		Set<Class<?>> clazzs = new HashSet<>();
		for (String packName : packNames) {
			List<String> clazzNames = scanClazzName(packName);
			for (String clazzName : clazzNames) { // 获取包下的全部类
				Class<?> clazz = Class.forName(clazzName);
				clazzs.add(clazz);
			}
		}
		if(clazzs.size() < 1) { // 未搜索到类
			return Collections.emptyList();
		}
		return new ArrayList<>(clazzs);
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
	public static List<Class<?>> scanClazzs(List<String> packageNames,
			boolean recursion) throws FileNotFoundException, IllegalArgumentException, ClassNotFoundException   {
		if(packageNames.size() < 1) { // 未指定包名
			return Collections.emptyList();
		}
		List<String> packNames = scanPackages(packageNames, recursion)
										.stream()
										.map(Package::getName)
										.collect(Collectors.toList());
		return scanClazzs(packNames);
	}

	/**
	 * 扫描包及其子包。
	 * 
	 * @param basePackage basePackage
	 * @param recursion
	 *            是否递归扫描子包
	 * @return 	搜索到的包信息，返回值永远都不为null。
	 * 			以下情况将返回长度为0的列表：
	 * 			<li>所有指定的包都不存在</li>
	 * 			<li>指定的包中没有package-info.java文件并且指定的包中没有任何类（或接口）</li>
	 * 			<li>参数basePackage为空串或全为空白字符</li>
	 * @since 1.0.0
	 */
	public static List<Package> scanPackage(final String basePackage, boolean recursion)  {
		if(StringUtils.isBlank(basePackage)) {
			return Collections.emptyList();
		}
		
		Set<Package> packages = new HashSet<>();
		String pack2path = basePackage.replaceAll("\\.", "/");
		URL url = ResourceUtil.getResource(pack2path);
		if(url == null) { // 包不存在
			return Collections.emptyList();
		}
		
		Path pkFile;
		try {
			pkFile = Paths.get(url.toURI());
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(basePackage + "不是合法的包名", e);
		}
		if (!Files.isDirectory(pkFile)) {
			throw new IllegalArgumentException(basePackage + "不是合法的包名");
		}

		Package pk = null;
		try {
			pk = getPackage(basePackage);
		} catch (Exception ignored) {
		} 
		if (pk != null) {
			packages.add(pk);
		}

		if (recursion && StringUtils.isNotBlank(basePackage)) { // 递归扫描子包
			try (DirectoryStream<Path> childs = Files.newDirectoryStream(pkFile)) {
				childs.forEach(child -> {
					if(Files.isDirectory(child)) {
						String childPackage = basePackage + "." + child.getFileName().toString();
						if(StringUtils.isBlank(basePackage)) { // 默认包
							childPackage = child.getFileName().toString();
						}
						packages.addAll(scanPackage(childPackage, true));
					}
				});
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
		if(packages.size() < 1) { // 未搜索到包信息
			return Collections.emptyList();
		}
		
		return new ArrayList<>(packages);
	}

	/**
	 * 扫描包信息（不会递归扫描子包）
	 * 
	 * @param packageName packageName
	 * @return 	搜索到的包信息，返回值永远都不为null。
	 * 			以下情况将返回长度为0的列表：
	 * 			<li>所有指定的包都不存在</li>
	 * 			<li>指定的包中没有package-info.java文件并且指定的包中没有任何类（或接口）</li>
	 * 			<li>参数basePackage为空串或全为空白字符</li>
	 * @since 1.0.0
	 */
	public static List<Package> scanPackage(final String packageName) {
		return scanPackage(packageName, false);
	}

	/**
	 * 根据包名扫描包信息。
	 * 
	 * @param packageNames packageNames
	 * @return 	搜索到的包信息，返回值永远都不为null。
	 * 			以下情况将返回长度为0的列表：
	 * 			<li>所有指定的包都不存在</li>
	 * 			<li>包中没有package-info.java文件并且包中没有任何类（或接口）</li>
	 * 			<li>参数basePackage为空串或全为空白字符</li>
	 * @since 1.0.0
	 */
	public static List<Package> scanPackages(List<String> packageNames) {
		return scanPackages(packageNames, false);
	}

	/**
	 * 根据包名扫描包信息。
	 * 
	 * @param packageNames packageNames
	 * @param recursion
	 *            是否递归扫描子包
	 * @return 	搜索到的包信息，返回值永远都不为null。
	 * 			以下情况将返回长度为0的列表：
	 * 			<li>所有指定的包都不存在</li>
	 * 			<li>指定的包中没有package-info.java文件并且指定的包中没有任何类（或接口）</li>
	 * 			<li>参数basePackage为空串或全为空白字符</li>
	 * @since 1.0.0
	 */
	public static List<Package> scanPackages(List<String> packageNames, boolean recursion) {
		if(packageNames == null || packageNames.size() < 1) { // 未指定包名
			return Collections.emptyList();
		}
		return packageNames.stream()
								.flatMap(packName -> scanPackage(packName, recursion).stream())
								.collect(Collectors.toList());
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
	public static List<String> scanClazzName(String packageName)
			throws FileNotFoundException, IllegalArgumentException{
		String pack2path = packageName.replaceAll("\\.", "/");
		if(StringUtils.isBlank(packageName)) {
			pack2path = "";
		}
		
		URL fullPath = ResourceUtil.getResource(pack2path);
		if (fullPath == null) {
			throw new FileNotFoundException("包[" + packageName + "]不存在");
		}
		Path pack;
		try {
			pack = Paths.get(fullPath.toURI());
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
		if (!Files.isDirectory(pack)) {
			throw new IllegalArgumentException("[" + packageName + "]不是合法的包名");
		}

		Set<String> clazzNames = new HashSet<>();
		String prefix = StringUtils.isBlank(packageName) ? "" : packageName + ".";
		// 获取包下的Java源文件
		try(DirectoryStream<Path> clazzs = Files.newDirectoryStream(pack, "*.class")) {
			clazzs.forEach(clazz -> {  // 获取包下的全部类名
				String clazzName = prefix + clazz.getFileName().toString().replace(".class", "");
				clazzNames.add(clazzName);
			});
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}

		if(clazzNames.size() < 1) { // 未查找到类信息
			return Collections.emptyList();
		}
		return new ArrayList<>(clazzNames);
	}
	
	/**
	 * 根据包名获取包信息。
	 * <p>
	 * 当包中没有package-info.java文件并且包中没有任何类（或接口）的时候会返回{@code null}。
	 * </p>
	 * @since 1.2.2
	 */
	private static Package getPackage(final String packageName) throws FileNotFoundException, IllegalArgumentException {
		Package pkg = Package.getPackage(packageName);
		if(pkg == null) { // 包内不存在package-info.class文件
			Optional<Package> optionalPkg = scanClazzName(packageName).stream()
										.map(clazzName -> {
											try {
												return Class.forName(clazzName).getPackage();
											} catch (ClassNotFoundException e) {
												return null;
											}
										})
										.filter(Objects::nonNull)
										.findFirst();
			if(optionalPkg.isPresent()) {
				return optionalPkg.get();
			}
		}
		return pkg;
	}
}
