package com.upsmart.poll.tread;

import java.util.Timer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by norman on 16-11-20.
 */
public class VoteApp {
    private BlockingQueue<IpInfo> ipInfoQueue;
    private IpSpider ipSpider;

    public VoteApp() {
        this.ipInfoQueue = new LinkedBlockingQueue<>();
        this.ipSpider = new IpSpider(ipInfoQueue);
    }

    public static void main(String[] args) {
        VoteApp va = new VoteApp();//yes
        va.vote();
    }

    public void vote() {

        Timer timer = new Timer();
        long delay = 0;
        long period = 1000 * 60 * 60 * 2;
        //每半小时采集ip 一次
        timer.scheduleAtFixedRate(ipSpider, delay, period);

        for (int i = 0; i < 100; i++) {
            new Thread(new VoteThread(this.ipInfoQueue), "线程" + (i + 1)).start();
        }
    }
}
