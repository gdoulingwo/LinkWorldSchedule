package com.zhy.http.okhttp.callback;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import okhttp3.Response;

/**
 * @author zhy
 * @date 15/12/14
 */
public abstract class BitmapCallback extends Callback<Bitmap> {
    @Override
    public Bitmap parseNetworkResponse(Response response, int id) throws Exception {
        String cookieString = response.header("Set-Cookie", null);
        cookie(cookieString != null ? cookieString.replace("; path=/", "") : null);
        return BitmapFactory.decodeStream(response.body().byteStream());
    }

    /**
     * 返回Cookie
     *
     * @param cookie cookie
     */
    public abstract void cookie(String cookie);

}


