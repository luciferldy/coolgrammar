/*
 * 编号：0
 * 功能：主Activity
 * 时间：2014年8月4日
 * 最近更新时间：8月22日
 */
package com.example.coolgrammer;

import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainActivity extends Activity {

	private static MyDBHelper myDbHelper = null;
	private static SQLiteDatabase mdb = null ;
	private Context context = null;
	
	// 泛型数组+哈希图
	private ArrayList<HashMap<String, Object>> arraylist0_1 = null;
	private ArrayList<HashMap<String, Object>> arraylist0_2 = null;
	
	//
	private ListView list0_1 = null;
	private ListView list0_2 = null;
	
	//当前界面
	private int currentTab = 0;
	
	//监听手势
	GestureDetector detector = null;
	
	//tabhost
	private TabHost tabhost = null;
	//适配器
	TestLibAdapter myadapter0_1 = null;
	
	//以下是左侧侧滑菜单的控件显示
	ActionBarDrawerToggle mDrawerToggle;
	DrawerLayout drawerlayout_main;
	RelativeLayout relativeLayout_main;
	ListView listview_main_left_menu;
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);	
		importDataBase();
		initDrawerMenu();
		initListView();
		initTab();
		initGestureDetector();
	}
	
	@SuppressLint("NewApi")
	public void initDrawerMenu(){
		//获得Drawer
		drawerlayout_main = (DrawerLayout)findViewById(R.id.drawer_layout);
		listview_main_left_menu = (ListView)findViewById(R.id.listview_left_menu_main);
		//配置适配器
		SimpleAdapter left_menu_adapter = new SimpleAdapter(this, getLeftView(), R.layout.left_menu_adapter, new String[]{"img", "title"}, new int[]{R.id.imgview_main_leftmenu, R.id.textview_main_leftmenu});
		listview_main_left_menu.setAdapter(left_menu_adapter);
		//设置监听器
		listview_main_left_menu.setOnItemClickListener(new TabOnItemClickListener());
		drawerlayout_main.setDrawerShadow(null, GravityCompat.START);
		//设置滑动菜单的监听器
		mDrawerToggle = new ActionBarDrawerToggle(this, drawerlayout_main, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close){
			public void onDrawerClosed(View view){
//				getActionBar().setTitle("关闭的");
				invalidateOptionsMenu();
			}
			public void onDrawerOpened(View view){
//				getActionBar().setTitle("打开的");
				invalidateOptionsMenu();
			}
			
		};
		drawerlayout_main.setDrawerListener(mDrawerToggle);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);	
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		// If the nav drawer is open, hide action items related to the content view  
//        boolean drawerOpen = drawerlt_main.isDrawerOpen(relativeLayout_main);  
        //这里可以隐藏导航栏中的图标 
        return super.onPrepareOptionsMenu(menu);
	}
	
	//返回左侧滑动菜单的数据
	private ArrayList<HashMap<String, Object>> getLeftView(){
		
		ArrayList<HashMap<String, Object>> arraylist_main_leftmenu = new ArrayList<HashMap<String,Object>>();
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("title", "下载词库");
		hashMap.put("img", R.drawable.ic_action_download);
		arraylist_main_leftmenu.add(hashMap);
		hashMap = new HashMap<String, Object>();
		hashMap.put("title", "反馈");
		hashMap.put("img", R.drawable.ic_action_settings);
		arraylist_main_leftmenu.add(hashMap);
		hashMap = new HashMap<String, Object>();
		hashMap.put("title", "帮助");
		hashMap.put("img", R.drawable.ic_action_help);
		arraylist_main_leftmenu.add(hashMap);
		return arraylist_main_leftmenu;
	}
	
	// ...
	@Override   
	protected void onPostCreate(Bundle savedInstanceState) {  
        super.onPostCreate(savedInstanceState);  
        // Sync the toggle state after onRestoreInstanceState has occurred.  
        mDrawerToggle.syncState();  
    }  
  
    @Override  
    public void onConfigurationChanged(Configuration newConfig) {  
        super.onConfigurationChanged(newConfig);  
        // Pass any configuration change to the drawer toggls  
        mDrawerToggle.onConfigurationChanged(newConfig);  
    } 
    
	//设置Grammer列表和Test列表
	public void initListView(){
		//获取列表
		arraylist0_1 = getGrammar();
		arraylist0_2 = getTestLibrary();
		
		TabOnItemClickListener tabonitemclicklistener = new TabOnItemClickListener();
		//Grammer的列表
		list0_1 = (ListView) findViewById(R.id.list_View0_1);
		//构造适配器，String[]里面的是HashMap中对应的键值名，后面的int[]是对应放入的TextView
		SimpleAdapter listItemAdapter0_1 = new SimpleAdapter(this, arraylist0_1,
				R.layout.list_adapter, new String[] {"title", "description"}, new int[] {R.id.textView2_1, R.id.textView2_2});
		//将适配器置到ListView中
		list0_1.setAdapter(listItemAdapter0_1);
		// 添加ListView点击
		list0_1.setOnItemClickListener(tabonitemclicklistener);
		list0_1.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				detector.onTouchEvent(arg1);
				return false;
			}
		});
		
		//TestLibrary的列表
		list0_2 = (ListView) findViewById(R.id.list_View0_2);
		myadapter0_1 = new TestLibAdapter(this);
		list0_2.setAdapter(myadapter0_1);
		list0_2.setOnItemClickListener(tabonitemclicklistener);
		list0_2.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				detector.onTouchEvent(arg1);
				return false;
			}
		});
	}

	//设置tabhost选项卡
	public void initTab(){
		
		tabhost = (TabHost)findViewById(R.id.tabhost); //控件与资源ID绑定
		tabhost.setup();
		tabhost.addTab(tabhost.newTabSpec("Grammar").setContent(R.id.tab0_1).setIndicator("Grammar"));
		tabhost.addTab(tabhost.newTabSpec("Test").setContent(R.id.tab0_2).setIndicator("Test"));
		tabhost.setCurrentTab(0);
	
	}
	
