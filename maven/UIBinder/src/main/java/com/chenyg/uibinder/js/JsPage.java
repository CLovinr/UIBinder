package com.chenyg.uibinder.js;

import com.chenyg.uibinder.BaseUI;
import com.chenyg.uibinder.Prefix;
import com.chenyg.uibinder.UIAttrGetter;
import com.chenyg.uibinder.UISeeker;
import com.chenyg.uibinder.base.BinderPorter;
import com.chenyg.wporter.a.app.AppPorterUtil;
import com.chenyg.wporter.base.SimpleAppValues;

import java.util.Map;

/**
 * 用于绑定的入口类。
 * Created by 宇宙之灵 on 2016/4/28.
 */
public class JsPage
{
    private UISeeker uiSeeker;
    private BaseUI baseUI;

    public JsPage(Map<String, JsBinder> binderMap, BaseUI baseUI,UIAttrGetter uiAttrGetter, Prefix prefix, String url)
    {
        this.baseUI = baseUI;
        JsUIProvider jsUIProvider = new JsUIProvider(binderMap, prefix,uiAttrGetter);
        uiSeeker = new UISeeker(null);
        uiSeeker.push(jsUIProvider);
        if (prefix.bindCallbackMethod != null)
        {
            SimpleAppValues simpleAppValues = new SimpleAppValues(BinderPorter.CALLBACK_VIEW,
                    BinderPorter.CALLBACK_PORTER_PREFIX, BinderPorter.CALLBACK_BINDER_DATA_SENDER)
                    .values(url, prefix.porterPrefix, uiSeeker);
            AppPorterUtil.getPorterObject(prefix.porterPrefix, prefix.bindCallbackMethod, simpleAppValues);
        }
    }

    public BaseUI getBaseUI()
    {
        return baseUI;
    }

    public void release()
    {
        uiSeeker.clear();
        uiSeeker = null;
    }
}
