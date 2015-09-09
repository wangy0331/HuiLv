package com.wy.huilv.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.wy.huilv.bean.Money;

public class JsonParser {
	
	/**
	 * 解析JSON字符串----汇率
	 * @param jsondata
	 * @return
	 */
	public static double parserCurrency(String jsondata) {
		Map<String, String> map = null;
		try {
			JSONObject json1 = new JSONObject(jsondata).getJSONObject("retData");
			map = new HashMap<String, String>();
			map.put("amount", json1.getString("amount"));
			map.put("convertedamount", json1.getString("convertedamount"));
			map.put("fromCurrency", json1.getString("fromCurrency"));
			map.put("toCurrency",json1.getString("toCurrency"));
			map.put("currency",json1.getString("currency"));
			Log.d("json1",json1.getString("amount"));
			return Double.parseDouble(json1.getString("currency"));
		} catch (Exception e) {
			Log.d("EX", e.toString(), e);
		}
		return 0;
	}
	
	/**
	 * 解析JSON字符串----货币列表
	 * @param jsondata
	 * @return
	 */
	public static List<Money> parserMoneyList(String jsondata) {
		List<Money> list = null;
		Money money = null;
		try {
			// 构建JSON数组对象
			list = new ArrayList<Money>();
			JSONArray json1 = new JSONObject(jsondata).getJSONArray("retData");
			Log.e("JSONARRAY", json1.toString());
			Log.e("length", String.valueOf(json1.length()));
		
			for (int i = 0; i < json1.length(); i++) {
				money = new Money();
				JSONObject oj = json1.getJSONObject(i);  
	            money.setName(oj.getString("name"));
	            money.setCode(oj.getString("code"));
				list.add(money);
	                
	            Log.d("code", json1.get(i).toString());		                
			}
			return list;
		} catch (Exception e) {
			Log.e("error", e.toString(), e);
		}
		return list;
	}
}
