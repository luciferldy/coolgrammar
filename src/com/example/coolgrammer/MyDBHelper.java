/*
 * 编号：0.5
 * 功能：数据库辅助类的重写
 * 时间：2014年8月22日
 * 最近修改时间：8月23日
 */
package com.example.coolgrammer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

	//数据库的类,这里存储的是数据库的类，初始化之类的要在这里做
	public class MyDBHelper extends SQLiteOpenHelper {
		private final static int VERSION = 1;
		public final static String DATABASE_NAME = "grammar.db";
		@SuppressLint("SdCardPath")
		public final static String DATABASE_PATH = "/data/data/com.example.coolgrammer/databases/";
		
		public MyDBHelper(Context context){
			super(context, DATABASE_NAME, null, VERSION);
		}
		
		//在数据库第一次生成时调用这个方法，我们一般在这个方法里生成数据库表
		@Override
		public void onCreate(SQLiteDatabase db){
			
			try{
				System.out.println("grammar.db onCreate!");
			}
			catch(Exception e){
				e.printStackTrace();
			}
				
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
			System.out.println("garmmar.db onUpgrade!");
		}
	}