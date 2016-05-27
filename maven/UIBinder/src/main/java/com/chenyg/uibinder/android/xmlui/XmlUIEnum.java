package com.chenyg.uibinder.android.xmlui;

import com.chenyg.uibinder.base.ChooseCallback;

/**
 * Created by ZhuiFeng on 2015/6/6.
 */
public enum XmlUIEnum {
    FTied("xmlui/"),
    /**
     * <pre>
     * maxLength:字符的最大长度
     * multiRow:是否是多行
     * inputType:输入类型{@linkplain XmlInputType}
     * title:弹出框的标题
     * inputCallback:回调函数{@linkplain com.chenyg.uibinder.BaseUI.InputCallback}
     * </pre>
     */
    input("input"),
    /**
     * <pre>
     *  1.contentType:0表示content是layout的id，1表示lcontent是ayout的名字，2表示content是要显示的内容,调用toString,3表示content为view;
     * 2.needConfirm:0表示不需要确认(使用的是Toast.makeText,其余的用的是AlertDialog)，1表示需要ok按钮，2表示需要ok和cancel按钮（1和如传递了chooseCallback,则在点击按钮后会调用相应回调函数.）,3表示没有ok和cancel钮，使用的是Alert,4表示只有cancel按钮。
     * 3.当needConfirm为0时，content必须为要显示的内容，调用toString。
     * 4.只有当needConfirm为1、2或3时，使用的是AlertDialog，chooseCallback才可能被使用;chooseCallback可为{@linkplain ChooseCallback}或{@linkplain ChooseCallback.CallbackWithInit}
     * 5.不应该屏蔽掉返回键，返回键已经作为取消动作。
     *  </pre>
     */
    alert("alert"),
    /**
     * <pre>
     *     以下两个选择一个：
     *     name:字符串对应的名称
     *     id：字符串对应的id
     *     返回结果为SUCCESS时，结果就为对应字符串
     * </pre>
     */
    getString("getString"),

    /**
     * <pre>
     * 根据布局id或布局名得到View,以下两个选择一个:
     * layoutId:布局id，如:R.layout.mylayout
     * layoutName:布局名称，如"mylayout"
     * 返回码为SUCCESS时，结果为View
     * </pre>
     */
    getView("getView");

    private final String value;

    XmlUIEnum(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }
}
