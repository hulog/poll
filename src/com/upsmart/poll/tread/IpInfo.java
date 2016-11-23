package com.upsmart.poll.tread;

/**
 * Created by norman on 16-11-20.
 */
public class IpInfo {
    private String host;
    private int port;

    public IpInfo(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IpInfo ipInfo = (IpInfo) o;

        if (port != ipInfo.port) return false;
        return host.equals(ipInfo.host);

    }

    @Override
    public int hashCode() {
        int result = host.hashCode();
        result = 31 * result + port;
        return result;
    }
}
