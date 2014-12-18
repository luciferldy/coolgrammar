package com.example.coolgrammer;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.thread.CloudTranslateJsonParsonRunnable;
/**
 * ActionBar实现搜索
 * @author liandongyang
 * Date:2014.10.13
 */
public class SearchActionBar extends ActionBarActivity {
	
	ListView translate_result;
	Handler handler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cloud_translate);
		handler = new Handler();
		initResultView();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.air_search, menu);//获取menu目录下simple.xml的菜单文件
		setSearchActionBar(menu);
		setTitle("");
		return true;
	}
	/**
	 * 为ActionBar实现查询功能
	 * @param menu
	 */
	private void setSearchActionBar(Menu menu) {
		// TODO Auto-generated method stub
		final MenuItem item = menu.findItem(R.id.air_search);
		item.expandActionView();
		SearchView sv = (SearchView) MenuItemCompat.getActionView(item);
		if(sv != null){
			sv.setSubmitButtonEnabled(true);
			sv.setOnQueryTextListener(new OnQueryTextListener() {
				@Override
				public boolean onQueryTextSubmit(String arg0) {
					MenuItemCompat.collapseActionView(item);
					Thread thread = new Thread(new CloudTranslateJsonParsonRunnable(translate_result, getApplicationContext(), arg0, handler));
					thread.start();
					return true;
				}				
				@Override
				public boolean onQueryTextChange(String arg0) {
					return false;
				}
			});
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();		
		switch (id) {		
		case R.id.air_search_delete:
			return true;
		case R.id.home:
			SearchActionBar.this.finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	protected void initResultView(){
		translate_result = (ListView)findViewById(R.id.trans_result);
	}
}
