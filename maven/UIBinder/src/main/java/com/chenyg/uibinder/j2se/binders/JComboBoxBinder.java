package com.chenyg.uibinder.j2se.binders;

import com.chenyg.uibinder.AttrEnum;
import com.chenyg.uibinder.OnValueChangedListener;
import com.chenyg.uibinder.j2se.J2seBinder;
import org.json.JSONArray;
import org.json.JSONException;

import javax.swing.*;
import java.awt.event.*;

/**
 * 值是索引!
 * Created by 宇宙之灵 on 2015/9/11.
 */
public class JComboBoxBinder extends J2seBinder
{
    private JComboBox jComboBox;

    public JComboBoxBinder(JComboBox jComboBox)
    {
        super(jComboBox);
        this.jComboBox = jComboBox;
        lastIndex = jComboBox.getSelectedIndex();
    }

    private int lastIndex;
    private ItemListener itemListener;

    private void setOnValueChangedListener(
            final OnValueChangedListener onValueChangedListener)
    {
        this.onValueChangedListener = onValueChangedListener;
        if (itemListener == null)
        {

            itemListener = new ItemListener()
            {
                @Override
                public void itemStateChanged(ItemEvent e)
                {
                    int index = jComboBox.getSelectedIndex();
                    if (index != lastIndex)
                    {
                        currentValue = index;
                        lastIndex = index;
                        doOnchange();
                    }
                }
            };

        }

        jComboBox.removeItemListener(itemListener);
        jComboBox.addItemListener(itemListener);
    }

    @Override
    public void release()
    {
        super.release();
        jComboBox.removeItemListener(itemListener);
        jComboBox = null;
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
            Integer index = (Integer) value;
            jComboBox.setSelectedIndex(index);
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
            if (value != null)
            {
                if (value instanceof String[])
                {
                    DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>((String[]) value);
                    jComboBox.setModel(comboBoxModel);
                } else if (value instanceof JSONArray)
                {
                    JSONArray arr = (JSONArray) value;
                    String[] strs = new String[arr.length()];

                    try
                    {
                        for (int i = 0; i < strs.length; i++)
                        {
                            strs[i] = arr.getString(i);
                        }
                        DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>(strs);
                        jComboBox.setModel(comboBoxModel);
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }


                } else if (value instanceof ComboBoxModel)
                {
                    ComboBoxModel comboBoxModel = (ComboBoxModel) value;
                    jComboBox.setModel(comboBoxModel);
                } else
                {
                    throw new RuntimeException("unknown value type " + value.getClass());
                }

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
            return jComboBox.getSelectedIndex();
        }
        return null;
    }

}
