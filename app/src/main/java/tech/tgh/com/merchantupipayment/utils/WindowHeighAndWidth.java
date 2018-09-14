package tech.tgh.com.merchantupipayment.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

public class WindowHeighAndWidth {

        public static int getScreenWidthInDPs(Context context) {

            DisplayMetrics dm = new DisplayMetrics();

            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(dm);
            int widthInDP = Math.round(dm.widthPixels / dm.density);
            return widthInDP;
        }

        // Custom method to get screen height in dp/dip using Context object
        public static int getScreenHeightInDPs(Context context) {
            DisplayMetrics dm = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(dm);
            int heightInDP = Math.round(dm.heightPixels / dm.density);
            return heightInDP;
        }
    public static int getScreenHeight(Context context){
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        int heightInPx = dm.heightPixels;
        return heightInPx;
    }
    public static int getScreenWidth(Context context){
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        int widthInPx = dm.widthPixels;
        return widthInPx;
    }
    public static int pxValueInDp(Context context,int valueInPx)
    {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = valueInPx / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        Log.d("pxValueInDp: ",String.valueOf(dp));
        return (int)dp;
    }
    public static int dpInPx(Context context,int valueInDp)
    {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = valueInDp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int)px;
    }

    public static int pxInSp(Context context,int valueInpx)
    {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float sp = valueInpx / metrics.scaledDensity;
        return (int)sp;
    }
    }

