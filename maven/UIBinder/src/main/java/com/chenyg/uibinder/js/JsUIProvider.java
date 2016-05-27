package com.chenyg.uibinder.js;

import com.chenyg.uibinder.*;
import com.chenyg.wporter.util.WPTool;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by 宇宙之灵 on 2016/4/28.
 */
public class JsUIProvider extends UIProvider
{

    private HashMap<UiId, Binder> hashMap = new HashMap<UiId, Binder>();
    private UIAttrGetter uiAttrGetter;

    /**
     * @param idBridgeMap
     * @param prefix      接口参数
     */
    public JsUIProvider(Map<String, JsBinder> idBridgeMap, Prefix prefix, UIAttrGetter uiAttrGetter)
    {
        super(prefix);
        this.uiAttrGetter = uiAttrGetter;
        Iterator<String> ids = idBridgeMap.keySet().iterator();

        while (ids.hasNext())
        {
            String idStr = ids.next();
            if (WPTool.isEmpty(idStr) || !idStr.startsWith(getPrefix().idPrefix))
            {
                continue;
            }
            UiId uiId = new UiId(idStr, getPrefix().idPrefix);
            JsBinder binder = idBridgeMap.get(idStr);
            if (binder != null)
            {
                hashMap.put(uiId, binder);
            }
        }

    }

    @Override
    public UIAttrGetter getUIAttrGetter()
    {
        return uiAttrGetter;
    }

    @Override
    public Set<UiId> getUIs()
    {
        return hashMap.keySet();
    }

    @Override
    public Binder getBinder(UiId uiId)
    {
        return hashMap.get(uiId);
    }

}
