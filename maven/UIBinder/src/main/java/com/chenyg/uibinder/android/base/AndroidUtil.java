package com.chenyg.uibinder.android.base;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by ZhuiFeng on 2015/6/10.
 */
public class AndroidUtil {
    public static Button  getButton(View view,int id){
        return (Button)view.findViewById(id);
    }

    public static EditText getEditText(View view,int id){
        return (EditText)view.findViewById(id);
    }
}
