package com.wy.huilv.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.wy.huilv.R;
import com.wy.huilv.bean.Money;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Spinner spinner = null;
	private ArrayAdapter<String> adapter = null;
	private List<String> list = null;
	private List<Money> moneyList = null;
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
	private Map<String, String> map = null;
	private String[] abc = null;
	private Double moneyHL = null;
	String btnValA = null;
	String btnValB = null;
	String amount ="1";
	boolean flag = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//多少钱
		et = (EditText)this.findViewById(R.id.editText1);
		// 币种1
		btnA = (Button) this.findViewById(R.id.sprnner1);
		// 币种2
		btnB = (Button) this.findViewById(R.id.spinner2);
		// 调换按钮
		zhuan = (Button) this.findViewById(R.id.zhuan);

		// 币种1  监听点击事件
		btnA.setOnClickListener(onclick);
		// 币种2  监听点击事件
		btnB.setOnClickListener(onclick);
		// 
		zhuan.setOnClickListener(onclickBtn);
		
		et.addTextChangedListener(textWatcher);


	}
	
	//币种对调
	View.OnClickListener onclickBtn = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			btnA_val = btnA.getText().toString();
			btnB_val = btnB.getText().toString();
			btnA.setText(btnB_val);
			btnB.setText(btnA_val);
			
			Log.d("HL_old", moneyHL.toString());
			
			moneyHL = 1/moneyHL;

			Log.d("HL_new", moneyHL.toString());
			
			String aaa = btnA.getText().toString();
			String bbb = btnB.getText().toString();
			if(!"".equals(et.getText().toString())){
				amount = et.getText().toString();
			}
			
			TextView tv = (TextView) findViewById(R.id.msg1);
			TextView tv2 = (TextView) findViewById(R.id.msg2);
			
			StringBuffer sb = new StringBuffer();
			sb.append(amount);
			sb.append(" ");
			sb.append(aaa.substring(0,aaa.indexOf(" ")));
			sb.append("=");
			sb.append(Double.parseDouble(amount.toString())*moneyHL);
			sb.append(" ");
			sb.append(bbb.substring(0,bbb.indexOf(" ")));
			
			
			tv.setText(sb.toString());
			tv2.setText("");
			
			
			
//			httpArg = "fromCurrency=" + aaa.substring(aaa.lastIndexOf(" ")).trim() + "&toCurrency=" + bbb.substring(bbb.lastIndexOf(" ")).trim() + "&amount=" + amount;
//			Log.d("arg",httpArg);
			
			
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					jsonResult = request(httpUrl_c, httpArg);
//					map = parserJson(jsonResult);
//					Log.d("list","");
//					myHandlerChange.sendEmptyMessage(1);
//				}
//			}).start();
		}
	};
	
	
	
	
	//触发点击事件
	View.OnClickListener onclick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			
			//判断货币种类是否缓存，如果存在  直接根据按钮显示菜单，如果不存在，执行线程获取列表
			if(abc != null){
				Button button = null;
				switch (v.getId()) {
				case R.id.sprnner1:
					button = btnA;
					break;
				case R.id.spinner2:
					button = btnB;
					break;

				default:
					break;
				}
				showMyAlertDialog(button);
			}else{
				//获取文件数据，解析json，返回一个list
				final View clickedView = v;
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							BufferedReader reader = null;
							StringBuffer sbf = new StringBuffer();
							InputStream is = getResources().getAssets().open("huilv.txt");
							reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
							
							String strRead = null;
							while ((strRead = reader.readLine()) != null) {
								sbf.append(strRead);
								sbf.append("\r\n");
							}
							reader.close();
	
							jsonResult = sbf.toString();
	
							Log.d("ddd", sbf.toString());
	
							moneyList = parserJson3(jsonResult);
							switch (clickedView.getId()) {
							case R.id.sprnner1: //A
								myHandler3.sendEmptyMessage(1);
								break;
							case R.id.spinner2: //B
								myHandler3.sendEmptyMessage(2);
								break;
							default:
								break;
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
		}
	};


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
			JSONObject json1 = new JSONObject(jsondata).getJSONObject("retData");
			
			Log.e("JSONARRAY", json1.toString());
			map = new HashMap<String, String>();
			map.put("amount", json1.getString("amount"));
			map.put("convertedamount", json1.getString("convertedamount"));
			map.put("fromCurrency", json1.getString("fromCurrency"));
			map.put("toCurrency",json1.getString("toCurrency"));
			map.put("currency",json1.getString("currency"));
			Log.d("json1",json1.getString("amount"));
			moneyHL = Double.parseDouble(json1.getString("currency"));
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
			Log.e("length", String.valueOf(json1.length()));
		
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
	
	// 解析JSON字符串----币种
		public List<Money> parserJson3(String jsondata) {
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
		
	//币种
	Handler myHandler3 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Log.d("", "--------------------Hadnler----------------");
			switch (msg.what) {
			case 1://a
				showMyAlertDialog(btnA);
				break;
			case 2://b
				showMyAlertDialog(btnB);
				break;
				
			default:
				break;
			}
		
		}	
	};
		
	
	//转换
		Handler myHandlerChange = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Log.d("", "--------------------HadnlerChange----------------");
				btnValA = btnA.getText().toString();
				btnValB = btnB.getText().toString();
				TextView tv = (TextView) findViewById(R.id.msg1);
				StringBuffer sb = new StringBuffer();
				sb.append(map.get("amount"));
				sb.append(" ");
				sb.append(btnValA.substring(0,btnValA.indexOf(" ")));
				sb.append("=");
				sb.append(map.get("convertedamount"));
				sb.append(" ");
				sb.append(btnValB.substring(0,btnValB.indexOf(" ")));
				tv.setText(sb.toString());
				TextView tv2 = (TextView) findViewById(R.id.msg2);
				tv2.setText("");
			}
		};
		
		
		//跳出币种列表
		private void showMyAlertDialog(final Button clickedButton) {
			//判断币种列表是否缓存，有就直接跳出菜单，没有就缓存下
			
			if(abc == null) {
				abc = new String[moneyList.size()];
				for(int a = 0;a < moneyList.size(); a++){
					abc[a] = moneyList.get(a).getName() +" "+ moneyList.get(a).getCode();
				}
			}
			
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
			builder.setIcon(android.R.drawable.ic_dialog_info);
			builder.setTitle("选择币种");
			ListAdapter catalogsAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.activity_list, abc);
			builder.setAdapter(catalogsAdapter,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
							// 点击条目后的处理;
							clickedButton.setText(abc[arg1]);
							
							//获取2个币种
							String aaa = btnA.getText().toString();
							String bbb = btnB.getText().toString();
							if(!"".equals(et.getText().toString())){
								amount = et.getText().toString();
							}
							
							//发送请求
							httpArg = "fromCurrency=" + aaa.substring(aaa.lastIndexOf(" ")).trim() + "&toCurrency=" + bbb.substring(bbb.lastIndexOf(" ")).trim() + "&amount=" + amount;
							Log.d("arg",httpArg);
							
							
							new Thread(new Runnable() {
								@Override
								public void run() {
									jsonResult = request(httpUrl_c, httpArg);
									map = parserJson(jsonResult);
									Log.d("list","");
									myHandlerChange.sendEmptyMessage(1);
								}
							}).start();
							
						}
					}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
		
						}
					});
			builder.show();
		}

