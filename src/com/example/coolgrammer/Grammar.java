/*
 * 编号：1
 * 功能：这部分是语法显示的Activity
 * 时间：2014年8月6日
 * 最近修改时间：8月23日
 */
package com.example.coolgrammer;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class Grammar extends Activity{
	
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.grammar);
		//添加一个回退键
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		//获得从 Buddle 处传过来的数据，以键名来获得值
		Bundle bundle_1 = this.getIntent().getExtras();
	
		//显示语法的内容
		showGrammarContent(bundle_1.getString("id"));
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	//显示语法的内容
	public void showGrammarContent(String id){
		
		SQLiteDatabase mdb_1 = null;
		String title_1 = null;
		String content_1 = null;
		
		//从数据库中获取内容
		try{
			Context context_1 = (Context)this;
			MyDBHelper myDbHelper_1 = new MyDBHelper(context_1);
			mdb_1 =myDbHelper_1.getReadableDatabase();	
			Cursor cursor_1 = mdb_1.query("Grammar", new String[] {"title", "content"}, "id="+id, null, null, null, null);
			cursor_1.moveToNext();
			title_1 = cursor_1.getString(cursor_1.getColumnIndex("title"));
			content_1 = cursor_1.getString(cursor_1.getColumnIndex("content"));
		}
		catch (Exception e){
			System.out.println(e.toString());
			Toast.makeText(this, "something wrong in grammar content", Toast.LENGTH_SHORT).show();
		}
		mdb_1.close();
		
		TextView textView1 = (TextView)findViewById(R.id.textView1_1);
		textView1.setText(content_1);
		setTitle(title_1);
	}
}