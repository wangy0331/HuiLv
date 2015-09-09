package com.wy.huilv.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.wy.huilv.R;
import com.wy.huilv.bean.Money;
import com.wy.huilv.service.HttpService;
import com.wy.huilv.tools.JsonParser;

public class MainActivity extends Activity {

	private List<Money> moneyList = null;
	
	private TextView showInput1;
	private TextView showInput2;
	private EditText et = null;
	private Button btnA = null;
	private Button btnB = null;
	private Button zhuan = null;
	//货币shuzu
	private String[] cacheMoneyArray = null;
	//汇率
	private Double currency = null;	
	String btnValA = null;
	String btnValB = null;
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
		
		showInput1 = (TextView) findViewById(R.id.msg1);
		showInput2 = (TextView) findViewById(R.id.msg2);
		
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
			//货币对调
			String btnA_val = btnA.getText().toString();
			String btnB_val = btnB.getText().toString();
			btnA.setText(btnB_val);
			btnB.setText(btnA_val);
			//计算
			currency = 1 / currency;
			showMoney();
		}
	};
	
	//触发点击事件
	View.OnClickListener onclick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			
			//判断货币种类是否缓存，如果存在  直接根据按钮显示菜单，如果不存在，执行线程获取列表
			if(cacheMoneyArray != null){
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
							InputStream in = getResources().getAssets().open("huilv.txt");
							String result = JsonParser.parserCodeList(in);
							moneyList = JsonParser.parserMoneyList(result);
							switch (clickedView.getId()) {
							case R.id.sprnner1: //A
								myHandler.sendEmptyMessage(1);
								break;
							case R.id.spinner2: //B
								myHandler.sendEmptyMessage(2);
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
	
		
	//币种
	Handler myHandler = new Handler() {
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
			case 3:
				showMoney();
				break;
			default:
				break;
			}
		}	
	};
		
		//跳出币种列表
		private void showMyAlertDialog(final Button clickedButton) {
			//判断币种列表是否缓存，有就直接跳出菜单，没有就缓存下
			
			if(cacheMoneyArray == null) {
				cacheMoneyArray = new String[moneyList.size()];
				for(int a = 0;a < moneyList.size(); a++){
					cacheMoneyArray[a] = moneyList.get(a).getName() +" "+ moneyList.get(a).getCode();
				}
			}
			
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
			builder.setIcon(android.R.drawable.ic_dialog_info);
			builder.setTitle("选择币种");
			ListAdapter catalogsAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.activity_list, cacheMoneyArray);
			builder.setAdapter(catalogsAdapter, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
							// 点击条目后的处理;
							clickedButton.setText(cacheMoneyArray[arg1]);
							
							asyncGetCurrency();
						}
					}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
		
						}
					});
			builder.show();
		}
		
	/**
	 * 货币兑换计算，并显示
	 */
	private void showMoney() {
		try {
			String amount = et.getText().toString().trim();
			Log.d("money",amount);
			float input = Float.parseFloat(amount);
			
			if (currency != null) {
				StringBuffer sb = new StringBuffer();
				sb.append(amount);
				sb.append(" ");
				sb.append(getMoneyName(btnA));
				sb.append("=");
				sb.append(input*currency);
				sb.append(" ");
				sb.append(getMoneyName(btnB));
				
				
				showInput1.setText(sb.toString());
				showInput2.setText("");
				
			} else {
				asyncGetCurrency();
			}
		} catch(Exception e) {
			Log.d("", "输入不合法");
			showInput1.setText("");
			showInput2.setText("");
		}
	}
	
	/**
	 * 异步请求汇率
	 */
	private void asyncGetCurrency() {
		final String from = getMoneyCode(btnA);
		final String to = getMoneyCode(btnB);
		new Thread(new Runnable() {
			@Override
			public void run() {
				//发送获取汇率请求
				currency = HttpService.getCurrency(from, to);
				//通知
				myHandler.sendEmptyMessage(3);
			}
		}).start();
	}
	
	/**
	 * 获取货币名称
	 * @return
	 */
	private String getMoneyName(Button btn) {
		String text = btn.getText().toString();
		text = text.substring(0,text.indexOf(" ")).trim();
		return text;
	}
	
	/**
	 * 获取货币代码
	 * @return
	 */
	private String getMoneyCode(Button btn) {
		String text = btn.getText().toString();
		text = text.substring(text.lastIndexOf(" ")).trim();
		return text;
	}
	
	/**
	 * 输入框监听器
	 */
	 private TextWatcher textWatcher = new TextWatcher() {  
	      
        @Override    
        public void afterTextChanged(Editable s) {     
        }   
          
        @Override 
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {  
        }  
 
        @Override    
        public void onTextChanged(CharSequence s, int start, int before, int count) {     
            Log.d("TAG","onTextChanged--------------->");    
            showMoney();
	    }                    
	};  
}
