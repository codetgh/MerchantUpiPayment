package tech.tgh.com.merchantupipayment.utils;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;

public class CommonUtil {

    public static GradientDrawable setRoundedCorner(int bgColor,int stroke,int radius,int shape){
        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(radius);
        gd.setColor(bgColor);
        gd.setStroke(Constants.CORNER_RADIUS,stroke);
        gd.setShape(shape);
        return gd;
    }


}
