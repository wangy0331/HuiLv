package com.wy.huilv;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Spinner spinner = null;
	private ArrayAdapter<String> adapter = null;
	private List<String> list = null;
	private Dialog alertDialogA = null;
	private Dialog alertDialogB = null;
	private String[] arrayList = null;
	private EditText et = null;
	private Button btn = null;
	private Button btnA = null;
	private Button btnB = null;
	private Button zhuan = null;
	private String btnA_val = null;
	private String btnB_val = null;
	private String jsonResult = null;
	private String httpUrl = "http://apis.baidu.com/apistore/currencyservice/type";
	private String httpUrl_c = "http://apis.baidu.com/apistore/currencyservice/currency";
	private String httpArg = "";
	private Message msg = null;
	private Map<String, String> map = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//多少钱
		et = (EditText)this.findViewById(R.id.editText1);
		// 转换按钮
		btn = (Button) this.findViewById(R.id.btn2);
		// 币种1
		btnA = (Button) this.findViewById(R.id.sprnner1);
		// 币种2
		btnB = (Button) this.findViewById(R.id.spinner2);
		// 调换按钮
		zhuan = (Button) this.findViewById(R.id.zhuan);

		// 币种1
		btnA.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						jsonResult = request(httpUrl, httpArg);
						arrayList = parserJson2(jsonResult);
//						msg = new Message();
//						msg.what = 1;
						myHandler.sendEmptyMessage(1);
					}
				}).start();
			}
		});

		// 币种2
		btnB.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						jsonResult = request(httpUrl, httpArg);
						arrayList = parserJson2(jsonResult);
//						msg = new Message();
//						msg.what = 2;
						myHandler2.sendEmptyMessage(1);
					}
				}).start();
			}
		});

		// 调换
		zhuan.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				btnA_val = btnA.getText().toString();
				btnB_val = btnB.getText().toString();

				btnA.setText(btnB_val);
				btnB.setText(btnA_val);
			}
		});

		// 转换按钮点击事件
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.e("Main", "Main");
//				TextView tv = (TextView) findViewById(R.id.msg1);
//				tv.setText("100人名币=100美元");
//				TextView tv2 = (TextView) findViewById(R.id.msg2);
//				tv2.setText("");
//				EditText et = (EditText) findViewById(R.id.editText1);
//				Log.e("EditText", et.getText().toString());
//				tv.setText(et.getText());
				// startActivity(it);
				
				httpArg = "fromCurrency=" + btnA.getText() + "&toCurrency=" + btnB.getText() + "&amount=" + et.getText();
				Log.d("arg",httpArg);
			
					new Thread(new Runnable() {
						@Override
						public void run() {
							jsonResult = request(httpUrl_c, httpArg);
						//	String ab = "fromCurrency=CNY&toCurrency=USD&amount=2";
							map = parserJson(jsonResult);
//							msg = new Message();
//							msg.what = 2;
							Log.d("list","");
							myHandlerChange.sendEmptyMessage(1);
						}
					}).start();
			
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * @param urlAll
	 *            :请求接口
	 * @param httpArg
	 *            :参数
	 * @return 返回结果
	 */
	public static String request(String httpUrl, String httpArg) {
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

	// 解析JSON字符串----转换
	public Map<String, String> parserJson(String jsondata) {
		Map<String, String> map = null;
		try {
			// 构建JSON数组对象
//			JSONArray json1 = new JSONObject(jsondata).getJSONArray("retData");
			
			JSONObject json1 = new JSONObject(jsondata).getJSONObject("retData");
			
			Log.e("JSONARRAY", json1.toString());
			map = new HashMap<String, String>();
			map.put("amount", json1.getString("amount"));
			map.put("convertedamount", json1.getString("convertedamount"));
			map.put("fromCurrency", json1.getString("fromCurrency"));
			map.put("toCurrency",json1.getString("toCurrency"));
//			for (int i = 0; i < json1.length(); i++) {
////				JSONObject jsonObj2 = json1.optJSONObject(i);
////				String huobi = (String)json1.get(i);
////				Log.i("JSONDATA", huobi);
////				map.add(huobi);
//			}
			Log.d("json1",json1.getString("amount"));
			return map;
		} catch (Exception e) {
			Log.d("EX", e.toString(), e);
		}
		if (map == null) {
			return new HashMap<String,String>();
		}
		return map;
	}

	// 解析JSON字符串----币种
	public String[] parserJson2(String jsondata) {
		String[] data = null;
		try {
			// 构建JSON数组对象
			JSONArray json1 = new JSONObject(jsondata).getJSONArray("retData");
			Log.e("JSONARRAY", json1.toString());

			data = new String[json1.length()];
			for (int i = 0; i < json1.length(); i++) {
				String huobi = (String) json1.get(i);
				data[i] = huobi;
			}
			Log.d("", "" + data.length);
			return data;
		} catch (Exception e) {
			Log.e("error", e.toString(), e);
		}
		if (data == null) {
			return new String[] {};
		}
		return data;
	}

	
	//币种2
	Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Log.d("", "--------------------Hadnler----------------");
			alertDialogA = new AlertDialog.Builder(MainActivity.this)
					.setTitle("请选择货币")
					.setIcon(R.drawable.ic_launcher)
					.setItems(arrayList, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Toast.makeText(MainActivity.this, arrayList[which],
									Toast.LENGTH_SHORT).show();
//							Log.d("msgAlert",String.valueOf((msgAlert.what == 1)));
//							if(msgAlert.what == 1){
								btnA.setText(arrayList[which]);
//							}else if(msgAlert.what == 2){
//								btnB.setText(arrayList[which]);
//							}
							
							//btnA.setText(arrayList[which]);
							// TextView tv2 = (TextView)findViewById(R.id.msg2);
							// tv2.setText("");
						}
					})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

								}
							}).create();
			alertDialogA.show();
		}

	};
	
	//币种2
	Handler myHandler2 = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Log.d("", "--------------------Hadnler----------------");
			alertDialogA = new AlertDialog.Builder(MainActivity.this)
					.setTitle("请选择货币")
					.setIcon(R.drawable.ic_launcher)
					.setItems(arrayList, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Toast.makeText(MainActivity.this, arrayList[which],
									Toast.LENGTH_SHORT).show();
//							Log.d("msgAlert",String.valueOf((msgAlert.what == 1)));
//							if(msgAlert.what == 1){
//								btnA.setText(arrayList[which]);
//							}else if(msgAlert.what == 2){
								btnB.setText(arrayList[which]);
//							}
							
							//btnA.setText(arrayList[which]);
							// TextView tv2 = (TextView)findViewById(R.id.msg2);
							// tv2.setText("");
						}
					})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

								}
							}).create();
			alertDialogA.show();
		}
	};
	
	//转换
		Handler myHandlerChange = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Log.d("", "--------------------HadnlerChange----------------");
				
				TextView tv = (TextView) findViewById(R.id.msg1);
				StringBuffer sb = new StringBuffer();
				sb.append(map.get("amount"));
				sb.append(map.get("fromCurrency"));
				sb.append("=");
				sb.append(map.get("convertedamount"));
				sb.append(map.get("toCurrency"));
				tv.setText(sb.toString());
				TextView tv2 = (TextView) findViewById(R.id.msg2);
				tv2.setText("");
//				EditText et = (EditText) findViewById(R.id.editText1);
//				Log.e("EditText", et.getText().toString());
//				tv.setText(et.getText());
			}
		};
}
