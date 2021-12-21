package com.chooongg.simple.api;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class SeniverseUtils {
    private final Map<String, String> paramMap = new HashMap<>();

    public void addParameter(String key, String value) {
        this.paramMap.put(key, value);
    }

    public void addParameter(String key, long value) {
        this.paramMap.put(key, String.valueOf(value));
    }

    public void removeParameter(String key) {
        this.paramMap.remove(key);
    }

    public String getUrl(String baseUrl, String secretKey) throws NoSuchAlgorithmException, InvalidKeyException {
        Set<String> paramSet = this.paramMap.keySet();

        List<String> paramList = new ArrayList<>(paramSet);

        Collections.sort(paramList);
        boolean isFirst = true;
        StringBuilder stringBuilder = new StringBuilder();

        for (String key : paramList) {
            if (isFirst) {
                isFirst = false;
            } else {
                stringBuilder.append("&");
            }
            stringBuilder.append(key);
            stringBuilder.append("=");
            stringBuilder.append(this.paramMap.get(key));
        }

        String query = stringBuilder.toString();

        Mac hmac = Mac.getInstance("HmacSHA1");
        hmac.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
        String sig = Base64.encodeToString(hmac.doFinal(query.getBytes(StandardCharsets.UTF_8)), Base64.DEFAULT);
        stringBuilder.append("&sig=");
        stringBuilder.append(sig);

        return baseUrl.concat("?").concat(stringBuilder.toString());
    }
}