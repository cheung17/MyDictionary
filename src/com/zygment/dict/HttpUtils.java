
package com.zygment.dict;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.Buffer;

	public class HttpUtils {
		public static String getJson(String queryword) throws Exception{
			String path="http://fanyi.youdao.com/openapi.do?keyfrom=ztxDirct&key=" +
					"1247246304&type=data&doctype=json&version=1.1&q="+URLEncoder.encode(queryword, "utf-8");
			URL url=new URL(path);
			String result="";
			HttpURLConnection conn=
			        (HttpURLConnection)url.openConnection();
			conn.setConnectTimeout(3000);
			conn.setRequestMethod("GET");	
			InputStream is = conn.getInputStream();  //得到返回流
			ByteArrayOutputStream os = new ByteArrayOutputStream();  
			 int len = 0;  
			byte buffer[] =new byte[1024];
			while((len=is.read(buffer))!=-1){
				os.write(buffer,0,len);
			}
			is.close();
			os.close();
		    result=new String(os.toByteArray());	  
		    System.out.println(result);
		    conn.disconnect();
			return result;
		}
	}