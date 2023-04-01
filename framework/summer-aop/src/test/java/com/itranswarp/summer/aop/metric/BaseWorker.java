package com.itranswarp.summer.aop.metric;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Metric("metricInvocationHandler")
public abstract class BaseWorker {

    @Metric("MD5")
    public String md5(String input) {
        return hash("MD5", input);
    }

    protected String hash(String name, String input) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance(name);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        md.update(input.getBytes(StandardCharsets.UTF_8));
        byte[] result = md.digest();
        return "0x" + HexFormat.of().formatHex(result);
    }
}
