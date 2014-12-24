package com.example.listener;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.coolgrammer.Grammar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class MainGrammarItemClickListener implements OnItemClickListener{
	Context context;
	ArrayList<HashMap<String, Object>>	grammar_infor;
	public MainGrammarItemClickListener(Context context, ArrayList<HashMap<String, Object>> grammar_infor) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.grammar_infor = grammar_infor;
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		//跳转到Grammer显示的界面，传递数据
		Intent intent = new Intent();
		intent.setClass(context,Grammar.class);
		//在Bundle中存放数据，使用“键”-“值”的方式存放数据
		Bundle bundle = new Bundle();
		bundle.putString("id",grammar_infor.get(arg2).get("id").toString());
		intent.putExtras(bundle);
		context.startActivity(intent);
	}

}
