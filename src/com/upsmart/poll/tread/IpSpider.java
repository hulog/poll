package com.upsmart.poll.tread;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

/**
 * Created by norman on 16-11-20.
 */
public class IpSpider extends TimerTask {

    private BlockingQueue<IpInfo> ipInfoQueue;
    private List<IpInfo> historyIpLists;

    public IpSpider(BlockingQueue<IpInfo> ipInfoQueue) {
        this.ipInfoQueue = ipInfoQueue;
        this.historyIpLists = new ArrayList<IpInfo>();
    }

    private String url;

    //获取ip地址信息
    public void getXiCiDaiLiIpListFromUrl(int num) {
        String url = "http://www.xicidaili.com/nn/" + num;
        String host = "www.xicidaili.com";
        Document doc = getDocumentByUrl(url, host);
        parseXiCiDaiLiIpLists(doc);
    }

    private void getKuaiDaiLiIpListFromUrl(int num) {
        String url = "http://www.kuaidaili.com/free/inha/" + num + "/";
        String host = "www.kuaidaili.com";
        Document doc = getDocumentByUrl(url, host);
        parseKuaiDaiLiIpLists(doc);
    }

    private void getZDaYeIpListFromUrl() {
        String url = "http://ip.zdaye.com/";
        String host = "ip.zdaye.com";
        Document doc = getDocumentByUrl(url, host);
        if (doc != null) {
            parseZDaYeLiIpLists(doc);
        } else {
            System.out.println("zdy has some error");
        }
    }

    private void parseZDaYeLiIpLists(Document doc) {
        String ipAddress = null;
        String ipTemp = null;
        IpInfo ipInfo = null;
        int port = 0;
        int[] ports = {8080, 80, 3128, 8118, 8998, 9999, 808, 8123, 8888};

        Elements eleLists = doc.getElementsByTag("tbody");
        Element tbody = eleLists.get(0);
        Elements trLists = tbody.children();
        Element e = null;
        Elements eChildrens = null;
        for (int i = 1; i < trLists.size(); i++) {
            e = trLists.get(i);
            eChildrens = e.children();
            ipTemp = eChildrens.get(0).text();
            ipAddress = ipTemp.substring(0, ipTemp.length() - 5);
//            port = Integer.parseInt(eChildrens.get(1).text());
            for (int j = 0; j < ports.length; j++) {
                port = ports[j];
                ipInfo = new IpInfo(ipAddress, port);
                putIpInfo2Queue(ipInfo);
            }
        }

    }

    private void parseKuaiDaiLiIpLists(Document doc) {
        String ipAddress = null;
        IpInfo ipInfo = null;
        int port = 0;

        Elements eleLists = doc.getElementsByTag("tbody");
        Element tbody = eleLists.get(0);
        Elements trLists = tbody.children();
        Element e = null;
        Elements eChildrens = null;
        for (int i = 0; i < trLists.size(); i++) {
            e = trLists.get(i);
            eChildrens = e.children();
            ipAddress = eChildrens.get(0).text();
            port = Integer.parseInt(eChildrens.get(1).text());
            ipInfo = new IpInfo(ipAddress, port);
            putIpInfo2Queue(ipInfo);
        }
    }

    private void parseXiCiDaiLiIpLists(Document doc) {
        String ipAddress = null;
        IpInfo ipInfo = null;
        int port = 0;

        Elements eleLists = doc.getElementsByTag("tbody");
        Element tbody = eleLists.get(0);
        Elements trLists = tbody.children();
        Element e = null;
        Elements eChildrens = null;
        for (int i = 0; i < trLists.size(); i++) {
            if (i == 0)
                continue;
            e = trLists.get(i);
            eChildrens = e.children();
            ipAddress = eChildrens.get(1).text();
            port = Integer.parseInt(eChildrens.get(2).text());
            ipInfo = new IpInfo(ipAddress, port);
            putIpInfo2Queue(ipInfo);
        }
    }

    private boolean putIpInfo2Queue(IpInfo ipInfo) {
        //TODO 可以用Set实现
        if (!historyIpLists.contains(ipInfo)) {
//            System.out.println("IP=====>>>>" + ipInfo.getHost() + ":" + ipInfo.getPort());
            try {
                ipInfoQueue.put(ipInfo);
//                System.out.println("Queue's size is "+ ipInfoQueue.size());
            } catch (InterruptedException e) {
                System.out.println("ipinfo放入阻塞队列出错=====>>>>>" + ipInfo.getHost() + ":" + ipInfo.getPort());
            }
            historyIpLists.add(ipInfo);
            return true;
        }
        return false;
    }


    @Override
    public void run() {
        System.out.println("Ip spider is running....");
        for (int i = 1; i < 21; i++) {
            if (i % 3 == 0) {
                getXiCiDaiLiIpListFromUrl(i / 3);
                getKuaiDaiLiIpListFromUrl(i / 3);
                try {
                    Thread.sleep(2 * 60 * 1000);
//                    Thread.sleep(20 * 1000);
                } catch (InterruptedException e) {
                    System.out.println("防止爬取ip 过快 ，让你睡觉15min，睡出异常了。。。。。。");
                }
            }
            getZDaYeIpListFromUrl();
            try {
                Thread.sleep(2 * 1000);
            } catch (InterruptedException e) {
                System.out.println("防止爬取ip 过快 ，让你睡觉20s，睡出异常了。。。。。。");
            }
        }
    }


    private Document getDocumentByUrl(String url, String host) {
        Document doc = null;
        try {
            doc = Jsoup
                    .connect(url)
                    .header("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:48.0) Gecko/20100101 Firefox/48.0")
                    .header("Host", host)
                    .timeout(5000)
                    .get();
        } catch (IOException e) {
            System.out.println("爬取ip html出错....");
        }
        return doc;
    }
}
