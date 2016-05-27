package com.chenyg.uibinder;

/**
 * Created by ZhuiFeng on 2015/6/12.
 */
public class BinderSet {
    /**
     * 控件的接口方法绑定名
     */
    public String tiedFunName;

    /**
     * Binder属性名
     */
    public AttrEnum attrEnum;

    /**
     * 接口中对应的参数名称
     */
    public String paramName;
    /**
     * 值
     */
    public Object value;

    /**
     *
     * @param tiedFunName 控件的接口方法绑定名
     * @param paramName 接口中对应的参数名称
     * @param attrEnum Binder属性名
     * @param value 值
     */
    public BinderSet(String tiedFunName, String paramName, AttrEnum attrEnum, Object value) {
        this.tiedFunName = tiedFunName;
        this.attrEnum = attrEnum;
        this.paramName = paramName;
        this.value = value;
    }


}
