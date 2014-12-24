//package com.example.listener;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//
//import com.example.coolgrammer.AboutGrammar;
//import com.example.coolgrammer.DownloadLibrary;
//import com.example.coolgrammer.FeedBack;
//import com.example.coolgrammer.Grammar;
//import com.example.coolgrammer.MainActivity;
//import com.example.coolgrammer.TimeTest;
//
////每一个ListView的监听器
//public class MainListViewItemClickListener implements OnItemClickListener{
//	
//	ArrayList<HashMap<String, Object>> grammar_infor;
//	ArrayList<HashMap<String, Object>> test_lib_infor;
//	
//	public MainListViewItemClickListener(ArrayList<HashMap<String, Object>> grammar_infor, ArrayList<HashMap<String, Object>> test_lib_infor) {
//		// TODO Auto-generated constructor stub
//		this.grammar_infor = grammar_infor;
//		this.test_lib_infor = test_lib_infor;
//	}
//	@Override
//	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//			long arg3) {
//		
//		//单击Grammer那一栏
//		if(arg0.getAdapter() == lv_grammar_infor.getAdapter()){
//			
//		}
//		
//		//单击TestLibrary那一栏
//		else if(arg0.getAdapter() == lv_testlib_infor.getAdapter()){
//			
//			
//		}	
//		else if(arg0.getAdapter() == listview_main_left_menu.getAdapter()){
//			System.out.println("LeftMenu滑动菜单的单击事件");
//			Intent intent;
//			switch (arg2) {
//			case 0:
//				intent = new Intent(MainActivity.class, DownloadLibrary.class);
//				startActivity(intent);
//				break;
//			case 1:
//				intent = new Intent(MainActivity.this, FeedBack.class);
//				startActivity(intent);
//				break;
//			case 2:
//				intent = new Intent(MainActivity.this, AboutGrammar.class);
//				startActivity(intent);
//				break;
//			default:
//				break;
//			}
//		}
//		else {
//			//下载词库
//			if(arg2 == 0){
//				Intent intent = new Intent(MainActivity.this, DownloadLibrary.class);
//				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				startActivity(intent);
//			}
//			
//		}
//	}
//}