package com.example.coolgrammer;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class FeedBack extends Activity{
	
	private ProgressDialog progressDialog;
	private Handler handler;
	EditText feedback_content;
	@Override
	protected void onCreate(Bundle savedInstanceBundle){
		super.onCreate(savedInstanceBundle);
		setContentView(R.layout.feedbackshow);
		setProgressDialog();
		Button feedbackButton = (Button)findViewById(R.id.feedback_commit);
		handler = new Handler();
		feedbackButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				feedback_content = (EditText)findViewById(R.id.feedback_content);
				String content = feedback_content.getText().toString();
				if(content.equals("")){
					return;
				}
				else if (content.equals("delete angles db")) {
					delteDatabase();
					return;
				}
				else {
					Thread thread = new Thread(new FeedbackCommitRunnable());
					thread.start();
					progressDialog.show();
				}
			}
		});
	}
	
	public void feedbackCommit(){
		String content = feedback_content.getText().toString();
		System.out.println(content);
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost("http://1.coolgrammar.sinaapp.com/feedback.php");
		try {
			List<BasicNameValuePair> paramsList = new ArrayList<BasicNameValuePair>();
			paramsList.add(new BasicNameValuePair("feedback", content));
			paramsList.add(new BasicNameValuePair("address", ""));
			httpPost.setEntity(new UrlEncodedFormEntity(paramsList, HTTP.UTF_8));
			HttpResponse response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == 200) {
				String response_content = EntityUtils.toString(response.getEntity(),"utf-8");
				progressDialog.dismiss();
				if(response_content.equals("success")){
					// 提交成功，退出本界面
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Toast.makeText(getApplicationContext(), "已提交成功，我会尽快处理", Toast.LENGTH_SHORT).show();
							feedback_content.setText("");
						}
					});					
				}
				else {
					// 没提交成功，请重新填写
					System.out.println(response_content);
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Toast.makeText(getApplicationContext(), "没有提交成功，请重新填写", Toast.LENGTH_SHORT).show();
							feedback_content.setText("");
						}
					});
				}
 				
			}
			else{
				System.out.println("the web can not open!");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	public class FeedbackCommitRunnable implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			feedbackCommit();
		}
		
	}
	
	//对于进度条对话框的一些设置
	public void setProgressDialog(){
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("提示！");
		progressDialog.setMessage("正在提交数据");
		progressDialog.setCancelable(false);
	}
	
	// 特殊指令
	public void delteDatabase(){
		AlertDialog.Builder builder = new Builder(FeedBack.this);
		builder.setMessage("你确定删除数据库");
		builder.setTitle("提示");
		builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				//这个是对话框要消失时的对话框
				arg0.dismiss();
				SQLiteDatabase mdb0_1 = null;
				MyDBHelper mdbhelper = null;
				try{
					mdbhelper = new MyDBHelper(getApplicationContext());
					mdb0_1 = mdbhelper.getReadableDatabase();
					mdb0_1.close();
					if(getApplicationContext().deleteDatabase(MyDBHelper.DATABASE_NAME)){
						Toast.makeText(getApplicationContext(), "delete database", Toast.LENGTH_SHORT).show();
					}
				}
				catch(Exception e){
					e.printStackTrace();
					mdb0_1.close();
				}
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
}