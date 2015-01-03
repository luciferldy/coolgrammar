package com.example.coolgrammer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import com.example.thread.CloudTranslateJsonParsonRunnable;
/**
 * ActionBar实现搜索
 * @author liandongyang
 * Date:2014.10.13
 */
public class SearchActionBar extends Activity {
	
	ListView translate_result;
	Handler handler;
	ProgressBar loading_translate;
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
		getMenuInflater().inflate(R.menu.cloud_translate, menu);//获取menu目录下simple.xml的菜单文件
		setSearchActionBar(menu);
		return true;
	}
	/**
	 * 为ActionBar实现查询功能
	 * @param menu
	 */
	private void setSearchActionBar(Menu menu) {
		// TODO Auto-generated method stub
		getActionBar().setDisplayShowTitleEnabled(false);
		loading_translate = (ProgressBar)findViewById(R.id.cloud_translate_progressbar);
		loading_translate.setVisibility(View.GONE);
		final MenuItem item = menu.findItem(R.id.air_search);
		item.expandActionView();
		SearchView sv = (SearchView) MenuItemCompat.getActionView(item);
		if(sv != null){
			sv.setSubmitButtonEnabled(true);
			sv.setQueryHint(getResources().getString(R.string.cloud_translate_hint));
//			sv.setIconifiedByDefault(false);
			sv.setIconified(false);
			sv.setOnQueryTextListener(new OnQueryTextListener() {
				@Override
				public boolean onQueryTextSubmit(String arg0) {
					if(checkInput(arg0.trim())){
						loading_translate.setVisibility(View.VISIBLE);
						translate_result.setVisibility(View.GONE);
						Thread thread = new Thread(new CloudTranslateJsonParsonRunnable(translate_result, getApplicationContext(),
								arg0.trim(), handler, loading_translate, "dictionary_entozh"));
						thread.start();
					}
					else{
						loading_translate.setVisibility(View.VISIBLE);
						translate_result.setVisibility(View.GONE);
						Thread thread = new Thread(new CloudTranslateJsonParsonRunnable(translate_result, getApplicationContext(),
								arg0.trim(), handler, loading_translate, "dictionary_zhtoen"));
						thread.start();
					}
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
		case R.id.air_search_back:
			finish();
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
	
	protected boolean checkInput(String input){
		String word="qazwsxedcrfvtgbyhnujmiklopQAZWSXEDCRFVTGBYHNUJMILOP ";
		for (int i = 0; i < input.length(); i++) {
			if (word.contains(input.charAt(i)+"")) {
				
			}
			else {
//				Toast.makeText(this, "请输入一个单词", Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		return true;
	}
}
