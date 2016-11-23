package com.upsmart.poll.tread;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by norman on 16-11-19.
 */
public class app {
    public static void main(String[] args) {

        CloseableHttpClient httpclient = HttpClients.createDefault();

        while (true) {
            try {

//            HttpHost target = new HttpHost("httpbin.org", 443, "https");
//            HttpHost target = new HttpHost("m.fangxinbao.com/wx/repersentVote.html?userId=4721", 80, "http");

                HttpHost target = new HttpHost("m.fangxinbao.com/wx/repersentVote.html");
                HttpHost proxy = new HttpHost("202.118.236.52", 8080, "http");
                RequestConfig config = RequestConfig.custom().setProxy(proxy).build();

                HttpPost request = new HttpPost("http://m.fangxinbao.com/wx/repersentVote.html");
                request.setConfig(config);
                request.setHeader("Host", "m.fangxinbao.com");
                request.setHeader("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:48.0) Gecko/20100101 Firefox/48.1");
                request.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                request.setHeader("X-Requested-With", "XMLHttpRequest");

                List<NameValuePair> list = new ArrayList<NameValuePair>();
                list.add(new BasicNameValuePair("userId", "4721"));
                //url格式编码
                UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(list, "UTF-8");
                request.setEntity(uefEntity);

                System.out.println("Executing request " + request.getRequestLine() + " to " + target + " via " + proxy);


                CloseableHttpResponse response = httpclient.execute(target, request);
                try {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        System.out.println("response:" + EntityUtils.toString(entity, "UTF-8"));
                    }
//                System.out.println("response:" + EntityUtils.toString(entity, "UTF-8"));
                    System.out.println("----------------------------------------");
                    System.out.println(response.getStatusLine());
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        response.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    httpclient.close();
                    System.err.println("结束！！！");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
