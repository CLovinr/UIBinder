package com.chenyg.uibinder.android;

import android.view.View;
import com.chenyg.uibinder.*;
import com.chenyg.uibinder.Prefix;
import com.chenyg.wporter.util.WPTool;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by ZhuiFeng on 2015/6/11.
 */
public class AndroidUIProvider extends UIProvider
{

    private HashMap<UiId, Binder> hashMap = new HashMap<UiId, Binder>();


    /**
     * @param classR
     * @param view
     * @param prefix 接口参数
     * @throws Exception
     */
    public AndroidUIProvider(Class<?> classR, View view, Prefix prefix) throws Exception
    {
        super(prefix);
        Field[] fields = getIds(classR);
        for (int i = 0; i < fields.length; i++)
        {
            Field field = fields[i];
            String idStr = field.getName();
            if (WPTool.isEmpty(idStr)||!idStr.startsWith(getPrefix().idPrefix))
            {
                continue;
            }
            UiId uiId = new UiId(idStr, getPrefix().idPrefix);
            int id = field.getInt(null);
            Binder binder = getBinder(id, view);
            if (binder != null)
            {
                hashMap.put(uiId, binder);
            }
        }
    }

    private Binder getBinder(int id, View view)
    {
        View v = view.findViewById(id);
        if (v == null) return null;
        return BaseUI.getBaseUI().getBinderFactory(View.class).getBinder(v);
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


    private Field[] getIds(Class<?> classR)
    {
        try
        {
            Class<?> layoutClass = Class.forName(classR.getName() + "$id");
            Field[] fields = layoutClass.getDeclaredFields();
            return fields;
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

}
