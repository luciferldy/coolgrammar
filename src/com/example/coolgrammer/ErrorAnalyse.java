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
	private TextView error_analyse_content = null;//题目
	private TextView error_analyse_rightanswer = null;//答案
	private TextView error_analyse_answerresolve = null;//解析
	
	private TextView show_grade_correctrate = null; //正确率
	private TextView show_grade_time = null; //时间
	
	private RadioButton error_analyse_answer_first = null;
	private RadioButton error_analyse_answer_second = null;
	private RadioButton error_analyse_answer_third = null;
	private RadioButton error_analyse_answer_fourth = null;
	
	
	private ImageButton error_analyse_prepage = null;
	private ImageButton error_analyse_nextpage = null;
	private ImageButton error_analyse_exit = null;
	private ImageButton btn_backto_main = null;
	private Button show_grade_entertoanalyse = null;
	
	private RatingBar show_grade_rating = null;
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
	private int error_anaylse_hour = 0;
	private int error_analyse_minute = 0;
	private int error_analyse_second = 0;
	
	ArrayList<HashMap<String, Object>> error_lib = null;
	
	//数据库需要用到的一些东西
	private SQLiteDatabase mdb = null;
	private Context context = null;
	private MyDBHelper myDbHelper  = null;
	
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
		show_grade_entertoanalyse.setOnClickListener(new View.OnClickListener() {
			
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
			
			error_lib = new ArrayList<HashMap<String, Object>>();
			//从Intent获得数据
			error_lib = (ArrayList<HashMap<String, Object>>) this.getIntent().getSerializableExtra("item_list");
			test_number = this.getIntent().getIntExtra("test_number", 0);
			test_level = this.getIntent().getIntExtra("test_level",3);
			error_anaylse_hour = this.getIntent().getIntExtra("hour",0);
			error_analyse_minute = this.getIntent().getIntExtra("minute",0);
			error_analyse_second = this.getIntent().getIntExtra("second",0);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		item_number = error_lib.size();
	}
	
	//切换视图 的 函数
	public void changeView(){
		
		getTestContent(error_lib.get(item_count).get("item_id").toString());
		setContentView(R.layout.error_analyse);
		getViewIdInErrorA();
		setViewContentInErrorA();
		//向前与向后翻页
		error_analyse_prepage.setOnClickListener(new onTurnPageListener(0, getApplicationContext()));
		error_analyse_nextpage.setOnClickListener(new onTurnPageListener(1, getApplicationContext()));
		
		//返回键
		error_analyse_exit.setOnClickListener(new View.OnClickListener() {
			
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
		
		show_grade_correctrate.setText("错误率\n"+error_lib.size()+"/"+test_number);
		show_grade_rating.setRating(test_level);
		show_grade_time.setText("时间\n"+error_anaylse_hour+":"+error_analyse_minute+":"+error_analyse_second);
		btn_backto_main.setOnClickListener(new View.OnClickListener() {
			
			@SuppressLint("InlinedApi")
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				backToMain();
			}
		});
		if(item_number == 0)
			show_grade_entertoanalyse.setText("退回主界面");
		if(item_number == 1){
			error_analyse_nextpage.setImageResource(drawable.ic_action_accept);
			error_analyse_nextpage.getBackground().setAlpha(0);
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
					//如果下一题是最后一题，那么下一题改为一个对号的样子
					if(item_count == (item_number-2)){
						error_analyse_nextpage.setImageResource(drawable.ic_action_accept);
						error_analyse_nextpage.getBackground().setAlpha(0);
					}
					//获取内容，设置内容
					getTestContent(error_lib.get(++item_count).get("item_id").toString());
					setViewContentInErrorA();
				}	
				
			}
			//向前翻页
			else{
				if(item_count == 0) 
					Toast.makeText(context, "已经是第一题了", Toast.LENGTH_SHORT).show();
				else{
					if(item_count == (item_number-1)){
						error_analyse_nextpage.setImageResource(drawable.ic_action_forward);
						error_analyse_nextpage.getBackground().setAlpha(0);
					}
					getTestContent(error_lib.get(--item_count).get("item_id").toString());
					setViewContentInErrorA();
				}
				
			}	
		}
		
	}
	
	//获取语法的内容	
	public void getTestContent(String id){
		
		//从数据库中获取内容
		try{
			context = (Context)this;
			myDbHelper = new MyDBHelper(context);
			mdb =myDbHelper.getReadableDatabase();	
			Cursor cursor_2 = mdb.query("TestItem", new String[] {"question", "a", "b", 
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
			mdb.close();
		}	
		
	}
	
	//获取错题分析部分各个视图的ID
	public void getViewIdInErrorA(){
		
		error_analyse_content = (TextView)findViewById(R.id.error_analyse_content);
		error_analyse_rightanswer = (TextView)findViewById(R.id.error_analyse_rightanswer);
		error_analyse_answerresolve = (TextView)findViewById(R.id.error_analyse_answerresolve);
		
		
		error_analyse_answer_first = (RadioButton)findViewById(R.id.error_analyse_answer_first);
		error_analyse_answer_second = (RadioButton)findViewById(R.id.error_analyse_answer_second);
		error_analyse_answer_third = (RadioButton)findViewById(R.id.error_analyse_answer_third);
		error_analyse_answer_fourth = (RadioButton)findViewById(R.id.error_analyse_answer_fourth);
		
		error_analyse_prepage = (ImageButton)findViewById(R.id.error_analyse_prepage);
		error_analyse_nextpage = (ImageButton)findViewById(R.id.error_analyse_nextpage);
		error_analyse_exit = (ImageButton)findViewById(R.id.error_analyse_exit);
		
	}
	
	//获得显示成绩处的视图的id
	public void getViewIdInShowG(){
		
		show_grade_correctrate = (TextView)findViewById(R.id.show_grade_correctrate);
		show_grade_time = (TextView)findViewById(R.id.show_grade_time);
		show_grade_entertoanalyse = (Button)findViewById(R.id.show_grade_entertoanalyse);
		btn_backto_main = (ImageButton)findViewById(R.id.imgbtn_back_insg);
		show_grade_rating = (RatingBar)findViewById(R.id.show_grade_rating);
	}
	
	//设置各个视图显示的内容
	public void setViewContentInErrorA(){
		
		error_analyse_content.setText("问题："+error_lib.get(item_count).get("item_order").toString()+"."+question);
		error_analyse_rightanswer.setText("你的答案："+error_lib.get(item_count).get("item_reply").toString()+"	  "+"正确答案："+"："+answer);
		error_analyse_answerresolve.setText("解析："+why);
		
		error_analyse_answer_first.setText(a);
		error_analyse_answer_second.setText(b);
		error_analyse_answer_third.setText(c);
		error_analyse_answer_fourth.setText(d);
		
		error_analyse_answer_first.setChecked(false);
		error_analyse_answer_second.setChecked(false);
		error_analyse_answer_third.setChecked(false);
		error_analyse_answer_fourth.setChecked(false);
		
	}
}