package com.shen.shengeunion.utils;

import android.widget.Toast;

import com.shen.shengeunion.base.BaseApplication;

/**
 * 弹框的工具类
 */
public class ToastUtils {

    private static Toast toast;

    public static void showToast(String tip) {
        if (toast == null) {
            toast = Toast.makeText(BaseApplication.getContext(), tip, Toast.LENGTH_SHORT);
        } else {
            toast.setText(tip);
        }
        toast.show();
    }
}
