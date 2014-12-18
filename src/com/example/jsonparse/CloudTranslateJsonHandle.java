package com.example.jsonparse;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

/*
 * 云翻译json数据解析格式
 * 运用的是百度翻译的api
 * @author:liandongyang
 * data:{} 这样的是jsonobject
 * data:[] 这样的是jsonarray
 */
public class CloudTranslateJsonHandle {
	
	private String keys;
	private String trans_url="http://openapi.baidu.com/public/2.0/bmt/translate?client_id=RCFGRzYNoN00OiGd3UHqLGGS" +
			"&q=";
	private ArrayList<HashMap<String, String>> trans_result;
	private boolean ifreturnresult = true;
	
	public CloudTranslateJsonHandle(String keys) {
		// TODO Auto-generated constructor stub
		this.keys = keys.trim().replace(" ", "%0A");
		Log.v("CloudTranslateJsonHandle.construct", trans_url+keys+"&from=auto&to=auto"); 
		if(getTranslateJsonContent().equals("-1")){
			// 报错
			Log.v("CloudTranslateJsonHandle.CloudTranslateJsonHandle", "cloudtranslate无法获得正常json数据");
			ifreturnresult = false;
		}
		else {
			trans_result = new ArrayList<HashMap<String,String>>();
			parseTranslateJsonData(getTranslateJsonContent());
		}
		
	}
	
	// 返回-1说明遇到了异常,获得翻译的内容
	protected String getTranslateJsonContent(){
		HttpClient httpClient = null;
		try {
			httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(trans_url+keys+"&from=auto&to=auto");
			HttpResponse httpResponse = httpClient.execute(httpGet);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity httpEntity = httpResponse.getEntity();
				// 获得返回的字符串
				String json_data = EntityUtils.toString(httpEntity,"utf-8");
				return json_data;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ifreturnresult = false;
		}
		return "-1";
	}
	
	// 解析json串，解析翻译的串
	protected void parseTranslateJsonData(String json_data){
		HashMap<String, String> hashMap;
		try {
			JSONObject jsonObject = new JSONObject(json_data);
			if(jsonObject.getString("trans_result")!=null){
				JSONArray jsonArray = jsonObject.getJSONArray("trans_result");
				for (int i = 0; i < jsonArray.length(); i++) {
					hashMap = new HashMap<String, String>();
					hashMap.put("src", jsonArray.getJSONObject(i).getString("src"));
					hashMap.put("dst", jsonArray.getJSONObject(i).getString("dst"));
					trans_result.add(hashMap);
				}	
			}
			else {
				//报错
				Log.v("CloudTranslateJsonHandle.parseJsonData", "返回的json数据貌似是出错了那一组");
				ifreturnresult = false;
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ifreturnresult = false;
		}
	}
	
	
	// 解析json串，解析词典的串
	public boolean isgetTranslateResult(){
		return ifreturnresult;
	}
	// 
	public ArrayList<HashMap<String, String>> getTranslateResult(){
		return trans_result;
	}
}
