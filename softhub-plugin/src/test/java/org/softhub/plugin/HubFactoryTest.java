package org.softhub.plugin;

public class HubFactoryTest {
	public static void main(String[] args) throws Exception {
		HubFactory hubFactory = new HubFactory();
		hubFactory.setHubLoader(new HubLoader() {
			@Override
			public String load(String name) {
				if ("j".equals(name)) {
					String classCode = "package com.xxl.groovy.core;" +
							"public class DemoImpl implements IDemo {" +
							"public int add(int a, int b) {" +
							"return a + b;" +
							"}" +
							"}" +
							";";
					return classCode;
				} else if ("g".equals(name)) {
					String classCode = "package com.xxl.groovy.core;" +
							"class DemoImpl implements IDemo {" +
							"public int add(int a, int b) {" +
							"Utils.print(999999);" +
							"foo();" +
							"return a * b;" +
							"}" +
							"def foo(){" +
							"println 11111;" +
							"}" +
							"}" +
							";";
					return classCode;
				} else {
					String classCode = "package com.xxl.groovy.core;" +
							"public class DemoImpl implements IDemo {" +
							"public int add(int a, int b) {" +
							"Utils.print(999999);" +
							"return a * b;" +
							"}" +
							"}" +
							";";
					return classCode;
				}
			}

		});
		
		@SuppressWarnings("unchecked")
		Class<IDemo> clazz = (Class<IDemo>) hubFactory.loadClass("g");
		
		IDemo service = clazz.newInstance();
		System.out.println(service);
		System.out.println(service.add(10, 10));
	}
}

