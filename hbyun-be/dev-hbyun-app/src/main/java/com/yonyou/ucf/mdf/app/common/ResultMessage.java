package com.yonyou.ucf.mdf.app.common;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.yonyou.ucf.mdd.common.utils.json.GsonHelper;

import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

public class ResultMessage {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String[] JSON_IGNORE_PROPERTIES = new String[]{"cPassword", "cSalt"};
    private int code;
    private String message;
    private Object data;

    private ResultMessage(int code, String message, Object data) {
        if (message != null) {
            message = message.replaceFirst("^.*Exception:\\s*", "");
        }

        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static ResultMessage getIns(Object data) {
        return new ResultMessage(200, "操作成功", data);
    }

    public static String data(Object data, String[] keys) {
        return getJsonResult(200, "操作成功", data, keys);
    }

    public static String data(Object data, boolean isHoldNull) {
        return isHoldNull ? holdNulldata(data) : data(data, null);
    }

    public static String data(Object data) {
        return data(data, null);
    }

    private static String holdNulldata(Object data) {
        ResultMessage result = new ResultMessage(200, "操作成功", data);
        return GsonHelper.ToJSon(result, "yyyy-MM-dd HH:mm:ss");
    }

    public static String toMap(String data) {
        return data(GsonHelper.FromJSon(data, Map.class), null);
    }

    public static String toMap(String data, boolean isHoldNull) {
        return data(GsonHelper.FromJSon(data, Map.class), isHoldNull);
    }

    public static String formatDate(Object data) {
        return formatDate(data, "yyyy-MM-dd HH:mm:ss");
    }

    public static String formatDate(Object data, String dateFormat) {
        return formatDate(data, dateFormat, null);
    }

    public static String formatDate(Object data, String[] keys) {
        return formatDate(data, "yyyy-MM-dd HH:mm:ss", keys);
    }

    public static String formatDate(Object data, String dateFormate, String[] keys) {
        return getJsonResult(200, "操作成功", data, keys);
    }

    public static String success() {
        return data(null);
    }

    public static String success(String message) {
        return getJsonResult(200, message, null, null);
    }

    public static String error(String message) {
        return getJsonResult(999, message, null, null);
    }

    public static String NoAuth(String message) {
        return getJsonResult(901, message, null, null);
    }

    public static String NoBind(String message, Object data) {
        return getJsonResult(902, message, data, null);
    }

    public static String invalidparameters() {
        return getJsonResult(998, "无效的参数或非法的请求！", null, null);
    }

    public static String NoLogin() {
        return NoLogin("请登录后操作!");
    }

    public static String NoLogin(String message) {
        return getJsonResult(900, message, null, null);
    }

    private static String getJsonResult(int code, String message, Object data, String[] keys) {
        ResultMessage result = new ResultMessage(code, message, data);
        return getJsonResult(result, keys);
    }

    private static String getJsonResult(ResultMessage result, String[] keys) {
        if (keys != null && keys.length != 0) {
            int len = keys.length;
            keys = (String[]) Arrays.copyOf(keys, len + 2);
            keys[len] = "cPassword";
            keys[len + 1] = "cSalt";
        } else {
            keys = JSON_IGNORE_PROPERTIES;
        }

        GsonBuilder builder = new GsonBuilder();
        if (keys != null && keys.length > 0) {
            builder = builder.setExclusionStrategies(new ExclusionStrategy[]{new JsonKit(keys)});
        }

        builder.setDateFormat("yyyy-MM-dd HH:mm:ss");

        builder.registerTypeAdapter(Time.class, MddTimeTypeAdapter.FACTORY.create(null, TypeToken.get(Time.class)));
        Gson gson = builder.create();
        return gson.toJson(result);
    }

    static final class MddTimeTypeAdapter extends TypeAdapter<Time> {
        public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
            @SuppressWarnings("unchecked") // we use a runtime check to make sure the 'T's equal
            @Override
            public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
                return typeToken.getRawType() == Time.class ? (TypeAdapter<T>) new MddTimeTypeAdapter() : null;
            }
        };

        private final DateFormat format = new SimpleDateFormat("HH:mm:ss");

        @Override
        public synchronized Time read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            try {
                Date date = format.parse(in.nextString());
                return new Time(date.getTime());
            } catch (ParseException e) {
                throw new JsonSyntaxException(e);
            }
        }

        @Override
        public synchronized void write(JsonWriter out, Time value) throws IOException {
            out.value(value == null ? null : format.format(value));
        }

    }

}
