package com.my.project.java;

import org.junit.Test;

public class FunctionalInterfaceTest {

	GreetingService service1 = (String s) -> System.out.println("service1: " + s);
	GreetingService service2 = s -> System.out.println("service2: " + s);

	@Test
	public void testGreetService() {
		service1.sayMessage("Hello World");
		service2.sayMessage("Hello Newegg");
	}
}
