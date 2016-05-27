package com.chenyg.uibinder.android;

import android.app.AlertDialog;
import com.chenyg.wporter.WebPorter;
import com.chenyg.wporter.annotation.ChildIn;

import java.util.Stack;

/**
 * Created by ZhuiFeng on 2015/6/14.
 */
 class BinderWebPorter extends WebPorter {
    private Stack<AlertDialog> dialogStack = new Stack<AlertDialog>();


    /**
     * push一个Dialog
     * @param alertDialog
     */
    protected void pushAlertDialog(AlertDialog alertDialog) {
        dialogStack.push(alertDialog);
    }



    /**
     * 取消对话框
     */
    @ChildIn(tiedName = "dissmisDialog")
    public void dissmisDialog() {
        dismissPopDialog();
    }

    /**
     * BinderActivity.popBinder();
     */
    @ChildIn(tiedName = "cancel")
    public void cancel(){
        BinderActivity.popBinder();
    }

    /**
     * 取消显示栈顶dialog
     */
    protected void dismissPopDialog() {
        if(!dialogStack.empty()){
            dialogStack.pop().dismiss();
        }

    }
}
