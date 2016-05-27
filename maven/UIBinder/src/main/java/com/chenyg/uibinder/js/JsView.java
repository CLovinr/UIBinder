package com.chenyg.uibinder.js;

import com.chenyg.uibinder.AttrEnum;

/**
 * Created by 宇宙之灵 on 2016/4/28.
 */
public class JsView
{
    private JsBridge jsBridge;

    public JsView(JsBridge jsBridge)
    {
        this.jsBridge = jsBridge;
    }

    public void setJsBridge(JsBridge jsBridge)
    {
        this.jsBridge = jsBridge;
    }

    public void set(AttrEnum attrEnum, Object value)
    {
        jsBridge.set(jsBridge.parseAttrName(attrEnum), value);
    }

    public Object get(AttrEnum attrEnum)
    {
        return jsBridge.get(jsBridge.parseAttrName(attrEnum));
    }


    public void release()
    {
        jsBridge.release();
    }
}
