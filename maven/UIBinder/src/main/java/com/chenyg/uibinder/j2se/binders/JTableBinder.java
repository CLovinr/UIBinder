package com.chenyg.uibinder.j2se.binders;

import com.chenyg.uibinder.AttrEnum;
import com.chenyg.uibinder.OnValueChangedListener;
import com.chenyg.uibinder.j2se.J2seBinder;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;

/**
 * Created by 宇宙之灵 on 2015/12/16.
 */
public class JTableBinder extends J2seBinder
{
    private JTable jTable;

    public JTableBinder(JTable jTable)
    {
        super(jTable);
        this.jTable = jTable;
    }

    private ListSelectionListener listSelectionListener;

    private void setOnValueChangedListener(
            final OnValueChangedListener onValueChangedListener)
    {
        this.onValueChangedListener = onValueChangedListener;
        if (listSelectionListener == null)
        {
            listSelectionListener = new ListSelectionListener()
            {
                @Override
                public void valueChanged(ListSelectionEvent e)
                {
                    currentValue = new int[]{e.getFirstIndex(), e.getLastIndex()};
                    doOnchange();
                }
            };

        }
        jTable.getSelectionModel().removeListSelectionListener(listSelectionListener);
        jTable.getSelectionModel().addListSelectionListener(listSelectionListener);
    }

    @Override
    public void release()
    {
        if (listSelectionListener != null)
        {
            jTable.getSelectionModel().removeListSelectionListener(listSelectionListener);
            ;
        }
        jTable = null;
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
            int[] selection = (int[]) value;
            jTable.setRowSelectionInterval(selection[0], selection[1]);
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
                if (value instanceof TableModel)
                {
                    TableModel tableModel = (TableModel) value;
                    jTable.setModel(tableModel);

                }
                /*else if(value instanceof String[]){
                    String[] names = (String[]) value;
                    jTable.setTableHeader(new JTableHeader(names));
                }*/
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
            return jTable.getSelectedRows();
        }
        return null;
    }

}
