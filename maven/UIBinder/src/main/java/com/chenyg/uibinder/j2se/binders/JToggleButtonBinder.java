package com.chenyg.uibinder.j2se.binders;

import com.chenyg.uibinder.AttrEnum;
import com.chenyg.uibinder.OnValueChangedListener;
import com.chenyg.uibinder.j2se.J2seBinder;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * JCheckBox,JRadioButton是JToggleButton的子类.
 * Created by ZhuiFeng on 2015/9/9.
 */
public class JToggleButtonBinder extends J2seBinder
{
    private JToggleButton jToggleButton;

    public JToggleButtonBinder(JToggleButton jToggleButton)
    {
        super(jToggleButton);
        this.jToggleButton = jToggleButton;
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
                    currentValue = jToggleButton.isSelected();
                }


            };
            mouseReleaseForValue();
        }
        removeAndAdd(changeListener);
    }

    @Override
    public void release()
    {
        jToggleButton.removeChangeListener(changeListener);
        jToggleButton = null;
    }

    /**
     * @param attrEnum
     * @param value
     */
    @Override
    public void set(AttrEnum attrEnum, Object value)
    {
        if (AttrEnum.ATTR_VALUE == attrEnum)
        {
            Boolean _bool = (Boolean) value;
            jToggleButton.setSelected(_bool);
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

    /**
     * @param attrEnum
     * @return Boolean
     */
    @Override
    public Object get(AttrEnum attrEnum)
    {
        if (AttrEnum.ATTR_VALUE == attrEnum)
        {
            return jToggleButton.isSelected();
        }
        return null;
    }


}
