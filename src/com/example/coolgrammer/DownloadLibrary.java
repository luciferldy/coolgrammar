/*
* 编号：4
* 功能：显示下载界面，下载数据
* 时间：2014年9月3日
*/
package com.example.coolgrammer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;


import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class DownloadLibrary extends Activity{
	
	//固定的URL
	private final static String BASE_URL = "http://1.coolgrammar.sinaapp.com/index.php?a=";
	//ListView
	ListView download_lib_listview = null;
	//泛型数组哦
	ArrayList<HashMap<String, Object>> store_lib_infor = null;
	//进度条对话框
	ProgressDialog proDialog = null;
	//handler用post将消息压入堆栈
	Handler get_lib_handler = new Handler();
	
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.download_lib);
		ActionBar actionBar_in_dl = getActionBar();
		actionBar_in_dl.setDisplayHomeAsUpEnabled(true);
		setProgressDialog();
		new Thread(new getLibThread()).start();
		//显示进度条框
		proDialog.show();
	}
	
	//对于进度条对话框的一些设置
	public void setProgressDialog(){
		proDialog = new ProgressDialog(this);
		proDialog.setTitle("提示！");
		proDialog.setMessage("正在从网络获取数据……");
		proDialog.setCancelable(false);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(DownloadLibrary.this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	// 从网络获取所有lib列表
	public class getLibThread implements Runnable{
		@Override
		public void run(){
			
				String str_xml_lib = getXmlContent("library");	
				// 没有从网页上获取到xml字符串
				if(str_xml_lib.equals("-1")){
					proDialog.dismiss();
					get_lib_handler.post(new Runnable(){
						@Override
						public void run(){
							//显示对话框也是在主线程中？？
							Toast.makeText(getApplicationContext(), "网络连接出现了问题！", Toast.LENGTH_SHORT).show();
							finish();
						}	
					});
				}
				else{
					System.out.println(str_xml_lib);
					//获取并设置内容
					getLibandInit(str_xml_lib);
				}
		}
	}
	
	// 下载某个词库的题目
	public class getQuestionsThread implements Runnable{
		private String lib_id = null;
		private String lib_order = null;
		private DownloadLibViewGroup lib_infor;
		
		public getQuestionsThread(String lib_id, String lib_order, DownloadLibViewGroup lib_infor){
			this.lib_id = lib_id;
			this.lib_order = lib_order;
			this.lib_infor = lib_infor;
		}
		
		@Override
		public void run(){
			
			String str_xml = getXmlContent(lib_id);	
			// 没有从网页上获取到xml字符串
			if(str_xml.equals("-1") || !addQuestionstoLib(Integer.parseInt(lib_order))){
				get_lib_handler.post(new Runnable(){
					@Override
					public void run(){
						//显示对话框也是在主线程中？？
						Toast.makeText(getApplicationContext(), "网络连接出现了问题！", Toast.LENGTH_SHORT).show();
						finish();
					}	
				});
			}
			else{
				Log.d("试题的xml", str_xml);
				ParseQuestionsXmlandInstall(str_xml, lib_infor);
			}
		}
	}
	
	//添加到TestLib中
	public boolean addQuestionstoLib(int lib_order){
		try{
			MyDBHelper dbhelper_in_testlib = new MyDBHelper(getApplicationContext());
			SQLiteDatabase sqlite_in_testlib = dbhelper_in_testlib.getWritableDatabase();
			ContentValues content_in_testlib = new ContentValues();
			content_in_testlib.put("id", (String)store_lib_infor.get(lib_order).get("id"));
			content_in_testlib.put("grammarId", (String)store_lib_infor.get(lib_order).get("grammarId"));
			content_in_testlib.put("title", (String)store_lib_infor.get(lib_order).get("title"));
			content_in_testlib.put("description", (String)store_lib_infor.get(lib_order).get("description"));
			content_in_testlib.put("itemCount", (String)store_lib_infor.get(lib_order).get("itemCount"));
			content_in_testlib.put("level", (String)store_lib_infor.get(lib_order).get("level"));
			content_in_testlib.put("createDate", (String)store_lib_infor.get(lib_order).get("createDate"));
			content_in_testlib.put("downloadCount", (String)store_lib_infor.get(lib_order).get("downloadCount"));
			sqlite_in_testlib.insert("TestLibrary", null, content_in_testlib);
			sqlite_in_testlib.close();
			return true;
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
		
	}
	
	//解析试题部分xml并且将其安装
	public void ParseQuestionsXmlandInstall(String str_xml, DownloadLibViewGroup lib_infor){		
		try {
			
			InputStream input = new ByteArrayInputStream(str_xml.getBytes("utf-8"));
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser saxParser = spf.newSAXParser(); //创建解析器
			SAX_Questions_Handler handler = new SAX_Questions_Handler();
			saxParser.parse(input, handler);
			input.close();
			
			//成功安装
			if(handler.isSuccess()){
				get_lib_handler.post(new UpdateDownloadLibStatus(lib_infor));	
				return;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		get_lib_handler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "未成功安装！", Toast.LENGTH_LONG).show();
			}
		});
			
	}
	
	
	public class UpdateDownloadLibStatus implements Runnable{
		DownloadLibViewGroup lib_infor;
		
		public UpdateDownloadLibStatus(DownloadLibViewGroup lib_infor) {
			// TODO Auto-generated constructor stub
			this.lib_infor = lib_infor;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			// TODO Auto-generated method stub
			Toast.makeText(getApplicationContext(), "已下载成功！", Toast.LENGTH_SHORT).show();
			lib_infor.imageButton.setClickable(false);
			lib_infor.imageButton.setImageResource(R.drawable.ic_action_accept);
			lib_infor.tv_ifexist.setText("已下载");
		}
		
	}
	//获得题库内容并且初始化界面
	public void getLibandInit(String str){
		//将字符串转换为输入流，并获取内容,存到
		try{
			InputStream input = new ByteArrayInputStream(str.getBytes("utf-8"));
			
			//解析xml
			store_lib_infor = analyseContent(input);
			
			//没有成功解析或者存的是空的
			if(store_lib_infor == null || store_lib_infor.get(0) == null){
				
				System.out.println("无法成功解析!");
				//让进度对话框消失
				proDialog.dismiss();
				//当前Activity退出
				finish();
				return;
			}
			else{
				get_lib_handler.post(new Runnable(){
					@Override
					public void run(){
						//在post操作中操作UI组件
						download_lib_listview = (ListView)findViewById(R.id.download_lib_listview);
						DownloadAdapter dladapter = new DownloadAdapter(getApplicationContext());
						download_lib_listview.setAdapter(dladapter);
						DownloadLibrary.this.setTitle("下载");
					}	
				});
				//让进度对话框消失
				proDialog.dismiss();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//通过请求URL获取数据,返回-1的话代表连接失败
	public String getXmlContent(String arg1){
		HttpClient httpclient = null;
		try{
			//httpclient对象
			httpclient = new DefaultHttpClient();
			//httpget对象，构造
			HttpGet httpget = new HttpGet(BASE_URL+arg1);
			//请求httpclient获得httpresponse
			HttpResponse httpresponse = httpclient.execute(httpget);
			if(httpresponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				
				HttpEntity httpEntity = httpresponse.getEntity();
				//获得返回的字符串
				String str = EntityUtils.toString(httpEntity, "utf-8");
				return str;
			}
			else{
				//中断连接
				httpget.abort();
			}
		}
		catch(Exception e){
			e.printStackTrace();	
		}
		
		httpclient.getConnectionManager().shutdown();
		return "-1";
		
	}
	
	//SAX解析XML并且将结果存在arraylist里面,返回null说明xml解析失败
	public ArrayList<HashMap<String, Object>> analyseContent(InputStream input){
		try{
			
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser saxParser = spf.newSAXParser(); //创建解析器
			SAX_Lib_Handler handler = new SAX_Lib_Handler();
			saxParser.parse(input, handler);
			input.close();
			return handler.getItemList();
			
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	//SAX解析testLibrary类
	public class SAX_Lib_Handler extends DefaultHandler{
		
		//存储数据的ArrayList
		ArrayList<HashMap<String, Object>> arrayListItem = null; 
		HashMap<String, Object> hashMap = null;
		boolean ifInLib = false;
		
		//构造函数
		public SAX_Lib_Handler(){
			arrayListItem = new ArrayList<HashMap<String, Object>>();
		}
		
		public ArrayList<HashMap<String, Object>> getItemList(){
			return arrayListItem;
		}
		
		//元素开始时触发
		@SuppressLint("DefaultLocale")
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException{
			//标签元素开始时触发
			String tagName = localName.length() != 0 ? localName : qName;
			if(tagName.equals("library")){
				ifInLib = true;
				hashMap = new HashMap<String, Object>();
			}
			if(ifInLib){
				if(tagName.equals("id")){
					hashMap.put("id", attributes.getValue("data"));
				}
				else if(tagName.equals("grammarId")){
					hashMap.put("grammarId", attributes.getValue("data"));
				}
				else if(tagName.equals("title")){
					hashMap.put("title", attributes.getValue("data"));
					//System.out.println(attributes.getValue("title"));
				}
				else if(tagName.equals("description")){
					hashMap.put("description", attributes.getValue("data"));
				}
				else if(tagName.equals("itemCount")){
					hashMap.put("itemCount", attributes.getValue("data"));
				}
				else if(tagName.equals("level")){
					hashMap.put("level", attributes.getValue("data"));
				}
				else if(tagName.equals("createDate")){
					hashMap.put("createDate", attributes.getValue("data"));
				}
				else if(tagName.equals("downloadCount")){
					hashMap.put("downloadCount", attributes.getValue("data"));
				}
				else
					;
			}
		}
		
		//当标签结束时
		@SuppressLint("DefaultLocale")
		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException{
				String tagName = localName.length() != 0 ? localName : qName;
				tagName = tagName.toLowerCase();
				if(tagName.equals("library")){
					ifInLib = false;
					arrayListItem.add(hashMap);
				}
		}
	}
	
	//SAX解析testItem类
	public class SAX_Questions_Handler extends DefaultHandler{
		//存储数据的ArrayList
		ContentValues cv = null;
		boolean ifInTest = false;
		boolean isinstall = true;
		MyDBHelper myDBHelper_in_sax = null;
		SQLiteDatabase mdb_in_sax = null;
		
		public boolean isSuccess(){
			return isinstall;
		}
		//构造函数
		public SAX_Questions_Handler(){
			// TODO Auto-generated constructor stub
		}
			
		//文档开始时触发
		@Override
		public void startDocument(){
			//数据库辅助类和数据库类初始化一下
			myDBHelper_in_sax = new MyDBHelper(getApplicationContext());
			mdb_in_sax = myDBHelper_in_sax.getWritableDatabase();
		}
		
		//文档结束时触发
		@Override
		public void endDocument(){
			mdb_in_sax.close();
		}
		
		//元素开始时触发
		@SuppressLint("DefaultLocale")
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException{
			//标签元素开始时触发
			String tagName = localName.length() != 0 ? localName : qName;
			if(tagName.equals("item")){
				ifInTest = true;
				cv = new ContentValues();
			}
			if(ifInTest){
				if(tagName.equals("id")){
					cv.put("id", attributes.getValue("data"));
				}
				else if(tagName.equals("grammarId")){
					cv.put("grammarId", attributes.getValue("data"));
				}
				else if(tagName.equals("libraryId")){
					cv.put("libraryId", attributes.getValue("data"));
				}
				else if(tagName.equals("title")){
					cv.put("title", attributes.getValue("data"));
					//System.out.println(attributes.getValue("title"));
				}
				else if(tagName.equals("question")){
					cv.put("question", attributes.getValue("data"));
				}
				else if(tagName.equals("a")){
					cv.put("a", attributes.getValue("data"));
				}
				else if(tagName.equals("b")){
					cv.put("b", attributes.getValue("data"));
				}
				else if(tagName.equals("c")){
					cv.put("c", attributes.getValue("data"));
				}
				else if(tagName.equals("d")){
					cv.put("d", attributes.getValue("data"));
				}
				else if(tagName.equals("answer")){
					cv.put("answer", attributes.getValue("data"));
				}
				else if(tagName.equals("why")){
					cv.put("why", attributes.getValue("data"));
				}
				else
					;
			}
		}
		
		//当标签结束时
		@SuppressLint("DefaultLocale")
		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException{
				String tagName = localName.length() != 0 ? localName : qName;
				if(tagName.equals("item")){
					ifInTest = false;
					//存入数据库中
					try{
						mdb_in_sax.insert("TestItem", null, cv);
					}
					catch(Exception e){
						e.printStackTrace();
						isinstall = false;
					}
				}
		}
	}
	
	//下载界面的自定义适配器
	public class DownloadAdapter extends BaseAdapter{
		
		private LayoutInflater mInflater = null;
		
		//构造函数
		public DownloadAdapter(Context context){
			this.mInflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount(){
			return store_lib_infor.size();
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
			
			DownloadLibViewGroup mvg = null;
			
			if(convertView == null){
				mvg = new DownloadLibViewGroup();
				
				convertView = mInflater.inflate(R.layout.download_lib_list_adapter, null);
				mvg.tv_title = (TextView)convertView.findViewById(R.id.download_lib_list_adapter_tv_title);
				mvg.tv_description = (TextView)convertView.findViewById(R.id.download_lib_list_adapter_tv_description);
				mvg.tv_number = (TextView)convertView.findViewById(R.id.download_lib_list_adapter_tv_time);
				mvg.tv_time = (TextView)convertView.findViewById(R.id.download_lib_list_adapter_tv_time);
				mvg.tv_ifexist = (TextView)convertView.findViewById(R.id.download_lib_list_adapter_dl_tv_ifexist);
				
				mvg.ratingBar = (RatingBar)convertView.findViewById(R.id.download_lib_list_adapter_ratingbar);
				mvg.imageButton = (ImageButton)convertView.findViewById(R.id.download_lib_list_adapter_dl_img);
				
				convertView.setTag(mvg);
			}
			else{
				mvg = (DownloadLibViewGroup)convertView.getTag();
			}
			
			mvg.tv_title.setText((String)store_lib_infor.get(position).get("title"));
			mvg.tv_description.setText((String)store_lib_infor.get(position).get("description"));
			mvg.tv_number.setText("下载次数："+(String)store_lib_infor.get(position).get("itemCount"));
			mvg.tv_time.setText("创建日期："+(String)store_lib_infor.get(position).get("createDate"));
			
			mvg.ratingBar.setRating(Integer.parseInt((String)store_lib_infor.get(position).get("level")));
			
			//判断列表是否在数据库中,数据在数据库中的话……
			if(ifDownload((String)store_lib_infor.get(position).get("id"))){
				mvg.imageButton.setClickable(false);
				mvg.imageButton.setImageResource(R.drawable.ic_action_accept);
				mvg.tv_ifexist.setText("已下载");
			}
			else{
				//给imagebutton添加点击事件
				mvg.imageButton.setOnClickListener(new DownloadImageBtnClickListener((String)store_lib_infor.get(position).get("id"), 
						position+"", mvg));
			}
			return convertView;
		}
		
		//判断数据是否在数据库中
		public boolean ifDownload(String lib_id){
			try{
				MyDBHelper myDBHelper = new MyDBHelper(getApplicationContext());
				SQLiteDatabase mdb = myDBHelper.getReadableDatabase();
				
				//通过查询判断是否存在这条数据
				Cursor cursor = mdb.query("TestLibrary", new String[]{"id"}, "id="+lib_id, null, null, null, null);
				if(cursor.getCount() == 0){
					cursor.close();
					mdb.close();
					return false;
				}
				else{
					cursor.close();
					mdb.close();
					return true;
				}
				
			}
			catch(Exception e){
				e.printStackTrace();
				return false;
			}
		}
	}
	
	//自定义视图组，为了自定义适配器
	public class DownloadLibViewGroup{
		private TextView tv_title;
		private TextView tv_description;
		private TextView tv_number;
		private TextView tv_time;
		private TextView tv_ifexist;
		private RatingBar ratingBar;
		private ImageButton imageButton;
	}

	//下载按钮的监听器
	public class DownloadImageBtnClickListener implements View.OnClickListener{

		//题库的id
		private String lib_id;
		private String lib_order;
		private DownloadLibViewGroup lib_infor;
		
		public DownloadImageBtnClickListener(String lib_id, String lib_order, DownloadLibViewGroup lib_infor){
			this.lib_id = lib_id;
			this.lib_order = lib_order;
			this.lib_infor = lib_infor;
		}
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			new Thread(new getQuestionsThread(lib_id, lib_order, lib_infor)).start();
			
		}
		
	}

}