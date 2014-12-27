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
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.view.View;
import android.widget.SimpleAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import com.astuetz.PagerSlidingTabStrip;
import com.example.adapter.MainViewPagerAdapter;
import com.example.listener.MainDrawerItemClickListener;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainActivity extends FragmentActivity {
	
	private PagerSlidingTabStrip main_tabs;
	private ViewPager main_viewpager;
	private MainViewPagerAdapter main_viewpager_adapter;
	
	//以下是左侧侧滑菜单的控件显示
	private ActionBarDrawerToggle mDrawerToggle;
	private DrawerLayout drawerlayout_main;
//	private RelativeLayout relativeLayout_main;
	private ListView listview_main_left_menu;
	
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
	
	//初始化pagersliding和viewpager
	@SuppressLint("ResourceAsColor")
	public void initView(){
		main_tabs = (PagerSlidingTabStrip)findViewById(R.id.tabs);
		main_tabs.setShouldExpand(true);
		main_viewpager = (ViewPager)findViewById(R.id.main_viewpager);
		main_viewpager_adapter = new MainViewPagerAdapter(getSupportFragmentManager());
		
		main_viewpager.setAdapter(main_viewpager_adapter);
		int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
				.getDisplayMetrics());
		main_viewpager.setPageMargin(pageMargin);
		main_tabs.setViewPager(main_viewpager);
		// 这里整整写了一晚上啊，坑爹啊，直接设置貌似是不好使
		main_tabs.setIndicatorColor(getResources().getColor(R.color.focusholo));
	}
		
	// 初始化左侧滑菜单
	@SuppressLint("NewApi")
	public void initDrawerMenu(){
		//获得Drawer
		drawerlayout_main = (DrawerLayout)findViewById(R.id.drawer_layout);
		listview_main_left_menu = (ListView)findViewById(R.id.listview_left_menu_main);
		//配置适配器
		SimpleAdapter left_menu_adapter = new SimpleAdapter(this, initDrawerView(), R.layout.left_menu_adapter, new String[]{"img", "title"}, 
				new int[]{R.id.imgview_main_leftmenu, R.id.textview_main_leftmenu});
		listview_main_left_menu.setAdapter(left_menu_adapter);
		//设置监听器
		listview_main_left_menu.setOnItemClickListener(new MainDrawerItemClickListener(getApplicationContext()));
		drawerlayout_main.setDrawerShadow(null, GravityCompat.START);
		//设置滑动菜单的监听器
		mDrawerToggle = new ActionBarDrawerToggle(this, drawerlayout_main, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close){
			public void onDrawerClosed(View view){
				invalidateOptionsMenu();
			}
			public void onDrawerOpened(View view){
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
	private ArrayList<HashMap<String, Object>> initDrawerView(){
		
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
		return super.onContextItemSelected(item);
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
