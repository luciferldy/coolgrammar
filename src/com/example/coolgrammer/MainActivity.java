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
import android.support.v4.view.ViewPager;
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

import com.astuetz.PagerSlidingTabStrip;
import com.example.listener.MainDrawerListener;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainActivity extends Activity {

	
	
	// 泛型数组+哈希图
	private ArrayList<HashMap<String, Object>> grammar_infor = null;
	private ArrayList<HashMap<String, Object>> testlib_infor = null;
	
	//
	private ListView lv_grammar_infor = null;
	private ListView lv_testlib_infor = null;
	
	PagerSlidingTabStrip main_tabs;
	private ViewPager main_viewpager;
	
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
		initView();
	}
	
	// 初始化左滑菜单
	@SuppressLint("NewApi")
	public void initDrawerMenu(){
		//获得Drawer
		drawerlayout_main = (DrawerLayout)findViewById(R.id.drawer_layout);
		listview_main_left_menu = (ListView)findViewById(R.id.listview_left_menu_main);
		//配置适配器
		SimpleAdapter left_menu_adapter = new SimpleAdapter(this, getLeftView(), R.layout.left_menu_adapter, new String[]{"img", "title"}, new int[]{R.id.imgview_main_leftmenu, R.id.textview_main_leftmenu});
		listview_main_left_menu.setAdapter(left_menu_adapter);
		//设置监听器
		listview_main_left_menu.setOnItemClickListener(new MainDrawerListener(getApplicationContext()));
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
		// boolean drawerOpen = drawerlt_main.isDrawerOpen(relativeLayout_main);  
        // 这里可以隐藏导航栏中的图标 
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
	public void initView(){
		main_tabs = (PagerSlidingTabStrip)findViewById(R.id.tabs);
		main_viewpager = (ViewPager)findViewById(R.id.main_viewpager);
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
		//判断是否是Drawer的按钮被点击
		if(mDrawerToggle.onOptionsItemSelected(item)){
			return true;
		}
		
		//获取菜单项的Id
		switch(item.getItemId()){
		case R.id.action_search:
			Intent intent = new Intent(MainActivity.this, SearchActionBar.class);
			startActivity(intent);
			break;
		case R.id.action_exit:
			exitDialog();
			break;
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
	

	//TestLibrary列表中删除和详细信息的监听器
	public class lib_Imgbtn_Listener implements OnClickListener{
		
		private int lib_order;
		private ProgressDialog proDialog;
		
		public lib_Imgbtn_Listener(int lib_order){
			this.lib_order = lib_order;
		}
		
		@Override
		public void onClick(View arg0) {
			if(arg0.getId() == R.id.testlib_adapter_img_detail){
				showDetailDialog();
			}
			else if(arg0.getId() == R.id.testlib_adapter_img_delete){
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
			tv_title.setText("名称："+testlib_infor.get(lib_order).get("title"));
			TextView tv_description = (TextView)layout.findViewById(R.id.tv_detail_descrip);
			tv_description.setText("简介："+testlib_infor.get(lib_order).get("description"));
			TextView tv_count = (TextView)layout.findViewById(R.id.tv_detial_count);
			tv_count.setTag("题数："+testlib_infor.get(lib_order).get("itemCount"));
			TextView tv_date = (TextView)layout.findViewById(R.id.tv_detail_date);
			tv_date.setText("创建时间："+testlib_infor.get(lib_order).get("createDate"));
			
			new AlertDialog.Builder(MainActivity.this).setTitle("详细").setView(layout).setNegativeButton("取消", null).show();
		}
		
		public boolean deleteLib(){
			try {
				initProgressDialog();
				myDbHelper = new MyDBHelper(getApplicationContext());
				mdb = myDbHelper.getWritableDatabase();
				String str_dt_lib = "delete from 'TestLibrary' where id='"+testlib_infor.get(lib_order).get("id")+"'";
				mdb.execSQL(str_dt_lib);
				str_dt_lib = "delete from 'TestItem' where libraryId='"+testlib_infor.get(lib_order).get("id")+"'";
				mdb.execSQL(str_dt_lib);
				testlib_infor.remove(lib_order);
				myadapter0_1.notifyDataSetChanged();
				mdb.close();
				proDialog.dismiss();
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
			proDialog = new ProgressDialog(MainActivity.this);
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
				
				mvg.relativeLayout0_1 = (RelativeLayout)convertView.findViewById(R.id.testlib_adapter_hide);
				mvg.ratingBar0_1 = (RatingBar)convertView.findViewById(R.id.testlib_adapter_ratingBar);
				convertView.setTag(mvg);
			}
			else{
				mvg = (TestLibViewGroup)convertView.getTag();
			}
			
			mvg.tv_lib_title.setText((String)testlib_infor.get(position).get("title"));
			
			mvg.ratingBar0_1.setRating((Integer)testlib_infor.get(position).get("level"));
			mvg.imgbtn_expand.setOnClickListener(new lib_Imgbtn_ExpListener(mvg.relativeLayout0_1));
			mvg.imgbtn_detail_infor.setOnClickListener(new lib_Imgbtn_Listener(position));
			mvg.imgbtn_delete.setOnClickListener(new lib_Imgbtn_Listener(position));
			
			return convertView;
		}
		
		//自定义视图组，为了自定义适配器
		public class TestLibViewGroup{
			
			TextView tv_lib_title;
			
			ImageButton imgbtn_expand;
			ImageButton imgbtn_detail_infor;
			ImageButton imgbtn_delete;
			
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
