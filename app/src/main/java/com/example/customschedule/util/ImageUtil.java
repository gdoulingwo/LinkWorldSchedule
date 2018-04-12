package com.example.customschedule.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * @author wangyu
 * @date 18-3-27
 * @describe TODO
 */

public class ImageUtil {
    public static void loadImageView(Context mContext, String path, ImageView mImageView) {
        Glide.with(mContext).load(path).into(mImageView);
    }

}
