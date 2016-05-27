package com.chenyg.uibinder.js;

import com.chenyg.uibinder.AttrEnum;

/**
 * Created by 宇宙之灵 on 2016/4/28.
 */
public interface JsBridge
{
    void set(String name,Object value);
    Object get(String name);
    String parseAttrName(AttrEnum attrEnum);
    void release();
}
