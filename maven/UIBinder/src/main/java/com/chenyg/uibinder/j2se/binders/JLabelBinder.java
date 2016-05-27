package com.chenyg.uibinder.j2se.binders;

import com.chenyg.uibinder.AttrEnum;
import com.chenyg.uibinder.j2se.J2seBinder;

import javax.swing.*;

/**
 * Created by ZhuiFeng on 2015/9/9.
 */
public class JLabelBinder extends J2seBinder
{
    private JLabel jLabel;

    public JLabelBinder(JLabel jLabel)
    {
        super(jLabel);
        this.jLabel = jLabel;
    }

    @Override
    public void release()
    {
        jLabel = null;
    }

    @Override
    public void set(AttrEnum attrEnum, Object value)
    {
        if (AttrEnum.ATTR_VALUE == attrEnum)
        {
            jLabel.setText(value == null ? "" : value + "");
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
            return jLabel.getText();
        }
        return null;
    }


}
