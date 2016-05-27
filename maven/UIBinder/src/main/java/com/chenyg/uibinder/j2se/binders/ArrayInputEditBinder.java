package com.chenyg.uibinder.j2se.binders;

import com.chenyg.uibinder.AttrEnum;
import com.chenyg.uibinder.OnValueChangedListener;
import com.chenyg.uibinder.j2se.J2seBinder;
import com.chenyg.uibinder.j2se.form.ArrayInputEdit;
import org.json.JSONArray;

/**
 * Created by 宇宙之灵 on 2015/9/18.
 */
public class ArrayInputEditBinder extends J2seBinder
{
    private ArrayInputEdit arrayInputEdit;
    private ArrayInputEdit.OnChangeListener onChangeListener;

    public ArrayInputEditBinder(ArrayInputEdit arrayInputEdit)
    {
        super(arrayInputEdit);
        this.arrayInputEdit = arrayInputEdit;
    }


    @Override
    public void release()
    {
        super.release();
        arrayInputEdit.setOnChangeListener(null);
        arrayInputEdit = null;
    }

    private void setOnValueChangedListener(final OnValueChangedListener listener)
    {
        super.onValueChangedListener=listener;
        if (onChangeListener == null)
        {
            onChangeListener = new ArrayInputEdit.OnChangeListener()
            {
                @Override
                public void onChange(JSONArray jsonArray)
                {
                    currentValue = jsonArray;
                    doOnchange();
                }
            };

        }
        arrayInputEdit.setOnChangeListener(onChangeListener);
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
            JSONArray jsonArray = (JSONArray) value;
            arrayInputEdit.setValue(jsonArray);
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
     * @return spinner.getSelectedItem
     */
    @Override
    public Object get(AttrEnum attrEnum)
    {
        if (AttrEnum.ATTR_VALUE == attrEnum)
        {
            return arrayInputEdit.getValue();
        }
        return null;
    }


}
