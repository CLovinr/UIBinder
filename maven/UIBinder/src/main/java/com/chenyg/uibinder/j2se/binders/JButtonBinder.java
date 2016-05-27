package com.chenyg.uibinder.j2se.binders;

import com.chenyg.uibinder.AttrEnum;
import com.chenyg.uibinder.j2se.J2seBinder;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by ZhuiFeng on 2015/9/9.
 */
public class JButtonBinder extends J2seBinder
{
    private JButton jButton;
    private ActionListener actionListener;

    public JButtonBinder(JButton jButton)
    {
        super(jButton);
        this.jButton = jButton;
        actionListener = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
               onOccur();
            }
        };
        jButton.addActionListener(actionListener);
    }

    @Override
    public void release()
    {
        super.release();
        jButton.removeActionListener(actionListener);
        jButton = null;
    }

    @Override
    public void set(AttrEnum attrEnum, Object value)
    {
        if (AttrEnum.ATTR_VALUE == attrEnum)
        {
            jButton.setText(value.toString());
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
            return jButton.getText();
        }
        return null;
    }

}
