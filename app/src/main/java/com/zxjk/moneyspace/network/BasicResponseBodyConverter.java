package com.zxjk.moneyspace.network;

import android.text.TextUtils;

import com.google.gson.TypeAdapter;
import com.zxjk.moneyspace.Constant;
import com.zxjk.moneyspace.network.rx.RxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

public class BasicResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private final TypeAdapter<T> adapter;

    BasicResponseBodyConverter(TypeAdapter<T> adapter) {
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        String json = value.string();
        try {
            JSONObject jsonObject = new JSONObject(json);
            String data = jsonObject.optString("data");
            String msg = jsonObject.optString("msg");
            int code = jsonObject.optInt("code", Constant.CODE_SUCCESS);
            if (code == Constant.CODE_UNLOGIN) {
                throw new IOException(msg, new RxException.DuplicateLoginExcepiton(msg));
            }
            if (code != Constant.CODE_SUCCESS && TextUtils.isEmpty(data)) {
                throw new IOException(msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            value.close();
        }
        return adapter.fromJson(json);
    }
}
