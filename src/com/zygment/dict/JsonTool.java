package com.zygment.dict;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.zygment.dict.webData.Result;
import com.zygment.dict.webData.WebData;

public class JsonTool {

	public static Result getPerson(String jsonString) {
		Result result = null;
		List<WebData> webList = new ArrayList<WebData>();
		boolean isCn = false;
		String temp = "";
		WebData webData = null;
		try {
			result = new Result();
			JSONObject json = new JSONObject(jsonString);
			JSONArray transArray = json.getJSONArray("translation");  // 肯定有
			StringBuffer transBuffer = new StringBuffer();
			for (int i = 0; i < transArray.length(); i++) {
				transBuffer.append(transArray.get(i) + ",");
			}
			temp = transBuffer.toString();
			temp = temp.substring(0, temp.length() - 1);// 去掉最后一个逗号
			result.setTranslation(temp);
			System.out.println("translation=" + result.getTranslation());
			

			// 解析源翻译
			result.setQuery(json.getString("query")); // 肯定有
			// 状态码
			result.setErrorCode(json.getInt("errorCode")); // 肯定有
			/**
			 * 解析基本翻译 可能没有
			 */
			// 拿到Json数据里的basic对象
			if (json.has("basic")) { // 是否有basic对象
				JSONObject basicJsonObject = json.getJSONObject("basic");
				// 拿到basic对象里的explains数组
				JSONArray explainsArray = basicJsonObject
						.getJSONArray("explains");
				StringBuffer expBuffer = new StringBuffer();
				for (int i = 0; i < explainsArray.length(); i++) {
					// 遍历explains数组 并将每行内容封装到Stringbuffer中
					expBuffer.append(explainsArray.get(i) + "\n");// 换行
				}
				result.setBasicTrans(expBuffer.toString());

				if (basicJsonObject.has("uk-phonetic")) { // 是否有美式或英式音标
					result.setUk_phonetic("UK:[ "+basicJsonObject
							.getString("uk-phonetic")+" ]");
					result.setUs_phonetic("US:[ "+basicJsonObject
							.getString("us-phonetic")+" ]");
					
				} else {
					result.setUk_phonetic("");
					result.setUs_phonetic("");
				} // 保存音标
				if(basicJsonObject.has("phonetic")){
					result.setPhonetic("[ "+basicJsonObject.getString("phonetic")+" ]");
				}
				else {
					result.setPhonetic("");
				}
				
			} else {
				result.setBasicTrans("");
				result.setUk_phonetic("");
				result.setUs_phonetic("");
				result.setPhonetic("");
				result.setBasicTrans("");
			}

			// 基本释义
			/**
			 * 解析网络释义
			 */
			// 拿到web json数组
			if (json.has("web")) { // 是否有web对象
				JSONArray webJson = json.getJSONArray("web");
				// web数组里有若干个json对象 将其存放在list中

				// 遍历web数组
				for (int i = 0; i < webJson.length(); i++) {
					// 拿到web数组里指定下标json对象
					webData = new WebData();
					JSONObject webJsonObject = webJson.getJSONObject(i);
					webData.setKey(webJsonObject.getString("key")); // 保存key
					JSONArray valueArray = webJsonObject.getJSONArray("value"); // 拿到value数组
					StringBuffer valueBuffer = new StringBuffer();
					for (int j = 0; j < valueArray.length(); j++) {
						// 遍历value数组
						valueBuffer.append(valueArray.get(j) + ", ");// 解析value数组里的数据，并封装到Stringbuffer中，逗号隔开
					}
					temp = valueBuffer.toString();
					temp = temp.substring(0, temp.length() - 2);
					webData.setValue(temp);
					webList.add(webData);
				}

			} else {
				webData.setValue("");
				webData.setKey("");
				webList.add(webData);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		result.setWebDatas(webList); // 保存weblist

		return result;
	}

}
