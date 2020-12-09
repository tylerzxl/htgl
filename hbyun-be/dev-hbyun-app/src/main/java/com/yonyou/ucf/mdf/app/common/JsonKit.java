package com.yonyou.ucf.mdf.app.common;


import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class JsonKit implements ExclusionStrategy {

    String[] keys;

    public JsonKit(String[] keys) {
        this.keys = keys;
    }

    @Override
    public boolean shouldSkipClass(Class<?> arg0) {
        return false;
    }

    @Override
    public boolean shouldSkipField(FieldAttributes arg0) {
        String[] var2 = this.keys;
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            String key = var2[var4];
            if (key.equals(arg0.getName())) {
                return true;
            }
        }

        return false;
    }

}

