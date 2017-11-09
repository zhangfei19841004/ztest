package com.test.testcase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.gson.Gson;

public class FileUtil {

	public static String read(String path) {
		File file = new File(path);
		InputStream is = null;
		StringBuffer sb = new StringBuffer();
		try {
			is = new FileInputStream(file);
			int index = 0;
			byte[] b = new byte[1024];
			while ((index = is.read(b)) != -1) {
				sb.append(new String(b, 0, index));
			}
			return sb.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static void write(String path, String content) {
		File file = new File(path);
		OutputStream os = null;
		try {
			os = new FileOutputStream(file);
			os.write(content.getBytes());
			os.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null) {
					os.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	public static void replace(){
		String template = FileUtil.read("E:\\report\\template.html");
		String[] s = new String[]{"a","b","c"};
		Gson gson = new Gson();
		String json = gson.toJson(s);
		template = template.replaceFirst("\\$\\{arr\\}", json);
		FileUtil.write("E:\\report\\report.html", template);
		System.out.println(template);
	}
	
	public static void main(String[] args) {
		FileUtil.replace();
	}

}
