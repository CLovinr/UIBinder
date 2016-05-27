package com.chenyg.uibinder.j2se.binders;

import com.chenyg.uibinder.AttrEnum;
import com.chenyg.uibinder.OnValueChangedListener;
import com.chenyg.uibinder.j2se.J2seBinder;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * Created by ZhuiFeng on 2015/9/9.
 */
public class JProgressBarBinder extends J2seBinder
{
    private JProgressBar jProgressBar;

    public JProgressBarBinder(JProgressBar jProgressBar)
    {
        super(jProgressBar);
        this.jProgressBar = jProgressBar;
    }

    private ChangeListener changeListener;

    private void setOnValueChangedListener(
            final OnValueChangedListener onValueChangedListener)
    {
        this.onValueChangedListener = onValueChangedListener;
        if (changeListener == null)
        {
            changeListener = new ChangeListener()
            {
                @Override
                public void stateChanged(ChangeEvent changeEvent)
                {
                    currentValue = jProgressBar.getValue();
                    doOnchange();
                }

            };

        }
        removeAndAdd(changeListener);
    }

    @Override
    public void release()
    {
        jProgressBar.removeChangeListener(changeListener);
        jProgressBar = null;
    }

    @Override
    public void set(AttrEnum attrEnum, Object value)
    {
        if (AttrEnum.ATTR_VALUE == attrEnum)
        {
            Integer progress = (Integer) value;
            jProgressBar.setValue(progress);
        } else if (AttrEnum.ATTR_VALUE_CHANGE_LISTENER == attrEnum
                && value != null
                && (value instanceof OnValueChangedListener))
        {
            OnValueChangedListener
                    onValueChangedListener =
                    (OnValueChangedListener) value;
            setOnValueChangedListener(onValueChangedListener);
        } else
        {
            super.set(attrEnum, value);
        }
    }

    @Override
    public Object get(AttrEnum attrEnum)
    {
        if (AttrEnum.ATTR_VALUE == attrEnum)
        {
            return jProgressBar.getValue();
        }
        return null;
    }


}
