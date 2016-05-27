package com.chenyg.uibinder.j2se;

import com.chenyg.uibinder.*;
import com.chenyg.uibinder.Prefix;
import com.chenyg.wporter.util.WPTool;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by ZhuiFeng on 2015/9/9.
 */
public class J2seUIProvider extends UIProvider
{
    private HashMap<UiId, Binder> hashMap = new HashMap<UiId, Binder>();

    /**
     * @param prefix    接口参数
     * @param container awt容器
     */
    public J2seUIProvider(Prefix prefix,
            Container container)
    {
        super(prefix);
        search(container);
    }

    private void search(Container container)
    {
        Component[] components = container.getComponents();
        for (Component c : components)
        {
            if (c instanceof JComponent)
            {
                JComponent jc = (JComponent) c;
                if (_check(jc))
                {
                    search(jc);
                }
            } else if (c instanceof Container)
            {
                search((Container) c);
            }
        }
    }

    private boolean _check(JComponent jComponent)
    {
        String idStr = jComponent.getName();
        if (WPTool.isEmpty(idStr) || !idStr.startsWith(getPrefix().idPrefix))
        {
            return true;
        }
        UiId uiId = new UiId(idStr,getPrefix().idPrefix);
        Binder binder = BaseUI.
                getBaseUI().getBinderFactory(JComponent.class).getBinder(jComponent);
        if (binder != null)
        {
            hashMap.put(uiId, binder);
            return false;
        } else
        {
            return true;
        }
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
