package com.chenyg.uibinder.base;

import com.chenyg.uibinder.C2LDialog;

/**
 * Created by ZhuiFeng on 2015/6/10.
 */
public interface ChooseCallback {
    /**
     * 选择确定的回调函数
     */
     void onOk();

    /**
     * 选择取消的回调函数
     */
     void onCancel();



    public interface CallbackWithInit extends ChooseCallback {

        /**
         *
         * @param c2LDialog
         */
        public void onInit(C2LDialog c2LDialog);
    }

    public abstract   class CallbackOk implements ChooseCallback {


        @Override
        public void onCancel() {

        }


    }

    public abstract class CallbackInit implements CallbackWithInit{

        @Override
        public void onOk() {

        }

        @Override
        public void onCancel() {

        }
    }
}
