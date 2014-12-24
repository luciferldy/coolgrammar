package com.example.fragment;

import com.example.coolgrammer.R;
import com.example.coolgrammer.TabOnItemClickListener;
import com.example.coolgrammer.MainActivity.TestLibAdapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.FrameLayout.LayoutParams;

public class MainViewPagerFragment extends Fragment {

	private static final String ARG_POSITION = "position";

	private int position;

	public static MainViewPagerFragment newInstance(int position) {
		MainViewPagerFragment f = new MainViewPagerFragment();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		position = getArguments().getInt(ARG_POSITION);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		FrameLayout fl = new FrameLayout(getActivity());
		fl.setLayoutParams(params);

		final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources()
				.getDisplayMetrics());

//		TextView v = new TextView(getActivity());
//		params.setMargins(margin, margin, margin, margin);
//		v.setLayoutParams(params);
//		v.setLayoutParams(params);
//		v.setGravity(Gravity.CENTER);
//		v.setBackgroundResource(R.drawable.background_card);
//		v.setText("CARD " + (position + 1));
//
//		fl.addView(v);
		// 在这里添加控件？
		return fl;
	}

	public ListView initGrammar(){
		ListView grammar = new ListView(getActivity());
		//构造适配器，String[]里面的是HashMap中对应的键值名，后面的int[]是对应放入的TextView
		SimpleAdapter listItemAdapter0_1 = new SimpleAdapter(this, grammar_infor,
				R.layout.grammar_adapter, new String[] {"title", "description"}, new int[] {R.id.grammar_adapter_title, R.id.grammar_adapter_description});
		//将适配器置到ListView中
		lv_grammar_infor.setAdapter(listItemAdapter0_1);
		// 添加ListView点击
		lv_grammar_infor.setOnItemClickListener(tabonitemclicklistener);
	}
	
	public void initTestLibrary(){
		//TestLibrary的列表
		lv_testlib_infor = (ListView) findViewById(R.id.main_tab_two_listview);
		myadapter0_1 = new TestLibAdapter(this);
		lv_testlib_infor.setAdapter(myadapter0_1);
		lv_testlib_infor.setOnItemClickListener(tabonitemclicklistener);
	}
}
