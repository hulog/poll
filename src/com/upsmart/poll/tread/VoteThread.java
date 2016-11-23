package com.upsmart.poll.tread;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by norman on 16-11-20.
 */
public class VoteThread implements Runnable {

    private BlockingQueue<IpInfo> ipInfoQueue;
    private static int successCout;

    public VoteThread(BlockingQueue<IpInfo> ipInfoQueue) {
        successCout = 1;
        this.ipInfoQueue = ipInfoQueue;
    }

    @Override
    public void run() {

        IpInfo ipInfo = null;
        while (true) {
            try {
                ipInfo = ipInfoQueue.take();
                System.out.println("Queue's size is " + ipInfoQueue.size());
            } catch (InterruptedException e) {
                System.out.println("[" + Thread.currentThread().getName() + "]" + "execption : ipInfoQueue has occoured some problem...");
            }

            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpHost target = new HttpHost("m.fangxinbao.com/wx/repersentVote.html");
            HttpHost proxy = new HttpHost(ipInfo.getHost(), ipInfo.getPort(), "http");
            RequestConfig config = RequestConfig.custom().setProxy(proxy).build();

            HttpPost request = new HttpPost("http://m.fangxinbao.com/wx/repersentVote.html");
            request.setConfig(config);
            request.setHeader("Host", "m.fangxinbao.com");
            request.setHeader("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:48.0) Gecko/20100101 Firefox/48.0");
            request.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            request.setHeader("X-Requested-With", "XMLHttpRequest");

            List<NameValuePair> list = new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("userId", "4721"));
            //url格式编码
            try {
                UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(list, "UTF-8");
                request.setEntity(uefEntity);

                System.out.println("[" + Thread.currentThread().getName() + "]" + "Executing request " + request.getRequestLine() + " to " + target + " via " + proxy);

                CloseableHttpResponse response = httpclient.execute(target, request);
                try {
                    HttpEntity entity = response.getEntity();
                    String str = EntityUtils.toString(entity, "UTF-8");
                    String respStr = str.length() > 10 ? str.substring(0, 10) : str;
                    if (entity != null) {
                        System.out.println("[" + Thread.currentThread().getName() + "]" + "----------------------------------------");
                        Date date = new Date();
                        System.out.println(date.toString() + "  " + "[" + Thread.currentThread().getName() + "]" + "-->response:" + respStr);
                    }
                    if (respStr.trim().equals("2")) {
                        System.err.println("[" + Thread.currentThread().getName() + "] " + "已成功投票：" + successCout++);
                    }
                } catch (IOException e) {
                    System.out.println("[" + Thread.currentThread().getName() + "]" + "execption...1");
                } finally {
                    try {
                        response.close();
                    } catch (IOException e) {
                        System.out.println("[" + Thread.currentThread().getName() + "]" + "execption...2");
                    }
                }
            } catch (IOException e) {
                System.out.println("[" + Thread.currentThread().getName() + "]" + "execption...3");
            } finally {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    System.out.println("[" + Thread.currentThread().getName() + "]" + "execption...4");
                }
            }
        }

    }
}