//		@Override
//		public void run() {
//			try {
//				BufferedReader reader = null;
//				StringBuffer sbf = new StringBuffer();
//				InputStream is = getResources().getAssets().open("huilv.txt");
//				reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
//				
//				String strRead = null;
//				while ((strRead = reader.readLine()) != null) {
//					sbf.append(strRead);
//					sbf.append("\r\n");
//				}
//				reader.close();
//
//				jsonResult = sbf.toString();
//
//				Log.d("ddd", sbf.toString());
//
//				moneyList = parserJson3(jsonResult);
//				
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
		
		 private TextWatcher textWatcher = new TextWatcher() {  
	          
		        @Override    
		        public void afterTextChanged(Editable s) {     
		            // TODO Auto-generated method stub     
//		            Log.d("TAG","afterTextChanged--------------->");   
		        }   
		          
		        @Override 
		        public void beforeTextChanged(CharSequence s, int start, int count,  
		                int after) {  
		            // TODO Auto-generated method stub  
//		            Log.d("TAG","beforeTextChanged--------------->");  
		            
		        }  
		 
		         @Override    
		        public void onTextChanged(CharSequence s, int start, int before,     
		                int count) {     
		            Log.d("TAG","onTextChanged--------------->");    
		            String aaa = btnA.getText().toString();
					String bbb = btnB.getText().toString();
					Log.d("et1",et.getText().toString());
					if(!"".equals(et.getText().toString()) && !".".equals(et.getText().toString())){
						amount = et.getText().toString();
					
					Log.d("et", amount);
					
					if(moneyHL != null){
						Log.d("HL",moneyHL.toString());
						TextView tv = (TextView) findViewById(R.id.msg1);
						TextView tv2 = (TextView) findViewById(R.id.msg2);
						
						StringBuffer sb = new StringBuffer();
						sb.append(amount);
						sb.append(" ");
						sb.append(aaa.substring(0,aaa.indexOf(" ")));
						sb.append("=");
						sb.append(Double.parseDouble(amount.toString())*moneyHL);
						sb.append(" ");
						sb.append(bbb.substring(0,bbb.indexOf(" ")));
						
						
						tv.setText(sb.toString());
						tv2.setText("");
						
					}else{
						
					
					httpArg = "fromCurrency=" + aaa.substring(aaa.lastIndexOf(" ")).trim() + "&toCurrency=" + bbb.substring(bbb.lastIndexOf(" ")).trim() + "&amount=" + amount;
					Log.d("arg",httpArg);
				
						new Thread(new Runnable() {
							@Override
							public void run() {
								jsonResult = request(httpUrl_c, httpArg);
								map = parserJson(jsonResult);
								Log.d("list","");
								myHandlerChange.sendEmptyMessage(1);
							}
						}).start();
					}  
					}else{
						TextView tv = (TextView) findViewById(R.id.msg1);
						TextView tv2 = (TextView) findViewById(R.id.msg2);
						tv.setText("");
						tv2.setText("");
					}
		        }                    
		    };  
}
