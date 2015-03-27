package com.example.coolgrammer;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

public class AboutGrammar extends Activity{
	protected void onCreate(Bundle savedInstanceBundle){
		super.onCreate(savedInstanceBundle);
		setContentView(R.layout.coolgrammar_about);
		//添加一个回退键
//		ActionBar actionBar = getActionBar();
//		actionBar.setDisplayHomeAsUpEnabled(true);
	
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
}
