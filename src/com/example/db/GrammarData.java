package com.example.db;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.coolgrammer.MyDBHelper;

public class GrammarData {
	private Context context;
	private MyDBHelper myDbHelper = null;
	private SQLiteDatabase mdb = null ;
	
	public GrammarData(Context context){
		this.context = context;
	}

	//获取数据库中Grammer表的所有信息
	public ArrayList<HashMap<String, Object>> getGrammarData(){
		//泛型数组
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		try{
			//数据库创建
			myDbHelper = new MyDBHelper(context);
			mdb = myDbHelper.getReadableDatabase();
			
			Cursor cursor = null;
			//获取数据库中所有元组
			cursor = mdb.query("Grammar", new String[] {"id","title","description"}, null, null, null, null, null);
			if(cursor.getCount()==0)
				Toast.makeText(context,"no data in table Grammar!",Toast.LENGTH_SHORT).show();
			
			//HashMap存储
			HashMap<String,Object> item = null;
			while(cursor.moveToNext()){
				item = new HashMap<String,Object>();
				//获取id
				item.put("id", cursor.getString(cursor.getColumnIndex("id")));
				//获取title
				item.put("title", cursor.getString(cursor.getColumnIndex("title")));
				//获取description
				item.put("description", cursor.getString(cursor.getColumnIndex("description")));
				//获取content
				//item.put("content", cursor.getString(cursor.getColumnIndex("content")));
				//保存到list组中
				list.add(item);
			}
			mdb.close();
		}
		catch (Exception e){
			e.printStackTrace();
			mdb.close();
		}
		return list;	
	}
}
