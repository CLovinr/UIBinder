package com.chenyg.uibinder.android.binders;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import com.chenyg.uibinder.AttrEnum;
import com.chenyg.uibinder.OnValueChangedListener;
import com.chenyg.uibinder.android.AndroidBinder;

/**
 * Created by ZhuiFeng on 2015/6/11.
 */
public class EditTextBinder extends AndroidBinder
{

    private EditText editText;

    public EditTextBinder(EditText editText)
    {
        super(editText);
        this.editText = editText;
    }

    private void setOnValueChangedListener(final OnValueChangedListener onValueChangedListener)
    {
        editText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                currentValue = s.toString();
            }
        });
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                doOnchange();
            }

        });
    }

    @Override
    public void release()
    {
        super.release();
        editText = null;
    }

    @Override
    public void set(AttrEnum attrEnum, Object value)
    {
        if (AttrEnum.ATTR_VALUE == attrEnum)
        {
            editText.setText(value == null ? "" : value + "");
        } else if (AttrEnum.ATTR_VALUE_CHANGE_LISTENER == attrEnum && value != null && (value instanceof
                OnValueChangedListener))
        {
            OnValueChangedListener onValueChangedListener = (OnValueChangedListener) value;
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
            return editText.getText().toString();
        }
        return super.get(attrEnum);
    }


}
