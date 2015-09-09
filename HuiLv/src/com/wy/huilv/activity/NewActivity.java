package com.wy.huilv.activity;

import com.wy.huilv.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class NewActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new);
//		final String[] arrayList = new String[]{"日元","美元","人民币","英镑","欧元","韩元"};
//		 Dialog alertDialog = new AlertDialog.Builder(this).   
//	                setTitle("请选择货币").   
//	                setIcon(R.drawable.ic_launcher)   
//	                .setItems(arrayList, new DialogInterface.OnClickListener() {   
//	    
//	                    @Override   
//	                    public void onClick(DialogInterface dialog, int which) {   
//	                        Toast.makeText(NewActivity.this, arrayList[which], Toast.LENGTH_SHORT).show();   
//	                    }   
//	                }).   
//	                setNegativeButton("取消", new DialogInterface.OnClickListener() {   
//	   
//	                    @Override   
//	                    public void onClick(DialogInterface dialog, int which) {   
//	                        // TODO Auto-generated method stub    
//	                    }   
//	                }).   
//	                create();   
//	        alertDialog.show(); 
		
		 Dialog alertDialog = new AlertDialog.Builder(this).   
	                setTitle("对话框的标题").   
	                setMessage("对话框的内容").   
	                setIcon(R.drawable.ic_launcher).   
	                create();   
	        alertDialog.show();
		
//		Button bt = (Button)findViewById(R.id.xin);
//			bt.setOnClickListener(new View.OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					Intent it = new Intent(NewActivity.this,MainActivity.class);
//					startActivity(it);
//				}
//			});
	}

}
