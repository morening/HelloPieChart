package com.morening.hello.piechart;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by morening on 2017/12/28.
 */

final public class Utils {

    public static int dp2px(Context context, int dpValue){
        if (context == null){
            return 0;
        }
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

    public static int sp2px(Context context, int spValue){
        if (context == null){
            return 0;
        }
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, context.getResources().getDisplayMetrics());
    }
}
