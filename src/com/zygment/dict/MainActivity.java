package com.zygment.dict;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.zygment.dict.webData.Result;
import com.zygment.dict.webData.WebData;

public class MainActivity extends Activity implements OnEditorActionListener {
	private EditText inputTxt;
	private ImageView queryBtn, playPhotic;
	private TextView phticTx, usPhoticTx, ukPhoticTx, webTransTx, queryWordTx,
			basicTrans;
	private LinearLayout webLinear;
	private RelativeLayout phticRelative;
	private Handler handler = null;
	private String resultJson ="";
	private Result result = null;
	private String webString = "";
	private MediaPlayer mp;
	private SharedPreferences sf=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		setContentView(R.layout.main);
		initViews();
		initEvent();
		/**
		 * 	保存用户上一次输入的单词 每次启动应用时自动显示上次所查询的词	
		 */
		sf=getSharedPreferences("query_word", MODE_PRIVATE); 
		String lastWord=sf.getString("word","thanks for using");
		beforeQuery(lastWord);
	}
    private void saveSf(String word){ //保存单词
    	Editor editor=sf.edit();
    	editor.putString("word", word);
    	editor.commit();
    }
    
    private void beforeQuery(String temp){ //查询之前检查格式
		String temp2=temp.replaceAll(" ", "");
	 	if(!(temp2==null||temp2.isEmpty())){
			if(!isOpenNetwork()){
				Toast.makeText(getApplicationContext(), "网络不可用", 1).show();
			}else {
		     	saveSf(temp);
				beginQuery(temp);				
			}
		}
	
  }
	private void initEvent() {     //初始化事件
		queryBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0){ 
				closeInput(); 
				beforeQuery(inputTxt.getText().toString()); 				
			} 
                
		});
		playPhotic.setOnClickListener(new OnClickListener() {  //播放发音
            
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						String tempUri = "http://dict.youdao.com/dictvoice?audio=";
						tempUri = tempUri + queryWordTx.getText().toString();
						playPhotic(tempUri);
					}

				}).start();

			}
		});
	}

	private void beginQuery(final String queryWord) { // 查词逻辑
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					resultJson = HttpUtils.getJson(queryWord); // 开始连接
					result = JsonTool.getPerson(resultJson); // 解析json
				   if(result.getErrorCode()==0){
						StringBuffer stringBuffer = new StringBuffer();
						List<WebData> webList = result.getWebDatas();
						for (WebData wd : webList) {
							stringBuffer.append(wd.getKey() + ": " + wd.getValue()
									+ "\n");
						}
						webString = stringBuffer.toString();
						handler.post(new Runnable() {
							@Override
							public void run() {
								setResult2View();
							}
							
						});
						
					
				   }	
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
    class workThreadClass extends Thread{
    	public Handler handler1;
    	@Override
    	public void run() {
    		// TODO Auto-generated method stub
    		super.run();
    	}
    }
	private void playPhotic(String url) { // 播放语音逻辑
		if (mp != null) {
			mp.release();// 释放资源
		}
		mp = new MediaPlayer();

		mp.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置媒体流类型
		try {
			mp.setDataSource(url);
			mp.prepare();
			mp.start();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();

		}
	}
   
	private void setResult2View() { // 设置结果显示到视图
		queryWordTx.setText(result.getQuery());
		if (!isChinese(result.getQuery())){  //英文才有发音
			playPhotic.setVisibility(View.VISIBLE);
		}else {
			playPhotic.setVisibility(View.GONE);
		}
		if (!result.getUk_phonetic().equals("")) {
			phticRelative.setVisibility(View.VISIBLE);			
			usPhoticTx.setText(result.getUs_phonetic());
			ukPhoticTx.setText(result.getUk_phonetic());

		} else {
			phticRelative.setVisibility(View.GONE);
			
		}

		/**
		 * 设置基本翻译
		 */
		if (!result.getBasicTrans().equals("")) {
			basicTrans.setText(result.getBasicTrans());
		} else {
			if (!result.getTranslation().equals("")) {
				basicTrans.setText(result.getTranslation());
			}
		}
		if(webString.equals("")){  //Json没有返回web内容 则隐藏掉
			webLinear.setVisibility(View.GONE);
		}else {
			webLinear.setVisibility(View.VISIBLE);
			webTransTx.setText(webString);
		}
	}

	private void initViews() { // 初始化控件
		inputTxt = (EditText) findViewById(R.id.input_txt);
		queryBtn = (ImageView) findViewById(R.id.query_btn);
		handler = new Handler();
		queryWordTx = (TextView) findViewById(R.id.query_word);
		phticTx = (TextView) findViewById(R.id.phtic);
		usPhoticTx = (TextView) findViewById(R.id.us_phtic);
		ukPhoticTx = (TextView) findViewById(R.id.uk_phtic);
		webTransTx = (TextView) findViewById(R.id.web_trans);
		basicTrans = (TextView) findViewById(R.id.basic_trans);
		phticRelative = (RelativeLayout) findViewById(R.id.relative_phtic);
		webLinear = (LinearLayout) findViewById(R.id.Linear_web);
		playPhotic = (ImageView) findViewById(R.id.play_phtic);
		inputTxt.setOnEditorActionListener(this);  //输入法搜索事件
	}

	private static boolean isChinese(String str) {  //源单词是否为中文
		for (int i = 0; i < str.length(); i++) {
			if (str.substring(i, i + 1).matches("[\u4e00-\u9fa5]")) {

				return true;
			}

		}
		return false;
	}
	/**
	 * 对网络连接状态进行判断
	 * @return  true, 可用； false， 不可用
	 */
	
	private boolean isOpenNetwork() {  
		ConnectivityManager connManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		if(connManager.getActiveNetworkInfo() != null) {
			return connManager.getActiveNetworkInfo().isAvailable();
		}

		return false;
	}
	@Override
	public  boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			beforeQuery(inputTxt.getText().toString());
			closeInput(); 		       
			return true;
	}
	private void closeInput(){ //关闭输入法
		 View view = getWindow().peekDecorView();
		 if (view != null) { 			
	            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
	        }
	}
	
}

