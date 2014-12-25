package com.example.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.adapter.MainTestLibAdapter;
import com.example.coolgrammer.R;
import com.example.db.GrammarData;
import com.example.db.TestLibraryData;
import com.example.listener.MainGrammarItemClickListener;
import com.example.listener.MainTestLibItemClickListener;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
		params.setMargins(margin, margin, margin, margin);
		Log.v("MainViewPagerFragment.onCreateView.postion", position+"");
		// 在这里添加控件？
		switch (position) {
		case 0:
			fl.addView(initGrammar());
			break;
		case 1:
			fl.addView(initTestLibrary());
			break;
		default:
			Log.v("MainViewPagerFragment.onCreateView", "Someting wrong!");
		}
		return fl;
	}

	// 造grammar的listview
	public ListView initGrammar(){
		ListView grammar = new ListView(getActivity());
		// 前后顺序很关键
		grammar.setDivider(getResources().getDrawable(R.color.dividergray));
		grammar.setDividerHeight(2);
		ArrayList<HashMap<String, Object>> grammar_infor = new ArrayList<HashMap<String,Object>>();
		grammar_infor = (new GrammarData(getActivity())).getGrammarData();
		//构造适配器，String[]里面的是HashMap中对应的键值名，后面的int[]是对应放入的TextView
		SimpleAdapter listItemAdapter0_1 = new SimpleAdapter(getActivity(), grammar_infor,
				R.layout.grammar_adapter, new String[] {"title", "description"},
				new int[] {R.id.grammar_adapter_title, R.id.grammar_adapter_description});
		//将适配器置到ListView中
		grammar.setAdapter(listItemAdapter0_1);
		// 添加ListView点击
		grammar.setOnItemClickListener(new MainGrammarItemClickListener(getActivity(), grammar_infor));
		return grammar;
	}
	
	// 造testlib的listview
	public ListView initTestLibrary(){
		ListView testlib = new ListView(getActivity());
		//TestLibrary的列表
		ArrayList<HashMap<String, Object>> testlib_infor = new ArrayList<HashMap<String,Object>>();
		testlib_infor = (new TestLibraryData(getActivity())).getTestLibraryData();
		testlib.setAdapter(new MainTestLibAdapter(getActivity(), testlib_infor));
		testlib.setOnItemClickListener(new MainTestLibItemClickListener(getActivity(), testlib_infor));
		
		testlib.setDivider(getResources().getDrawable(R.color.dividergray));
		testlib.setDividerHeight(2);
		
		return testlib;
	}
}
