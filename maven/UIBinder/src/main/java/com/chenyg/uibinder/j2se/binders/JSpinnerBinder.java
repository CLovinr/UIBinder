package com.chenyg.uibinder.j2se.binders;

import com.chenyg.uibinder.AttrEnum;
import com.chenyg.uibinder.OnValueChangedListener;
import com.chenyg.uibinder.j2se.J2seBinder;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * 得到的是控件的值
 * Created by ZhuiFeng on 2015/9/9.
 */
public class JSpinnerBinder extends J2seBinder
{
    private JSpinner jSpinner;

    public JSpinnerBinder(JSpinner jSpinner)
    {
        super(jSpinner);
        this.jSpinner = jSpinner;
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
                    currentValue = jSpinner.getValue();
                    doOnchange();
                }


            };
        }
        removeAndAdd(changeListener);
    }

    @Override
    public void release()
    {
        jSpinner.removeChangeListener(changeListener);
        jSpinner = null;
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
            jSpinner.setValue(value);
        } else if (AttrEnum.ATTR_VALUE_CHANGE_LISTENER == attrEnum
                && value != null
                && (value instanceof OnValueChangedListener))
        {
            OnValueChangedListener
                    onValueChangedListener =
                    (OnValueChangedListener) value;
            setOnValueChangedListener(onValueChangedListener);
        } else if (attrEnum == AttrEnum.ATTR_VALUE_OTHER)
        {
            if (value != null && value instanceof SpinnerModel)
            {
                SpinnerModel spinnerModel = (SpinnerModel) value;
                jSpinner.setModel(spinnerModel);
            }

        } else
        {
            super.set(attrEnum, value);
        }
    }

    /**
     * @param attrEnum
     * @return spinner.getSelectedItem
     */
    @Override
    public Object get(AttrEnum attrEnum)
    {
        if (AttrEnum.ATTR_VALUE == attrEnum)
        {
            return jSpinner.getValue();
        }
        return null;
    }


}
