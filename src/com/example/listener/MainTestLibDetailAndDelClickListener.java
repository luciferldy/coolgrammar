package com.example.listener;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adapter.MainTestLibAdapter;
import com.example.coolgrammer.MyDBHelper;
import com.example.coolgrammer.R;

public class MainTestLibDetailAndDelClickListener implements OnClickListener{
	
	private int lib_order;
	private ProgressDialog proDialog;
	private MyDBHelper myDbHelper = null;
	private SQLiteDatabase mdb = null ;
	private Context context;
	private ArrayList<HashMap<String, Object>> testlib_infor = new ArrayList<HashMap<String,Object>>();
	private MainTestLibAdapter adapter;
	
	public MainTestLibDetailAndDelClickListener(int lib_order, Context context, ArrayList<HashMap<String, Object>> testlib_infor,
			MainTestLibAdapter adapter){
		this.lib_order = lib_order;
		this.context = context;
		this.testlib_infor = testlib_infor;
		this.adapter = adapter;
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
		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage("确定删除此项？");
		builder.setTitle("提示");
		builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				//这个是对话框要消失时的对话框
				arg0.dismiss();
				if(!deleteLib())
					Toast.makeText(context, "删除失败！", Toast.LENGTH_SHORT).show();
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
		LayoutInflater inflater = LayoutInflater.from(context);
		View layout = inflater.inflate(R.layout.detail_dialog, (ViewGroup)(context.getResources().getLayout(R.id.linear_detail)));
		
		TextView tv_title = (TextView)layout.findViewById(R.id.tv_detail_title);
		tv_title.setText("名称："+testlib_infor.get(lib_order).get("title"));
		TextView tv_description = (TextView)layout.findViewById(R.id.tv_detail_descrip);
		tv_description.setText("简介："+testlib_infor.get(lib_order).get("description"));
		TextView tv_count = (TextView)layout.findViewById(R.id.tv_detial_count);
		tv_count.setTag("题数："+testlib_infor.get(lib_order).get("itemCount"));
		TextView tv_date = (TextView)layout.findViewById(R.id.tv_detail_date);
		tv_date.setText("创建时间："+testlib_infor.get(lib_order).get("createDate"));
		
		new AlertDialog.Builder(context).setTitle("详细").setView(layout).setNegativeButton("取消", null).show();
	}
	
	public boolean deleteLib(){
		try {
			initProgressDialog();
			myDbHelper = new MyDBHelper(context);
			mdb = myDbHelper.getWritableDatabase();
			String str_dt_lib = "delete from 'TestLibrary' where id='"+testlib_infor.get(lib_order).get("id")+"'";
			mdb.execSQL(str_dt_lib);
			str_dt_lib = "delete from 'TestItem' where libraryId='"+testlib_infor.get(lib_order).get("id")+"'";
			mdb.execSQL(str_dt_lib);
			testlib_infor.remove(lib_order);
			adapter.notifyDataSetChanged();
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
		proDialog = new ProgressDialog(context);
		proDialog.setTitle("提示");
		proDialog.setMessage("正在删除！");
		proDialog.setCancelable(false);
		proDialog.show();
	}

}
