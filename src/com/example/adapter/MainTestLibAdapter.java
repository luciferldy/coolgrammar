package com.example.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.coolgrammer.R;
import com.example.listener.MainTestLibDetailAndDelClickListener;
import com.example.listener.MainTestLibItemExpandClickListener;

//TestLibrary自定义适配器
	public class MainTestLibAdapter extends BaseAdapter{
		
		private LayoutInflater mInflater = null;
		private ArrayList<HashMap<String, Object>> testlib_infor = new ArrayList<HashMap<String,Object>>(); 
		private Context context;
		//构造函数
		public MainTestLibAdapter(Context context, ArrayList<HashMap<String, Object>> testlib_infor){
			this.mInflater = LayoutInflater.from(context);
			this.testlib_infor = testlib_infor;	
			this.context = context;
		}
		
		@Override
		public int getCount(){
			return testlib_infor.size();
		}
		
		@Override
		public Object getItem(int arg0){
			return null;
		}
		
		@Override
		public long getItemId(int arg0){
			return 0;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup Parent){
			
			TestLibViewGroup mvg = null;
			
			if(convertView == null){
				mvg = new TestLibViewGroup();
				convertView = mInflater.inflate(R.layout.test_lib_adapter, null);
				
				mvg.tv_lib_title = (TextView)convertView.findViewById(R.id.testlib_adapter_title);
				
				mvg.imgbtn_expand = (ImageButton)convertView.findViewById(R.id.testlib_adapter_imgbtn_expand);
				mvg.imgbtn_detail_infor = (ImageButton)convertView.findViewById(R.id.testlib_adapter_img_detail);
				mvg.imgbtn_delete = (ImageButton)convertView.findViewById(R.id.testlib_adapter_img_delete);
				
				mvg.relativeLayout = (RelativeLayout)convertView.findViewById(R.id.testlib_adapter_hide);
				mvg.ratingBar = (RatingBar)convertView.findViewById(R.id.testlib_adapter_ratingBar);
				convertView.setTag(mvg);
			}
			else{
				mvg = (TestLibViewGroup)convertView.getTag();
			}
			
			mvg.tv_lib_title.setText((String)testlib_infor.get(position).get("title"));
			
			mvg.ratingBar.setRating((Integer)testlib_infor.get(position).get("level"));
			mvg.imgbtn_expand.setOnClickListener(new MainTestLibItemExpandClickListener(mvg.relativeLayout, context));
			MainTestLibDetailAndDelClickListener listener = new MainTestLibDetailAndDelClickListener(position, context, testlib_infor, this);
			mvg.imgbtn_detail_infor.setOnClickListener(listener);
			mvg.imgbtn_delete.setOnClickListener(listener);
			
			return convertView;
		}
		
		//自定义视图组，为了自定义适配器
		public class TestLibViewGroup{
			
			TextView tv_lib_title;
			
			ImageButton imgbtn_expand;
			ImageButton imgbtn_detail_infor;
			ImageButton imgbtn_delete;
			
			RatingBar ratingBar;
			RelativeLayout relativeLayout;
		}
	}
