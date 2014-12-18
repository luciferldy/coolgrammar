package com.example.thread;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.coolgrammer.R;
import com.example.jsonparse.CloudDictionaryJsonHandle;
import com.example.jsonparse.CloudTranslateJsonHandle;

public class CloudTranslateJsonParsonRunnable implements Runnable{

	private Handler handler;
	private ListView translate_result;
	private String keys;
	private ArrayList<HashMap<String, String>> result;
	private CloudDictionaryJsonHandle translate;
	private Context context;
	public CloudTranslateJsonParsonRunnable(ListView translate_result,Context context, String keys, Handler handler){
		this.translate_result = translate_result;
		this.keys = keys;
		this.handler = handler;
		this.context = context;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		translate = new CloudDictionaryJsonHandle(keys);
		if (translate.isgetTranslateResult()) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					result = new ArrayList<HashMap<String,String>>();
					result = translate.getTranslateResult();
//					Log.v("CloudTranslateJsonParsonRunnable", result.get(0).get("src")+"+"+result.get(0).get("dst"));
//					SimpleAdapter adapter = new SimpleAdapter(context, result, R.layout.cloud_translate_list_item,
//							new String[]{"src", "dst"}, new int[]{R.id.before_translate, R.id.after_translate});
					SimpleAdapter adapter = new SimpleAdapter(context, result, R.layout.cloud_dictionary_list_item, 
							new String[]{"word_name", "ph_am", "ph_en", "explain"}, new int[]{R.id.before_dictionary, R.id.ph_am, R.id.ph_en, R.id.after_dictionary});
					translate_result.setAdapter(adapter);
				}
			});
		}
		else {
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Toast.makeText(context, "抱歉，出错了", Toast.LENGTH_SHORT).show();
				}
			});
		}
	}
	
}
