/*
 * 编号：2
 * 功能：显示测试界面的Activity
 * 时间：2014年8月6日
 * 最近修改的时间：8月23日
 */
package com.example.coolgrammer;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.coolgrammer.R.drawable;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class TimeTest extends Activity{
	
	//各个视图
	private TextView textView1 = null;
	private RadioButton radiobtn1 = null;
	private RadioButton radiobtn2 = null;
	private RadioButton radiobtn3 = null;
	private RadioButton radiobtn4 = null;
	private ImageButton button4_3 = null;
	private ImageButton button4_4 = null;
	private ImageButton button4_5 = null;
	private RadioGroup radiogrp2_1 = null;
	private ProgressBar progressBarInTT = null;
	
	//各个变量
	private String question = null;
	private String a = null;
	private String b = null;
	private String c = null;
	private String d = null;
	private int progress = 0;
	
	//等级
	private int test_level = 0;
	
	//计数与数量
	private int item_count = 0;
	private int item_number = 0;
	
	//时间计时
	private int hour_2 = 0;
	private int minute_2 = 0;
	private int second_2 = 0;
	
	//泛型数组+哈希图。存储关于题号，，题目以及你的答案
	private ArrayList<HashMap<String, Object>> list2_1 = null;
	//存储错题
	private ArrayList<HashMap<String, Object>> list2_2 = null;
	
	//数据库需要用到的一些东西
	private SQLiteDatabase mdb_2 = null;
	private Context context_2 = null;
	private MyDBHelper myDbHelper_2  = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.time_test);
		
		list2_1 = new ArrayList<HashMap<String, Object>>();
		list2_2 = new ArrayList<HashMap<String, Object>>();
		
		try{
			//获得从 Buddle 处传过来的数据，以键名来获得值
			Bundle bundle_2 = this.getIntent().getExtras();
			
			test_level = bundle_2.getInt("level");
			getAllTest(bundle_2.getString("libraryId"));
			setTitle(bundle_2.getString("title"));
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("接收数据处的问题！");
		}
		
		//获取时间，相当于计时
		getSystemTime();
	
		getViewId();
		
		//显示语法的内容
		getTestContent(list2_1.get(item_count).get("id").toString());
		setViewContent();
		
		
		//向前与向后翻页
		button4_3.setOnClickListener(new onTurnPageListener(0, this));
		button4_4.setOnClickListener(new onTurnPageListener(1, this));
		
		//返回键
		button4_5.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				confirmExit();			
			}
		});
		
	}
	
	//重写系统的返回函数
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		//按下返回键
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
			confirmExit();
			return true;
		}
		else {
			return super.onKeyDown(keyCode, event);
		}
	}
	
	//弹出确认退出的对话框函数
	protected void confirmExit(){
		AlertDialog.Builder builder = new Builder(TimeTest.this);
		builder.setMessage("中途退出的话成绩作废");
		builder.setTitle("提示");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				//这个是对话框要消失时的对话框
				arg0.dismiss();
				Intent intent = new Intent(TimeTest.this, MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
			}
		});
		
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				arg0.dismiss();
			}
		});
		builder.create().show();
	}
	
	//获取时间
	public void getSystemTime(){
		Time t = new Time();
		t.setToNow();
		hour_2 = t.hour;
		minute_2 = t.minute;
		second_2 = t.second;
	}
	
	//获取所有题目的id，并且存在泛型数组中
	public void getAllTest(String libraryid){
		//从数据库中获取内容
		Cursor cursor_2 = null;
		try{
			context_2 = (Context)this;
			myDbHelper_2 = new MyDBHelper(context_2);
			mdb_2 =myDbHelper_2.getReadableDatabase();	
			cursor_2 = mdb_2.query("TestItem", new String[] {"id","answer"}, "libraryId="+libraryid, null, null, null, null);
			
			//题目总数
			item_number = cursor_2.getCount();
			//声明一个HashMap
			HashMap<String,Object> hashmap2_1 = null;
			//将每道题的id，answer都存到list_2中
			while(cursor_2.moveToNext()){
				hashmap2_1 = new HashMap<String, Object>();
				hashmap2_1.put("id", cursor_2.getString(cursor_2.getColumnIndex("id")));
				hashmap2_1.put("answer", cursor_2.getString(cursor_2.getColumnIndex("answer")));
				list2_1.add(hashmap2_1);
			}
			//从指针处提取数据
			
		}
		catch (Exception e){
			e.printStackTrace();
			Toast.makeText(this, "something wrong in test content", Toast.LENGTH_SHORT).show();
		}
		finally{
			cursor_2.close();
			mdb_2.close();
		}
	}
		
	//监听向上，向下翻页的按钮
	public class onTurnPageListener implements View.OnClickListener{
		
		//判断向前翻还是向后翻
		private int sw = 0;
		private Context context=null;
		
		public onTurnPageListener(int i, Context c){
			sw=i;
			context=c;
		}
		
		@Override
		public void onClick(View arg0){
			
			//向后翻页
			if( sw == 1 ){
				
				//最后一题实现跳转
				if(item_count == (item_number-1)) {
					LastItemJump();
				}	
				else{
					//如果下一题是最后一题，那么下一题改为  提交
					if(item_count == (item_number-2)){
						button4_4.setImageResource(drawable.ic_action_accept);
						button4_4.getBackground().setAlpha(0);
					}
					
					//将错误选项加入ArrayList+HashMap中
					addWrongItem(context, radiogrp2_1.getCheckedRadioButtonId());
					
					//获取内容，设置内容
					getTestContent(list2_1.get(++item_count).get("id").toString());
					setViewContent();
				}	
				
			}
			//向前翻页
			else{
				if(item_count == 0) {
					Toast.makeText(context, "已经是第一题了", Toast.LENGTH_SHORT).show();
				}
				else{
					if(item_count == (item_number-1)){
						button4_4.setImageResource(drawable.ic_action_forward);
						button4_4.getBackground().setAlpha(0);
					}
					getTestContent(list2_1.get(--item_count).get("id").toString());
					setViewContent();
				}
				
			}
			
		}
		
		public void LastItemJump(){
			
			//将错误选项加入ArrayList+HashMap中
			addWrongItem(context, radiogrp2_1.getCheckedRadioButtonId());
			
			//计时
			Time t = new Time();
			t.setToNow();
			hour_2 = t.hour-hour_2;
			// 判断是否出现了秒针比较小的情况
			if (t.second<second_2) {
				second_2 = t.second-second_2+60;
				minute_2 = t.minute-minute_2-1;
			}
			else {
				minute_2 = t.minute-minute_2;
				second_2 = t.second-second_2;
			}
			
			//传递数据，实现跳转
			Intent intent2_1 = new Intent(TimeTest.this, ErrorAnalyse.class);
			intent2_1.putExtra("test_number", item_number);
			intent2_1.putExtra("test_level", test_level);
			intent2_1.putExtra("item_list",list2_2);
			intent2_1.putExtra("hour", hour_2);
			intent2_1.putExtra("minute", minute_2);
			intent2_1.putExtra("second", second_2);
			
			startActivity(intent2_1);
		}
		
		//获得被选择的项目，并且将其放入HashMap中
		public void addWrongItem(Context context, int ischecked){
			HashMap<String, Object> hashmap2_2 = null;
			//将错误的选项存入HashMap中,并存入list2_2中方便数据传递
			switch(ischecked){
			case R.id.radiobutton4_1:
				if(list2_1.get(item_count).get("answer").toString().equals("A")){
					break;
				}
				else {
					hashmap2_2 = new HashMap<String, Object>();
					hashmap2_2.put("item_order", item_count+1);
					hashmap2_2.put("item_id", list2_1.get(item_count).get("id").toString());
					hashmap2_2.put("item_reply", "A");
					list2_2.add(hashmap2_2);
					System.out.println("加入错题库！");
				}
				break;
			case R.id.radiobutton4_2:
				if(list2_1.get(item_count).get("answer").toString().equals("B")){
					break;
				}
				else {
					hashmap2_2 = new HashMap<String, Object>();
					hashmap2_2.put("item_order", item_count+1);
					hashmap2_2.put("item_id", list2_1.get(item_count).get("id").toString());
					hashmap2_2.put("item_reply", "B");
					list2_2.add(hashmap2_2);
					System.out.println("加入错题库！");
				}
				break;
			case R.id.radiobutton4_3:
				if(list2_1.get(item_count).get("answer").toString().equals("C")){
					break;
				}
				else {
					hashmap2_2 = new HashMap<String, Object>();
					hashmap2_2.put("item_order", item_count+1);
					hashmap2_2.put("item_id", list2_1.get(item_count).get("id").toString());
					hashmap2_2.put("item_reply", "C");
					list2_2.add(hashmap2_2);
					System.out.println("加入错题库！");
				}
				break;
			case R.id.radiobutton4_4:
				if(list2_1.get(item_count).get("answer").toString().equals("D")){
				}
				else {
					hashmap2_2 = new HashMap<String, Object>();
					hashmap2_2.put("item_order", item_count+1);
					hashmap2_2.put("item_id", list2_1.get(item_count).get("id").toString());
					hashmap2_2.put("item_reply", "D");
					list2_2.add(hashmap2_2);
					System.out.println("加入错题库！");
				}
				break;
			case -1:
				
				hashmap2_2 = new HashMap<String, Object>();
				hashmap2_2.put("item_order", item_count+1);
				hashmap2_2.put("item_id", list2_1.get(item_count).get("id").toString());
				hashmap2_2.put("item_reply", "N");
				list2_2.add(hashmap2_2);
				Toast.makeText(context, "No choice is Wrong!", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	
	}
	
	//获取语法的内容	
	public void getTestContent(String id){
		
		//从数据库中获取内容
		try{
			context_2 = (Context)this;
			myDbHelper_2 = new MyDBHelper(context_2);
			mdb_2 =myDbHelper_2.getReadableDatabase();	
			Cursor cursor_2 = mdb_2.query("TestItem", new String[] {"question", "a", "b", 
					"c", "d", "answer", "why"}, "id="+id, null, null, null, null);
			
			//定位到题中
			cursor_2.moveToNext();
			
			//从指针处提取数据
			question = cursor_2.getString(cursor_2.getColumnIndex("question"));
			a = cursor_2.getString(cursor_2.getColumnIndex("a"));
			b = cursor_2.getString(cursor_2.getColumnIndex("b"));
			c = cursor_2.getString(cursor_2.getColumnIndex("c"));
			d = cursor_2.getString(cursor_2.getColumnIndex("d"));
			
			progress = 100*(item_count+1)/item_number;
		}
		catch (Exception e){
			e.printStackTrace();
			Toast.makeText(this, "something wrong in test content", Toast.LENGTH_SHORT).show();
		}
		finally{
			mdb_2.close();
		}
		
	}
	
	//获取各个视图的ID
	public void getViewId(){
		
		textView1 = (TextView)findViewById(R.id.textView4_1);
		radiogrp2_1 = (RadioGroup)findViewById(R.id.radiogroup4_1);
		progressBarInTT = (ProgressBar)findViewById(R.id.progressBarInTT);
		
		radiobtn1 = (RadioButton)findViewById(R.id.radiobutton4_1);
		radiobtn2 = (RadioButton)findViewById(R.id.radiobutton4_2);
		radiobtn3 = (RadioButton)findViewById(R.id.radiobutton4_3);
		radiobtn4 = (RadioButton)findViewById(R.id.radiobutton4_4);
		
		button4_3 = (ImageButton)findViewById(R.id.button4_3);
		button4_4 = (ImageButton)findViewById(R.id.button4_4);
		button4_5 = (ImageButton)findViewById(R.id.button4_5);
		
		//只有一道题，将其改为提交
		if(item_number==1){
			button4_4.setImageResource(drawable.ic_action_accept);
			button4_4.getBackground().setAlpha(0);
		}
	}
	
	//设置各个视图显示的内容
	public void setViewContent(){
		
		progressBarInTT.setProgress(progress);
		textView1.setText("问题"+"："+(item_count+1)+"."+question);
		
		radiobtn1.setText(a);
		radiobtn2.setText(b);
		radiobtn3.setText(c);
		radiobtn4.setText(d);
		
		radiogrp2_1.clearCheck();
		
	}
}