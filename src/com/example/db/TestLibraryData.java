package com.example.db;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.coolgrammer.MyDBHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

public class TestLibraryData {
	
	private Context context;
	private MyDBHelper myDbHelper = null;
	private SQLiteDatabase mdb = null ;
	
	public TestLibraryData(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}
	
	// 获取数据库中TestLibrary表的信息
	public ArrayList<HashMap<String, Object>> getTestLibraryData(){
		
		//泛型数组
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		try{
			//数据库创建
			myDbHelper = new MyDBHelper(context);
			mdb = myDbHelper.getReadableDatabase();
			
			Cursor cursor = null;
			//获取数据库中所有元组
			cursor = mdb.query("TestLibrary", new String[] {"id","grammarId", "title","description", "itemCount", "level", "createDate"}, null, null, null, null, null);
			if(cursor.getCount()==0)
				Toast.makeText(context, "no data in table TestLibrary", Toast.LENGTH_SHORT).show();
			
			HashMap<String,Object> item = null;
			while(cursor.moveToNext()){	
				item = new HashMap<String,Object>();
				//获取id,这里面的这个cursor.getColumenIndex("id")可以直接用0代替
				item.put("id", cursor.getString(cursor.getColumnIndex("id")));
				//获取garmmerId
				item.put("grammerId", cursor.getString(cursor.getColumnIndex("grammarId")));
				//获取title
				item.put("title", cursor.getString(cursor.getColumnIndex("title")));
				//获取description
				item.put("description", cursor.getString(cursor.getColumnIndex("description")));
				//获取itemCount
				item.put("itemCount", cursor.getInt(cursor.getColumnIndex("itemCount")));
				//获取level
				item.put("level",cursor.getInt(cursor.getColumnIndex("level")));
				//获取createData
				item.put("createDate",cursor.getInt(cursor.getColumnIndex("createDate")));
				//获取downloadCount
				//item.put("downloadCount",cursor.getInt(cursor.getColumnIndex("downloadCount")));
				//保存到日记组中
				list.add(item);
			}
			mdb.close();
			
		}
		catch(Exception e){
			e.printStackTrace();
			mdb.close();
		}
		return list;
	}
}
