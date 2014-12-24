package com.example.listener;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.example.coolgrammer.AboutGrammar;
import com.example.coolgrammer.DownloadLibrary;
import com.example.coolgrammer.FeedBack;

public class MainDrawerItemClickListener implements OnItemClickListener{
	Context context;
	public MainDrawerItemClickListener(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		System.out.println("LeftMenu滑动菜单的单击事件");
		Intent intent;
		switch (arg2) {
		case 0:
			intent = new Intent(context, DownloadLibrary.class);
			break;
		case 1:
			intent = new Intent(context, FeedBack.class);
			break;
		case 2:
			intent = new Intent(context, AboutGrammar.class);
			break;
		default:
			return;
		}
		context.startActivity(intent);
	}
}
