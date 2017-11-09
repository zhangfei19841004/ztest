package com.test.testcase;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.test.report.ZTestReport;

@Listeners({ZTestReport.class})
public class TestDemo1{
	
	@BeforeMethod
	public void beforeMethod(Method m){
		if("testDemo3".equals(m.getName())){
			int a = 1/0;
			System.out.println(a);
		}
	}
	
	@Test
	public void testDemo(){
		Reporter.log("this is demo!");
		int a = 1/0;
		System.out.println(a);
		Assert.assertEquals("a", "b", "should be equals.");
	} 
	
	@Test
	public void testDemo1(){
		Reporter.log("this is demo!");
		Assert.assertEquals("a", "b", "should be equals.");
	} 
	
	@Test(dataProvider="test")
	public void testDemo2(int a){
		Reporter.log("this is demo!");
		Assert.assertEquals(a, 1, "should be equals.");
	} 
	
	@Test
	public void testDemo3(){
		Reporter.log("this is demo!");
		Assert.assertEquals("a", "a", "should be equals.");
	} 
	
	@DataProvider(name="test")
	public Object[][] dataProvider(){
		return new Object[][]{{1},{2}};
	}
	
}
