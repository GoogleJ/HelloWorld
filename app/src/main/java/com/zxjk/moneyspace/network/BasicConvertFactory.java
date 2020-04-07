package com.zxjk.moneyspace.network;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * 处理response返回结果data为空串的转换器
 */
public class BasicConvertFactory extends Converter.Factory {

    private final Gson gson;

    static BasicConvertFactory create() {
        return create(new Gson());
    }

    private static BasicConvertFactory create(Gson gson) {
        return new BasicConvertFactory(gson);
    }

    private BasicConvertFactory(Gson gson) {
        this.gson = gson;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new BasicResponseBodyConverter<>(adapter);
    }
}
