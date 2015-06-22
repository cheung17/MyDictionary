package com.zygment.dict.webData;

import java.util.List;

import com.zygment.dict.webData.WebData;

public class Result {




	private  String query,translation,
	phonetic,us_phonetic,uk_phonetic,basicTrans;
	private List<WebData>webDatas;
	private int errorCode;

	
  
	@Override		
	public String toString() {
		return "Result [query=" + query + ", translation=" + translation
				+ ", errorCode=" +
				errorCode + ",us_phonetic="+us_phonetic+",uk_phonetic="+uk_phonetic+",phonetic="+phonetic+"]";
	}
	public List<WebData> getWebDatas() {
		return webDatas;
	}
	public void setWebDatas(List<WebData> webDatas) {
		this.webDatas = webDatas;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getTranslation() {
		return translation;
	}
	public void setTranslation(String translation) {
		this.translation = translation;
	}
	public String getPhonetic() {
		return phonetic;
	}
	public void setPhonetic(String phonetic) {
		this.phonetic = phonetic;
	}
	public String getUs_phonetic() {
		return us_phonetic;
	}
	public void setUs_phonetic(String us_phonetic) {
		this.us_phonetic = us_phonetic;
	}
	public String getUk_phonetic() {
		return uk_phonetic;
	}
	public void setUk_phonetic(String uk_phonetic) {
		this.uk_phonetic = uk_phonetic;
	}
	public String getBasicTrans() {
		return basicTrans;
	}
	public void setBasicTrans(String basicTrans) {
		this.basicTrans = basicTrans;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
}
