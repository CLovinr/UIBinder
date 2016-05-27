package com.chenyg.uibinder.android;

import android.app.AlertDialog;
import android.view.View;
import com.chenyg.uibinder.C2LDialog;

/**
 * Created by 宇宙之灵 on 2015/9/11.
 */
public class AndroidC2LDialog extends C2LDialog {
    public View view;
    public AlertDialog alertDialog;
    public AndroidC2LDialog(View view,AlertDialog alertDialog){
        this.view=view;
        this.alertDialog=alertDialog;
    }
}
