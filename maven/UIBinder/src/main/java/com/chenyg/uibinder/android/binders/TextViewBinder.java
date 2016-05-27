package com.chenyg.uibinder.android.binders;

import android.widget.TextView;
import com.chenyg.uibinder.AttrEnum;
import com.chenyg.uibinder.android.AndroidBinder;

/**
 * Created by ZhuiFeng on 2015/6/13.
 */
public class TextViewBinder extends AndroidBinder
{
    private TextView textView;

    public TextViewBinder(TextView textView)
    {
        super(textView);
        this.textView = textView;
    }

//    private void setOnValueChangedListener(final OnValueChangedListener onValueChangedListener) {
//        this.onValueChangedListener = onValueChangedListener;
//        textView.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                doOnchange(tiedFun1, s.toString());
//                doOnchange(tiedFun2, s.toString());
//            }
//
//            private void doOnchange(String tiedFun, Object value) {
//                if (tiedFun != null) {
//                    onValueChangedListener.onChanged(prefix, tiedFun, varName, value);
//                }
//            }
//        });
//    }

    @Override
    public void release()
    {
        super.release();
        textView = null;
    }

    @Override
    public void set(AttrEnum attrEnum, Object value)
    {
        if (AttrEnum.ATTR_VALUE == attrEnum)
        {
            textView.setText(value == null ? "" : value + "");
        }
//        else if (AttrEnum.ATTR_VALUE_CHANGE_LISTENTER == attrEnum && value != null && (value instanceof
// OnValueChangedListener)) {
        //            OnValueChangedListener onValueChangedListener = (OnValueChangedListener) value;
        //            setOnValueChangedListener(onValueChangedListener);
        //        }
        else
        {
            super.set(attrEnum, value);
        }
    }

    @Override
    public Object get(AttrEnum attrEnum)
    {
        if (AttrEnum.ATTR_VALUE == attrEnum)
        {
            return textView.getText().toString();
        }
        return super.get(attrEnum);
    }


}
