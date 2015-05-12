package com.example.jsonparse;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

/*
 * 用的是百度词典的api，发现没有能够同时查询多个词的功能
 * @author:liandongyang
 * data:{} 这样的是jsonobject
 * data:[] 这样的是jsonarra
 */
public class CloudDictionaryFromZhtoEnJsonHandle implements CloudDictionaryJsonHandle{
	private String dictionary_url="http://openapi.baidu.com/public/2.0/translate/dict/simple?client_id=RCFGRzYNoN00OiGd3UHqLGGS&q=";
	private String keys;
	private ArrayList<HashMap<String, String>> dictionary_result;
	private boolean ifreturnresult=true;
	
	public CloudDictionaryFromZhtoEnJsonHandle(String keys) {
		// TODO Auto-generated constructor stub
		this.keys = keys.trim();
		if(getDictionaryJsonContent().equals("-1")){
			// 报错
			Log.v("CloudDictionaryJsonHandle.CloudDictionaryJsonHandle", "clouddictionary无法获得正常json数据");
			ifreturnresult = false;
		}
		else {
			dictionary_result = new ArrayList<HashMap<String,String>>();
			parseDictionaryJsonData(getDictionaryJsonContent());
		}
		
	}
	
	// 解析json串，解析翻译的串
	protected void parseDictionaryJsonData(String json_data){
		HashMap<String, String> hashMap = new HashMap<String, String>();
		try {
			JSONObject jsonObject = new JSONObject(json_data);
			if(jsonObject.getString("errno").equals("0")){
				JSONObject data = jsonObject.getJSONObject("data");
				hashMap.put("word_name", data.getString("word_name"));
				JSONArray symbols = data.getJSONArray("symbols");
				for (int i = 0; i < symbols.length(); i++) {
					JSONObject symbolitem = symbols.getJSONObject(i);
					hashMap.put("ph_am", "拼音"+"【"+symbolitem.getString("ph_zh")+"】");
					// 放两个空值进去
					hashMap.put("ph_en", "");
					JSONArray parts = symbolitem.getJSONArray("parts");
					String explain="";
					for (int j = 0; j < parts.length(); j++) {
						JSONObject part = parts.getJSONObject(j);
						JSONArray means = part.getJSONArray("means");
						int k;
						for (k = 0; k < means.length()-1; k++) {
							explain += means.getString(k).trim()+", ";
						}
						explain += means.getString(k)+"\n";
					}
					hashMap.put("explain", explain);
				}
				dictionary_result.add(hashMap);
			}
			else {
				//报错
				Log.v("CloudTranslateJsonHandle.parseJsonData", "返回的json数据貌似是出错了");
				ifreturnresult = false;
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ifreturnresult = false;
		}
	}
	
	// 获得词典的解释
	protected String getDictionaryJsonContent(){
		HttpClient httpClient = null;
		
		// 设置timeout
		HttpParams params = new BasicHttpParams();
		int connectionTimeOut = 3000;  // 连接时常
		HttpConnectionParams.setConnectionTimeout(params, connectionTimeOut);
		int socketTimeOut = 5000;  // socket连接时常
		HttpConnectionParams.setSoTimeout(params, socketTimeOut);
		
		try {
			httpClient = new DefaultHttpClient(params);
			HttpGet httpGet = new HttpGet(dictionary_url+keys+"&from=zh&to=en");
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
	
	// 解析json串，解析词典的串
	public boolean isgetTranslateResult(){
		return ifreturnresult;
	}
	// 
	public ArrayList<HashMap<String, String>> getTranslateResult(){
		return dictionary_result;
	}
}
