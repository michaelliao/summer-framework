package com.itranswarp.scan.proxy;

public class FirstProxyBean extends OriginBean {

    final OriginBean target;

    public FirstProxyBean(OriginBean target) {
        this.target = target;
    }

    @Override
    public void setVersion(String version) {
        target.setVersion(version);
    }

    @Override
    public String getName() {
        return target.getName();
    }

    @Override
    public String getVersion() {
        return target.getVersion();
    }
}
