/*
 * 编号：3
 * 功能：显示错题解析界面的Activity
 * 创建时间：2014年8月27日
 */
package com.example.coolgrammer;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.coolgrammer.R.drawable;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class ErrorAnalyse extends Activity{
	
	//各个控件，写的有点麻烦了
	private TextView textView1 = null;//题目
	private TextView textView2 = null;//答案
	private TextView textView3 = null;//解析
	
	private TextView textView4 = null; //正确率
	private TextView textView6 = null; //时间
	
	private RadioButton radiobtn1 = null;
	private RadioButton radiobtn2 = null;
	private RadioButton radiobtn3 = null;
	private RadioButton radiobtn4 = null;
	
	
	private ImageButton button5_3 = null;
	private ImageButton button5_4 = null;
	private ImageButton button5_5 = null;
	private ImageButton btn_backto_main = null;
	private Button button5_6 = null;
	
	private RatingBar ratingBar5_1 = null;
	//各个变量
	private String question = null;
	private String a = null;
	private String b = null;
	private String c = null;
	private String d = null;
	private String answer = null;
	private String why = null;
	
	//计数与数量
	private int test_number = 0;
	private int item_count = 0;
	private int item_number = 0;
	private int test_level = 0;
	
	//计时
	private int hour_3 = 0;
	private int minute_3 = 0;
	private int second_3 = 0;
	
	ArrayList<HashMap<String, Object>> list3_1 = null;
	
	//数据库需要用到的一些东西
	SQLiteDatabase mdb_3 = null;
	Context context_3 = null;
	MyDBHelper myDbHelper_3  = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_grade);
		setTitle("你的成绩");
		//初始化数据
		initData();
		getViewIdInShowG();
		//显示成就界面
		showAchievement();
		
		// 进入解析按钮的控制
		button5_6.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(item_number == 0){
					backToMain();
					return;
				}
				else{
					changeView();
				}
			}
		});
	
	}
	
	//初始化数据
	@SuppressWarnings("unchecked")
	public void initData(){
		
		try{
			
			list3_1 = new ArrayList<HashMap<String, Object>>();
			//从Intent获得数据
			list3_1 = (ArrayList<HashMap<String, Object>>) this.getIntent().getSerializableExtra("item_list");
			test_number = this.getIntent().getIntExtra("test_number", 0);
			test_level = this.getIntent().getIntExtra("test_level",3);
			hour_3 = this.getIntent().getIntExtra("hour",0);
			minute_3 = this.getIntent().getIntExtra("minute",0);
			second_3 = this.getIntent().getIntExtra("second",0);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		item_number = list3_1.size();
	}
	
	//切换视图 的 函数
	public void changeView(){
		
		getTestContent(list3_1.get(item_count).get("item_id").toString());
		setContentView(R.layout.error_analyse);
		getViewIdInErrorA();
		setViewContentInErrorA();
		//向前与向后翻页
		button5_3.setOnClickListener(new onTurnPageListener(0, getApplicationContext()));
		button5_4.setOnClickListener(new onTurnPageListener(1, getApplicationContext()));
		
		//返回键
		button5_5.setOnClickListener(new View.OnClickListener() {
			
			@SuppressLint("InlinedApi")
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ErrorAnalyse.this, MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
			}
		});
	}
	
	//重写按下返回键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() ==0 ){
			backToMain();
			return true;
		}
		else
			return super.onKeyDown(keyCode, event);
	}
	
	// 回退回主程序
	public void backToMain(){
		Intent intent = new Intent(ErrorAnalyse.this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
	}
	
	//显示成就界面的内容
	public void showAchievement(){
		
		textView4.setText("错误率\n"+list3_1.size()+"/"+test_number);
		ratingBar5_1.setRating(test_level);
		textView6.setText("时间\n"+hour_3+":"+minute_3+":"+second_3);
		btn_backto_main.setOnClickListener(new View.OnClickListener() {
			
			@SuppressLint("InlinedApi")
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				backToMain();
			}
		});
		if(item_number == 0)
			button5_6.setText("退回主界面");
		if(item_number == 1){
			button5_4.setImageResource(drawable.ic_action_accept);
			button5_4.getBackground().setAlpha(0);
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
				if(item_count == (item_number-1)) {
					backToMain();
				}	
				else{
					//如果下一题是最后一题，那么下一题改为  提交
					if(item_count == (item_number-2)){
						button5_4.setImageResource(drawable.ic_action_accept);
						button5_4.getBackground().setAlpha(0);
					}
					//获取内容，设置内容
					getTestContent(list3_1.get(++item_count).get("item_id").toString());
					setViewContentInErrorA();
				}	
				
			}
			//向前翻页
			else{
				if(item_count == 0) 
					Toast.makeText(context, "已经是第一题了", Toast.LENGTH_SHORT).show();
				else{
					if(item_count == (item_number-1)){
						button5_4.setImageResource(drawable.ic_action_forward);
						button5_4.getBackground().setAlpha(0);
					}
					getTestContent(list3_1.get(--item_count).get("item_id").toString());
					setViewContentInErrorA();
				}
				
			}	
		}
		
	}
	
	//获取语法的内容	
	public void getTestContent(String id){
		
		//从数据库中获取内容
		try{
			context_3 = (Context)this;
			myDbHelper_3 = new MyDBHelper(context_3);
			mdb_3 =myDbHelper_3.getReadableDatabase();	
			Cursor cursor_2 = mdb_3.query("TestItem", new String[] {"question", "a", "b", 
					"c", "d", "answer", "why"}, "id="+id, null, null, null, null);
			
			//定位到题中
			cursor_2.moveToNext();
			
			//从指针处提取数据
			question = cursor_2.getString(cursor_2.getColumnIndex("question"));
			a = cursor_2.getString(cursor_2.getColumnIndex("a"));
			b = cursor_2.getString(cursor_2.getColumnIndex("b"));
			c = cursor_2.getString(cursor_2.getColumnIndex("c"));
			d = cursor_2.getString(cursor_2.getColumnIndex("d"));
			answer = cursor_2.getString(cursor_2.getColumnIndex("answer"));
			why = cursor_2.getString(cursor_2.getColumnIndex("why"));
		}
		catch (Exception e){
			System.out.println(e.toString());
			Toast.makeText(this, "something wrong in test content", Toast.LENGTH_SHORT).show();
		}
		finally{
			mdb_3.close();
		}
		
		
		
		
	}
	
	//获取错题分析部分各个视图的ID
	public void getViewIdInErrorA(){
		
		textView1 = (TextView)findViewById(R.id.textView5_1);
		textView2 = (TextView)findViewById(R.id.textView5_2);
		textView3 = (TextView)findViewById(R.id.textView5_3);
		
		
		radiobtn1 = (RadioButton)findViewById(R.id.radiobutton5_1);
		radiobtn2 = (RadioButton)findViewById(R.id.radiobutton5_2);
		radiobtn3 = (RadioButton)findViewById(R.id.radiobutton5_3);
		radiobtn4 = (RadioButton)findViewById(R.id.radiobutton5_4);
		
		button5_3 = (ImageButton)findViewById(R.id.button5_3);
		button5_4 = (ImageButton)findViewById(R.id.button5_4);
		button5_5 = (ImageButton)findViewById(R.id.button5_5);
		
	}
	
	//获得显示成绩处的视图的id
	public void getViewIdInShowG(){
		
		textView4 = (TextView)findViewById(R.id.textView5_4);
		textView6 = (TextView)findViewById(R.id.textView5_6);
		button5_6 = (Button)findViewById(R.id.button5_6);
		btn_backto_main = (ImageButton)findViewById(R.id.imgbtn_back_insg);
		ratingBar5_1 = (RatingBar)findViewById(R.id.ratingBar5_1);
	}
	
	//设置各个视图显示的内容
	public void setViewContentInErrorA(){
		
		textView1.setText("问题："+list3_1.get(item_count).get("item_order").toString()+"."+question);
		textView2.setText("你的答案："+list3_1.get(item_count).get("item_reply").toString()+"	  "+"正确答案："+"："+answer);
		textView3.setText("解析："+why);
		
		radiobtn1.setText(a);
		radiobtn2.setText(b);
		radiobtn3.setText(c);
		radiobtn4.setText(d);
		
		radiobtn1.setChecked(false);
		radiobtn2.setChecked(false);
		radiobtn3.setChecked(false);
		radiobtn4.setChecked(false);
		
	}
}