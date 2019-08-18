package com.innsbluck.navisidelauncher;

import android.content.Context;
import android.util.DisplayMetrics;

public class DpUtil {
    /**
     * dpからpixelへの変換
     *
     * @param dp
     * @param context
     * @return float pixel
     */
    public static float convertDp2Px(float dp, Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return dp * metrics.density;
    }

    /**
     * pixelからdpへの変換
     *
     * @param px
     * @param context
     * @return float dp
     */
    public static float convertPx2Dp(int px, Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return px / metrics.density;
    }
}
