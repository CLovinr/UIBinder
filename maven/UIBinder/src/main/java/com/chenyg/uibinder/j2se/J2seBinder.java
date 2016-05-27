package com.chenyg.uibinder.j2se;

import com.chenyg.uibinder.AttrEnum;
import com.chenyg.uibinder.BinderExample;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Method;

/**
 * Created by ZhuiFeng on 2015/9/9.
 */
public abstract class J2seBinder extends BinderExample<JComponent>
{

    private MouseListener mouseListener;

    public J2seBinder(JComponent jComponent)
    {
        super(jComponent);
    }

    protected void remove(ChangeListener changeListener)
    {
        if (changeListener != null)
        {
            try
            {
                Method method = viewType.getClass().getMethod("removeChangeListener", ChangeListener.class);
                method.invoke(viewType, changeListener);

            } catch (Exception e)
            {
            }
        }

    }

    /**
     * 先移除再添加
     *
     * @param changeListener
     */
    protected void removeAndAdd(ChangeListener changeListener)
    {
        remove(changeListener);
        if (changeListener != null)
        {
            try
            {
                Method method = viewType.getClass().getMethod("addChangeListener", ChangeListener.class);
                method.invoke(viewType, changeListener);
            } catch (Exception e)
            {
            }
        }
    }


    protected void mouseReleaseForValue()
    {
        if (mouseListener == null)
        {
            mouseListener = new MouseInputAdapter()
            {
                @Override
                public void mouseReleased(MouseEvent e)
                {
                    if (currentValue != null && e.getButton() == MouseEvent.BUTTON1)
                    {
                        doOnchange();
                    }
                }
            };
        }
        viewType.removeMouseListener(mouseListener);
        viewType.addMouseListener(mouseListener);
    }

    @Override
    public void release()
    {
        viewType = null;
        viewType.removeMouseListener(mouseListener);
        mouseListener = null;
    }


    @Override
    public void set(AttrEnum attrEnum, Object value)
    {
        if (AttrEnum.ATTR_ENABLE == attrEnum)
        {
            Boolean enable = (Boolean) value;
            viewType.setEnabled(enable);
        } else if (AttrEnum.ATTR_FOCUS_REQUEST == attrEnum)
        {
            viewType.requestFocus();
        } else if (AttrEnum.ATTR_VISIBLE == attrEnum)
        {
            Boolean visible = (Boolean) value;
            viewType.setVisible(visible);
        }
    }
}
