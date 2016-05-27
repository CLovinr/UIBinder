package com.chenyg.uibinder;

import com.chenyg.uibinder.base.ChooseCallback;
import com.chenyg.wporter.base.AppValues;


/**
 * Created by 宇宙之灵 on 2015/9/11.
 */
public abstract class BaseUI
{

    public interface InputCallback
    {
         void onOk(String text);

         void onCancel();

        public abstract class CallbackOK implements InputCallback
        {

            @Override
            public void onCancel()
            {

            }
        }
    }

    private static BaseUI baseUI;

    static void setBaseUI(BaseUI baseUI)
    {
        BaseUI.baseUI = baseUI;
    }

    public static  BaseUI getBaseUI()
    {
        return baseUI;
    }

    public abstract <T> BinderFactory<T> getBinderFactory(Class<T> t);

    public abstract void sendBinderData(String porterPrefix, BinderData binderData,boolean toAll);

    public abstract void alert(String ... contents);

    public abstract void alert(String title, String content, ChooseCallback chooseCallback);

    public abstract void toast(String content);

    /**
     * 弹出一个绑定
     */
    public abstract void popBinder();

    /**
     * 显示等待框，模态针对所有
     */
    public abstract void waitingShow(String ... contents);

    /**
     * 取消显示等待框
     */
    public abstract void waitingDisShow();

    /**
     * 用于绑定的
     *
     * @param title 标题
     * @param c2LView 显示的对象
     * @param callbackWithInit 初始
     * @param prefix 前缀
     * @param callbackValues 回调参数
     */
    public abstract void alert(String title, C2LView c2LView, ChooseCallback.CallbackWithInit callbackWithInit,
            Prefix prefix, AppValues callbackValues);

    /**
     * 输入
     * @param multiRow 是否多行
     * @param maxLength 最大长度
     * @param title 标题
     * @param initTxt 初始文本
     * @param inputCallback 回调函数
     */
    public abstract void input(boolean multiRow, int maxLength, String title, String initTxt,
            InputCallback inputCallback);
}
