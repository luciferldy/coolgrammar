package com.example.coolgrammer;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
/**
 * ActionBar实现搜索
 * @author liandongyang
 * Date:2014.10.13
 */
public class SearchActionBar extends ActionBarActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.air_search, menu);//获取menu目录下simple.xml的菜单文件
		setSearch(menu);
		setTitle("");
		return true;
	}
	/**
	 * 为ActionBar实现查询功能
	 * @param menu
	 */
	private void setSearch(Menu menu) {
		// TODO Auto-generated method stub
		final MenuItem item = menu.findItem(R.id.air_search);
		item.expandActionView();
		SearchView sv = (SearchView) MenuItemCompat.getActionView(item);
		if(sv != null){
			sv.setOnQueryTextListener(new OnQueryTextListener() {
				@Override
				public boolean onQueryTextSubmit(String arg0) {
					Toast.makeText(getApplicationContext(), " ", Toast.LENGTH_SHORT).show();
					MenuItemCompat.collapseActionView(item);
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
}
