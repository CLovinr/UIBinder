package com.chenyg.uibinder.android;

/**
 * Created by ZhuiFeng on 2015/7/8.
 */
public enum BinderType {
    /**
     * push当前的，然后显示栈顶view
     */
    PUSH_SET,
    /**
     * 先pop(弹出后，不会显示栈顶view)，然后push，并显示栈顶view
     */
    POP_PUSH_SET,


    /**
     * pop一次,然后显示栈顶view
     */
    POP
}
