package com.test.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testng.IReporter;
import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.xml.XmlSuite;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.test.testcase.FileUtil;

public class ZTestReport implements IReporter {
	
	private int testsPass = 0;

	private int testsFail = 0;

	private int testsSkip = 0;
	
	private String beginTime;
	
	private long totalTime;
	
	private String name;
	
	public ZTestReport(){
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMddHHmmssSSS");
		name = formatter.format(System.currentTimeMillis());
	}
	
	public ZTestReport(String name){
		this.name = name;
		if(this.name==null){
			SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMddHHmmssSSS");
			this.name = formatter.format(System.currentTimeMillis());
		}
	}

	@Override
	public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
		List<ITestResult> list = new ArrayList<ITestResult>();
		for (ISuite suite : suites) {
			Map<String, ISuiteResult> suiteResults = suite.getResults();
			for (ISuiteResult suiteResult : suiteResults.values()) {
				ITestContext testContext = suiteResult.getTestContext();
				IResultMap passedTests = testContext.getPassedTests();
				testsPass = testsPass + passedTests.size();
				IResultMap failedTests = testContext.getFailedTests();
				testsFail = testsFail + failedTests.size();
				IResultMap skippedTests = testContext.getSkippedTests();
				testsSkip = testsSkip + skippedTests.size();
				IResultMap failedConfig = testContext.getFailedConfigurations();
				list.addAll(this.listTestResult(passedTests));
				list.addAll(this.listTestResult(failedTests));
				list.addAll(this.listTestResult(skippedTests));
				list.addAll(this.listTestResult(failedConfig));
			}
		}
		this.sort(list);
		this.outputResult(list);
	}

	private ArrayList<ITestResult> listTestResult(IResultMap resultMap) {
		Set<ITestResult> results = resultMap.getAllResults();
		return new ArrayList<ITestResult>(results);
	}

	private void sort(List<ITestResult> list) {
		Collections.sort(list, new Comparator<ITestResult>() {
			@Override
			public int compare(ITestResult r1, ITestResult r2) {
				if (r1.getStartMillis() > r2.getStartMillis()) {
					return 1;
				} else {
					return -1;
				}
			}
		});
	}

	private void outputResult(List<ITestResult> list) {
		try {
			String path = System.getProperty("user.dir")+File.separator+"report/report.html";
			String templatePath = System.getProperty("user.dir")+File.separator+"report/template";
			BufferedWriter output = new BufferedWriter( new OutputStreamWriter(new FileOutputStream(new File(path)),"UTF-8"));
			List<ReportInfo> listInfo = new ArrayList<ReportInfo>();
			int index = 0;
			for (ITestResult result : list) {
				String tn = result.getTestContext().getCurrentXmlTest().getParameter("testCase");
				if(index==0){
					SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss.SSS");
					beginTime = formatter.format(new Date(result.getStartMillis()));
					index++;
				}
				long spendTime = result.getEndMillis() - result.getStartMillis();
				totalTime += spendTime;
				String status = this.getStatus(result.getStatus());
				List<String> log = Reporter.getOutput(result);
				Throwable throwable = result.getThrowable();
				if(throwable!=null){
					log.add(throwable.toString());
					StackTraceElement[] st = throwable.getStackTrace();
					for (StackTraceElement stackTraceElement : st) {
						log.add("    " + stackTraceElement);
					}
				}
				ReportInfo info = new ReportInfo();
				info.setName(tn);
				info.setSpendTime(spendTime+"ms");
				info.setStatus(status);
				info.setClassName(result.getInstanceName());
				info.setMethodName(result.getName());
				info.setDescription(result.getMethod().getDescription());
				info.setLog(log);
				listInfo.add(info);
			}
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("testName", name);
			result.put("testPass", testsPass);
			result.put("testFail", testsFail);
			result.put("testSkip", testsSkip);
			result.put("testAll", testsPass+testsFail+testsSkip);
			result.put("beginTime", beginTime);
			result.put("totalTime", totalTime+"ms");
			result.put("testResult", listInfo);
			Gson gson = new GsonBuilder().disableHtmlEscaping().create();
			String template = FileUtil.read(templatePath);
			template = template.replaceFirst("\\$\\{resultData\\}", gson.toJson(result));
			output.write(template);
			output.flush();
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private String getStatus(int status) {
		String statusString = null;
		switch (status) {
		case 1:
			statusString = "成功";
			break;
		case 2:
			statusString = "失败";
			break;
		case 3:
			statusString = "跳过";
			break;
		default:
			break;
		}
		return statusString;
	}
	
	public static class ReportInfo {
		
		private String name;
		
		private String className;
	
		private String methodName;
		
		private String description;
		
		private String spendTime;
				
		private String status;
		
		private List<String> log;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getClassName() {
			return className;
		}

		public void setClassName(String className) {
			this.className = className;
		}

		public String getMethodName() {
			return methodName;
		}

		public void setMethodName(String methodName) {
			this.methodName = methodName;
		}

		public String getSpendTime() {
			return spendTime;
		}

		public void setSpendTime(String spendTime) {
			this.spendTime = spendTime;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public List<String> getLog() {
			return log;
		}

		public void setLog(List<String> log) {
			this.log = log;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}
		
	}
}
