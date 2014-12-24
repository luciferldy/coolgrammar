package com.example.listener;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.coolgrammer.TimeTest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class MainTestLibItemClickListener implements OnItemClickListener{
	Context context;
	ArrayList<HashMap<String, Object>> testlib_data;
	public MainTestLibItemClickListener(Context context, ArrayList<HashMap<String, Object>> testlib_data){
		this.context = context;
		this.testlib_data = testlib_data;
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(context, TimeTest.class);
		Bundle bundle = new Bundle();
		bundle.putString("libraryId",testlib_data.get(arg2).get("libraryId").toString());
		bundle.putInt("level", Integer.parseInt(testlib_data.get(arg2).get("level").toString()));
		bundle.putString("title", testlib_data.get(arg2).get("title").toString());
		intent.putExtras(bundle);
		context.startActivity(intent);
	}
	
	
}