//	//监听手势的类
//	@Override
//	public boolean onTouchEvent(MotionEvent event){
//		detector.onTouchEvent(event);
//		return super.onTouchEvent(event);
//	}
	
	@SuppressWarnings("deprecation")
	public void initGestureDetector(){
		//手势监听
		 detector = new GestureDetector(new GestureDetector.OnGestureListener(){
			
			@Override
			public boolean onSingleTapUp(MotionEvent arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void onShowPress(MotionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
					float arg3) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void onLongPress(MotionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
					float arg3) {
				// TODO Auto-generated method stub
				if((arg1.getRawX()-arg0.getRawX()) > 60){
					//向后翻
					tabhost.setCurrentTab(currentTab = currentTab == 1 ? currentTab = 0 : ++currentTab);
					System.out.println("向后翻");
					return true;
				}
				else if((arg0.getRawX()-arg1.getRawX()) > 60){
					//向前翻
					tabhost.setCurrentTab(currentTab = currentTab == 0 ? currentTab = 1 : --currentTab);
					System.out.println("向前翻");
					return true;
				}
				else
					return true;
			}
			
			@Override
			public boolean onDown(MotionEvent arg0) {
				// TODO Auto-generated method stub
				System.out.println("Down");
				return false;
			}
			
//			@Override
//			public boolean onTouchEvent(MotionEvent event){
//					return detector.onTouchEvent(event);
//			}
		});
		 
	}

	//这个是设置的弹出菜单的函数
	@Override
 	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		System.out.println("menu!");
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	//弹出菜单响应函数
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		System.out.println(item.getItemId());
		
		//判断是否是Drawer的按钮被点击
		if(mDrawerToggle.onOptionsItemSelected(item)){
			return true;
		}
		
		//获取菜单项的Id
		switch(item.getItemId()){
		case R.id.action_search:
			break;
		case R.id.action_exit:
			System.exit(0);
		}
		
		//方法回调
		return super.onOptionsItemSelected(item);
	}
	
	// 长按菜单响应函数
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		setTitle("点击了长按菜单里面的第" + item.getItemId() + "个项目");
		return super.onContextItemSelected(item);
	}
	
	//获取数据库中Grammer表的所有信息
	public ArrayList<HashMap<String, Object>> getGrammar(){
		//泛型数组
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		try{
			//数据库创建
			context = (Context)this;
			myDbHelper = new MyDBHelper(context);
			mdb = myDbHelper.getReadableDatabase();
			
			Cursor cursor = null;
			//获取数据库中所有元组
			cursor = mdb.query("Grammar", new String[] {"id","title","description"}, null, null, null, null, null);
			if(cursor.getCount()==0)
				Toast.makeText(this,"no data in table Grammar!",Toast.LENGTH_SHORT).show();
			
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

	//获取数据库中TestLibrary表的信息
	public ArrayList<HashMap<String, Object>> getTestLibrary(){
		
		//泛型数组
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		try{
			//数据库创建
			context = (Context)this;
			myDbHelper = new MyDBHelper(context);
			mdb = myDbHelper.getReadableDatabase();
			
			Cursor cursor = null;
			//获取数据库中所有元组
			cursor = mdb.query("TestLibrary", new String[] {"id","grammarId", "title","description", "itemCount", "level", "createDate"}, null, null, null, null, null);
			if(cursor.getCount()==0)
				Toast.makeText(this, "no data in table TestLibrary", Toast.LENGTH_SHORT).show();
			
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

	//ListView表中选项的单击监听器
	
	//每一个ListView的监听器
	public class TabOnItemClickListener implements OnItemClickListener{
		
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			
			//单击Grammer那一栏
			if(arg0.getAdapter() == list0_1.getAdapter()){
				System.out.println("Grammer列表单击事件！");
				//跳转到Grammer显示的界面，传递数据
				Intent intent = new Intent();
				intent.setClass(MainActivity.this,Grammar.class);
				//在Bundle中存放数据，使用“键”-“值”的方式存放数据
				Bundle bundle = new Bundle();
				bundle.putString("id",arraylist0_1.get(arg2).get("id").toString());
				intent.putExtras(bundle);
				startActivity(intent);
			}
			
			//单击TestLibrary那一栏
			else if(arg0.getAdapter() == list0_2.getAdapter()){
				
				System.out.println("TestLibrary列表单击事件！");
				//跳转到Test显示的界面，传递数据
				Intent intent = new Intent();
				intent.setClass(MainActivity.this,TimeTest.class);
				
				//在Bundle中存放数据，使用“键”-“值”的方式存放数据
				Bundle bundle = new Bundle();
				bundle.putString("libraryId",arraylist0_2.get(arg2).get("id").toString());
				bundle.putString("title",arraylist0_2.get(arg2).get("title").toString());
				bundle.putInt("level", Integer.parseInt(arraylist0_2.get(arg2).get("level").toString()));
				intent.putExtras(bundle);
				startActivity(intent);
				System.out.println("Original MainActivity!");
			}	
			else if(arg0.getAdapter() == listview_main_left_menu.getAdapter()){
				System.out.println("LeftMenu滑动菜单的单击事件");
				Intent intent;
				switch (arg2) {
				case 0:
					intent = new Intent(MainActivity.this, DownloadLibrary.class);
					startActivity(intent);
					break;
				case 1:
					intent = new Intent(MainActivity.this, FeedBack.class);
					startActivity(intent);
					break;
				case 2:
					intent = new Intent(MainActivity.this, AboutGrammar.class);
					startActivity(intent);
					break;
				default:
					break;
				}
			}
			else {
				//下载词库
				if(arg2 == 0){
					Intent intent = new Intent(MainActivity.this, DownloadLibrary.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
				
			}
		}
	}

	
	//TestLibrary列表中删除和详细信息的监听器
	public class lib_Imgbtn_Listener implements OnClickListener{
		
		private int lib_order;
		private ProgressDialog proDialog;
		
		public lib_Imgbtn_Listener(int lib_order){
			this.lib_order = lib_order;
		}
		
		@Override
		public void onClick(View arg0) {
			if(arg0.getId() == R.id.imageButton3_2){
				showDetailDialog();
			}
			else if(arg0.getId() == R.id.imageButton3_3){
				handleDelete();
			}
			else{
				System.out.println("Something wrong in the lib_Imgbtn_Listener!");
			}
		}
		
		//响应点击删除事件
		public void handleDelete(){
			AlertDialog.Builder builder = new Builder(MainActivity.this);
			builder.setMessage("确定删除此项？");
			builder.setTitle("提示");
			builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					//这个是对话框要消失时的对话框
					arg0.dismiss();
					if(!deleteLib())
						Toast.makeText(getApplicationContext(), "删除失败！", Toast.LENGTH_SHORT).show();
				}
			});
			builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {			
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
				}
			});
			builder.create().show();
		}
		
		//响应点击详细事件
		public void showDetailDialog(){
			LayoutInflater inflater = getLayoutInflater();
			View layout = inflater.inflate(R.layout.detail_dialog, (ViewGroup)findViewById(R.id.linear_detail));
			
			TextView tv_title = (TextView)layout.findViewById(R.id.tv_detail_title);
			tv_title.setText("名称："+arraylist0_2.get(lib_order).get("title"));
			TextView tv_description = (TextView)layout.findViewById(R.id.tv_detail_descrip);
			tv_description.setText("简介："+arraylist0_2.get(lib_order).get("description"));
			TextView tv_count = (TextView)layout.findViewById(R.id.tv_detial_count);
			tv_count.setTag("题数："+arraylist0_2.get(lib_order).get("itemCount"));
			TextView tv_date = (TextView)layout.findViewById(R.id.tv_detail_date);
			tv_date.setText("创建时间："+arraylist0_2.get(lib_order).get("createDate"));
			
			new AlertDialog.Builder(MainActivity.this).setTitle("详细").setView(layout).setNegativeButton("取消", null).show();
		}
		
		public boolean deleteLib(){
			try {
				initProgressDialog();
				myDbHelper = new MyDBHelper(getApplicationContext());
				mdb = myDbHelper.getWritableDatabase();
				String str_dt_lib = "delete from 'TestLibrary' where id='"+arraylist0_2.get(lib_order).get("id")+"'";
				mdb.execSQL(str_dt_lib);
				str_dt_lib = "delete from 'TestItem' where libraryId='"+arraylist0_2.get(lib_order).get("id")+"'";
				mdb.execSQL(str_dt_lib);
				arraylist0_2.remove(lib_order);
				myadapter0_1.notifyDataSetChanged();
				proDialog.dismiss();
				mdb.close();
				return true;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				mdb.close();
				proDialog.dismiss();
				return false;
			}
		}
		
		public void initProgressDialog(){
			ProgressDialog proDialog = new ProgressDialog(MainActivity.this);
			proDialog.setTitle("提示");
			proDialog.setMessage("正在删除！");
			proDialog.setCancelable(false);
			proDialog.show();
		}
	}
	
	//TestLibrary列表中点击向下扩展按钮的监听器
	public class lib_Imgbtn_ExpListener implements OnClickListener{
		private RelativeLayout relativeLayout;
		
		public lib_Imgbtn_ExpListener(RelativeLayout relativeLayout) {
			this.relativeLayout = relativeLayout;
		}
		
		@Override
		public void onClick(View arg0){
			
			if(relativeLayout.getVisibility() == View.GONE){
				relativeLayout.setVisibility(View.VISIBLE);
				((ImageButton)arg0).setImageDrawable(getResources().getDrawable(R.drawable.ic_action_collapse));
			}
			else if(relativeLayout.getVisibility() == View.VISIBLE){
				relativeLayout.setVisibility(View.GONE);
				((ImageButton)arg0).setImageDrawable(getResources().getDrawable(R.drawable.ic_action_expand));
			}
			else {
				System.out.println("Something wrong in the lib_Imgbtn_Explistener!");
			}
		}
	}
	
	//TestLibrary自定义适配器
	public class TestLibAdapter extends BaseAdapter{
		
		private LayoutInflater mInflater = null;
		
		//构造函数
		public TestLibAdapter(Context context){
			this.mInflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount(){
			return arraylist0_2.size();
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
				convertView = mInflater.inflate(R.layout.list_adapter02, null);
				
				mvg.textView0_1 = (TextView)convertView.findViewById(R.id.textView3_1);
//				mvg.textView0_2 = (TextView)convertView.findViewById(R.id.textView3_2);
//				mvg.textView0_3 = (TextView)convertView.findViewById(R.id.textView3_3);
				
				mvg.imageBtn0_1 = (ImageButton)convertView.findViewById(R.id.imageButton3_1);
				mvg.imageBtn0_2 = (ImageButton)convertView.findViewById(R.id.imageButton3_2);
				mvg.imageBtn0_3 = (ImageButton)convertView.findViewById(R.id.imageButton3_3);
				
				mvg.relativeLayout0_1 = (RelativeLayout)convertView.findViewById(R.id.relativeLayout3_3);
				mvg.ratingBar0_1 = (RatingBar)convertView.findViewById(R.id.ratingBar3_1);
				convertView.setTag(mvg);
			}
			else{
				mvg = (TestLibViewGroup)convertView.getTag();
			}
			
			mvg.textView0_1.setText((String)arraylist0_2.get(position).get("title"));
//			mvg.textView0_2.setText((String)arraylist0_2.get(position).get("description"));
//			mvg.textView0_3.setText((Integer)arraylist0_2.get(position).get("itemCount")+"");
			
			mvg.ratingBar0_1.setRating((Integer)arraylist0_2.get(position).get("level"));
			mvg.imageBtn0_1.setOnClickListener(new lib_Imgbtn_ExpListener(mvg.relativeLayout0_1));
			mvg.imageBtn0_2.setOnClickListener(new lib_Imgbtn_Listener(position));
			mvg.imageBtn0_3.setOnClickListener(new lib_Imgbtn_Listener(position));
			
			return convertView;
		}
		
		//自定义视图组，为了自定义适配器
		public class TestLibViewGroup{
			
			TextView textView0_1;
			TextView textView0_2;
			TextView textView0_3;
			
			ImageButton imageBtn0_1;
			ImageButton imageBtn0_2;
			ImageButton imageBtn0_3;
			
			RatingBar ratingBar0_1;
			RelativeLayout relativeLayout0_1;
		}
	}
	
	//重写按下返回键
		@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode == KeyEvent.KEYCODE_BACK){
			exitDialog();
			return true;
		}
		else
			return super.onKeyDown(keyCode, event);
	}
	
	// 退出的对话框
	protected void exitDialog(){
		 AlertDialog.Builder builder = new Builder(MainActivity.this);
		  builder.setMessage("确认退出吗？");  
		  builder.setTitle("提示"); 
		  builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {   
		   public void onClick(DialogInterface dialog, int which) {
		    dialog.dismiss();    
		    System.exit(0);
		   }
		  });  
		  builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {  
		   public void onClick(DialogInterface dialog, int which) {
		    dialog.dismiss();
		   }
		  });  
		  builder.create().show();
	}
	
	// 导入数据库文件
	public void importDataBase(){
		String DATABASE_PATH = MyDBHelper.DATABASE_PATH;
		String DATABASE_NAME = MyDBHelper.DATABASE_NAME;
		if ((new File(DATABASE_PATH + DATABASE_NAME)).exists() == false) {
			// 如 SQLite 数据库文件不存在，再检查一下 database 目录是否存在
			File f = new File(DATABASE_PATH);
			// 如 database 目录不存在，新建该目录
			if (!f.exists()) {
				f.mkdir();
			}

			try {
				// 得到 assets 目录下我们实现准备好的 SQLite 数据库作为输入流
				InputStream is = getBaseContext().getAssets().open(DATABASE_NAME);
				// 输出流
				OutputStream os = new FileOutputStream(DATABASE_PATH + DATABASE_NAME);

				// 文件写入
				byte[] buffer = new byte[1024];
				int length;
				while ((length = is.read(buffer)) > 0) {
					os.write(buffer, 0, length);
				}

				// 关闭文件流
				os.flush();
				os.close();
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
