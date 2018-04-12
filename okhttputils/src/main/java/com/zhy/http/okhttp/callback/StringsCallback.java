package com.zhy.http.okhttp.callback;

import java.io.IOException;

import okhttp3.Response;

/**
 * 允许返回Cookie
 *
 * @author zhy
 * @date 15/12/14
 */
public abstract class StringsCallback extends Callback<String> {
    @Override
    public String parseNetworkResponse(Response response, int id) throws IOException {
        cookie(response.header("Set-Cookie", null).replace("; path=/", ""));
        return response.body().string();
    }

    /**
     * 返回Cookie
     *
     * @param cookie cookie
     */
    public abstract void cookie(String cookie);
}
