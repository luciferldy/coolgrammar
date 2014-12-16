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
	private TextView time_test_title;
	private RadioButton time_test_answer_first = null;
	private RadioButton time_test_answer_second = null;
	private RadioButton time_test_answer_third = null;
	private RadioButton time_test_answer_fourth = null;
	private ImageButton time_test_prepage = null;
	private ImageButton time_test_nextpage = null;
	private ImageButton time_test_exit = null;
	private RadioGroup time_test_answersgroup = null;
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
	private int time_test_hour = 0;
	private int time_test_minute = 0;
	private int time_test_second = 0;
	
	//泛型数组+哈希图。存储关于题号，，题目以及你的答案
	private ArrayList<HashMap<String, Object>> test_lib = null;
	//存储错题
	private ArrayList<HashMap<String, Object>> error_lib = null;
	
	//数据库需要用到的一些东西
	private SQLiteDatabase mdb_2 = null;
	private Context context_2 = null;
	private MyDBHelper myDbHelper_2  = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.time_test);
		
		test_lib = new ArrayList<HashMap<String, Object>>();
		error_lib = new ArrayList<HashMap<String, Object>>();
		
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
		getTestContent(test_lib.get(item_count).get("id").toString());
		setViewContent();
		
		
		//向前与向后翻页
		time_test_prepage.setOnClickListener(new onTurnPageListener(0, this));
		time_test_nextpage.setOnClickListener(new onTurnPageListener(1, this));
		
		//返回键
		time_test_exit.setOnClickListener(new View.OnClickListener() {
			
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
		time_test_hour = t.hour;
		time_test_minute = t.minute;
		time_test_second = t.second;
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
				test_lib.add(hashmap2_1);
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
						time_test_nextpage.setImageResource(drawable.ic_action_accept);
						time_test_nextpage.getBackground().setAlpha(0);
					}
					
					//将错误选项加入ArrayList+HashMap中
					addWrongItem(context, time_test_answersgroup.getCheckedRadioButtonId());
					
					//获取内容，设置内容
					getTestContent(test_lib.get(++item_count).get("id").toString());
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
						time_test_nextpage.setImageResource(drawable.ic_action_forward);
						time_test_nextpage.getBackground().setAlpha(0);
					}
					getTestContent(test_lib.get(--item_count).get("id").toString());
					setViewContent();
				}
				
			}
			
		}
		
		public void LastItemJump(){
			
			//将错误选项加入ArrayList+HashMap中
			addWrongItem(context, time_test_answersgroup.getCheckedRadioButtonId());
			
			//计时
			Time t = new Time();
			t.setToNow();
			time_test_hour = t.hour-time_test_hour;
			// 判断是否出现了秒针比较小的情况
			if (t.second<time_test_second) {
				time_test_second = t.second-time_test_second+60;
				time_test_minute = t.minute-time_test_minute-1;
			}
			else {
				time_test_minute = t.minute-time_test_minute;
				time_test_second = t.second-time_test_second;
			}
			
			//传递数据，实现跳转
			Intent intent2_1 = new Intent(TimeTest.this, ErrorAnalyse.class);
			intent2_1.putExtra("test_number", item_number);
			intent2_1.putExtra("test_level", test_level);
			intent2_1.putExtra("item_list",error_lib);
			intent2_1.putExtra("hour", time_test_hour);
			intent2_1.putExtra("minute", time_test_minute);
			intent2_1.putExtra("second", time_test_second);
			
			startActivity(intent2_1);
		}
		
		//获得被选择的项目，并且将其放入HashMap中
		public void addWrongItem(Context context, int ischecked){
			HashMap<String, Object> hashmap2_2 = null;
			//将错误的选项存入HashMap中,并存入error_lib中方便数据传递
			switch(ischecked){
			case R.id.time_test_answer_first:
				if(test_lib.get(item_count).get("answer").toString().equals("A")){
					break;
				}
				else {
					hashmap2_2 = new HashMap<String, Object>();
					hashmap2_2.put("item_order", item_count+1);
					hashmap2_2.put("item_id", test_lib.get(item_count).get("id").toString());
					hashmap2_2.put("item_reply", "A");
					error_lib.add(hashmap2_2);
					System.out.println("加入错题库！");
				}
				break;
			case R.id.time_test_answer_second:
				if(test_lib.get(item_count).get("answer").toString().equals("B")){
					break;
				}
				else {
					hashmap2_2 = new HashMap<String, Object>();
					hashmap2_2.put("item_order", item_count+1);
					hashmap2_2.put("item_id", test_lib.get(item_count).get("id").toString());
					hashmap2_2.put("item_reply", "B");
					error_lib.add(hashmap2_2);
					System.out.println("加入错题库！");
				}
				break;
			case R.id.time_test_answer_third:
				if(test_lib.get(item_count).get("answer").toString().equals("C")){
					break;
				}
				else {
					hashmap2_2 = new HashMap<String, Object>();
					hashmap2_2.put("item_order", item_count+1);
					hashmap2_2.put("item_id", test_lib.get(item_count).get("id").toString());
					hashmap2_2.put("item_reply", "C");
					error_lib.add(hashmap2_2);
					System.out.println("加入错题库！");
				}
				break;
			case R.id.time_test_answer_fourth:
				if(test_lib.get(item_count).get("answer").toString().equals("D")){
				}
				else {
					hashmap2_2 = new HashMap<String, Object>();
					hashmap2_2.put("item_order", item_count+1);
					hashmap2_2.put("item_id", test_lib.get(item_count).get("id").toString());
					hashmap2_2.put("item_reply", "D");
					error_lib.add(hashmap2_2);
					System.out.println("加入错题库！");
				}
				break;
			case -1:
				
				hashmap2_2 = new HashMap<String, Object>();
				hashmap2_2.put("item_order", item_count+1);
				hashmap2_2.put("item_id", test_lib.get(item_count).get("id").toString());
				hashmap2_2.put("item_reply", "N");
				error_lib.add(hashmap2_2);
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
		
		time_test_title = (TextView)findViewById(R.id.time_test_title);
		time_test_answersgroup = (RadioGroup)findViewById(R.id.time_test_answersgroup);
		progressBarInTT = (ProgressBar)findViewById(R.id.progressBarInTT);
		
		time_test_answer_first = (RadioButton)findViewById(R.id.time_test_answer_first);
		time_test_answer_second = (RadioButton)findViewById(R.id.time_test_answer_second);
		time_test_answer_third = (RadioButton)findViewById(R.id.time_test_answer_third);
		time_test_answer_fourth = (RadioButton)findViewById(R.id.time_test_answer_fourth);
		
		time_test_prepage = (ImageButton)findViewById(R.id.time_test_prepage);
		time_test_nextpage = (ImageButton)findViewById(R.id.time_test_nextpage);
		time_test_exit = (ImageButton)findViewById(R.id.time_test_exit);
		
		//只有一道题，将其改为提交
		if(item_number==1){
			time_test_nextpage.setImageResource(drawable.ic_action_accept);
			time_test_nextpage.getBackground().setAlpha(0);
		}
	}
	
	//设置各个视图显示的内容
	public void setViewContent(){
		
		progressBarInTT.setProgress(progress);
		time_test_title.setText("问题"+"："+(item_count+1)+"."+question);
		
		time_test_answer_first.setText(a);
		time_test_answer_second.setText(b);
		time_test_answer_third.setText(c);
		time_test_answer_fourth.setText(d);
		
		time_test_answersgroup.clearCheck();
		
	}
}