package com.chenyg.uibinder.js;

import com.chenyg.uibinder.AttrEnum;
import com.chenyg.uibinder.BinderExample;

/**
 * Created by 宇宙之灵 on 2016/4/28.
 */
public  class JsBinder extends BinderExample<JsView>
{
    public JsBinder(JsView viewType)
    {
        super(viewType);
    }

    @Override
    public void set(AttrEnum attrEnum, Object value)
    {
        viewType.set(attrEnum, value);
    }

    @Override
    public Object get(AttrEnum attrEnum)
    {
        return viewType.get(attrEnum);
    }


    @Override
    public void release()
    {
        if (viewType != null)
        {
            viewType.release();
            viewType = null;
        }
    }
}
