package com.chenyg.uibinder.android.binders;

import android.widget.CompoundButton;
import com.chenyg.uibinder.AttrEnum;
import com.chenyg.uibinder.OnValueChangedListener;
import com.chenyg.uibinder.android.AndroidBinder;

/**
 * CheckBox, RadioButton, ToggleButton,Switch是CompoundButton的子类
 * Created by 宇宙之灵 on 2015/9/11.
 */
public class CompoundButtonBinder extends AndroidBinder{
    private CompoundButton compoundButton;

    public CompoundButtonBinder(CompoundButton compoundButton) {
        super(compoundButton);
        this.compoundButton = compoundButton;
    }

    private void setOnValueChangedListener(final OnValueChangedListener onValueChangedListener) {
        this.onValueChangedListener = onValueChangedListener;
        compoundButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                currentValue=isChecked;
                doOnchange();
//                doOnchange(tiedFun1,isChecked);
//                doOnchange(tiedFun2,isChecked);
            }

//            private void doOnchange(String tiedFun,boolean isChecked){
//                if (tiedFun!=null){
//                    onValueChangedListener.onChanged(prefix, tiedFun, varName, isChecked);
//                }
//            }
        });
    }

    @Override
    public void release() {
        super.release();
        compoundButton = null;
    }

    /**
     * @param attrEnum
     * @param value
     */
    @Override
    public void set(AttrEnum attrEnum, Object value) {
        if (AttrEnum.ATTR_VALUE == attrEnum) {
            Boolean _bool = (Boolean) value;
            compoundButton.setChecked(_bool);
        } else if (AttrEnum.ATTR_VALUE_CHANGE_LISTENER == attrEnum && value != null && (value instanceof OnValueChangedListener)) {
            OnValueChangedListener onValueChangedListener = (OnValueChangedListener) value;
            setOnValueChangedListener(onValueChangedListener);
        } else {
            super.set(attrEnum, value);
        }
    }

    /**
     * @param attrEnum
     * @return Boolean
     */
    @Override
    public Object get(AttrEnum attrEnum) {
        if (AttrEnum.ATTR_VALUE == attrEnum) {
            return compoundButton.isChecked();
        }
        return super.get(attrEnum);
    }


}
