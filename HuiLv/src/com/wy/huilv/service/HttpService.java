package com.wy.huilv.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

import com.wy.huilv.tools.JsonParser;

public class HttpService {
	private static final String httpUrlCurrency = "http://apis.baidu.com/apistore/currencyservice/currency";

	/**
	 * 获取汇率
	 * @param httpArg
	 * @return
	 */
	public static double getCurrency(String from, String to) {
		String httpArg = String.format("fromCurrency=%s&toCurrency=%s&amount=1", from, to);
		return JsonParser.parserCurrency(get(httpUrlCurrency, httpArg));
	}
	
	/**
	 * @param urlAll
	 *            :请求接口
	 * @param httpArg
	 *            :参数
	 * @return 返回结果
	 */
	public static String get(String httpUrl, String httpArg) {
		BufferedReader reader = null;
		String result = null;
		StringBuffer sbf = new StringBuffer();
		httpUrl = httpUrl + "?" + httpArg;

		try {
			URL url = new URL(httpUrl);
			Log.e("Url", url.toString());
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("GET");
			// 填入apikey到HTTP header
			connection.setRequestProperty("apikey",
					"4cfb905cc67176a11b0bb70a853279e3");
			connection.connect();
			InputStream is = connection.getInputStream();
			reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String strRead = null;
			while ((strRead = reader.readLine()) != null) {
				sbf.append(strRead);
				sbf.append("\r\n");
			}
			reader.close();
			result = sbf.toString();
		} catch (Exception e) {
			Log.e("catch", e.toString());
		}
		return result;
	}
}
