package com.chenyg.uibinder.j2se.binders;

import com.chenyg.uibinder.AttrEnum;
import com.chenyg.uibinder.OnValueChangedListener;
import com.chenyg.uibinder.j2se.J2seBinder;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * Created by ZhuiFeng on 2015/9/9.
 */
public class JSliderBinder extends J2seBinder {
    private JSlider jSlider;

    public JSliderBinder(JSlider jSlider) {
        super(jSlider);
        this.jSlider = jSlider;
    }

    private ChangeListener changeListener;

    private void setOnValueChangedListener(
            final OnValueChangedListener onValueChangedListener) {
        this.onValueChangedListener = onValueChangedListener;
        if (changeListener == null) {
            changeListener = new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent changeEvent) {
                    currentValue = jSlider.getValue();
                }
            };


           mouseReleaseForValue();
        }
        removeAndAdd(changeListener);

    }

    @Override
    public void release() {
        jSlider.removeChangeListener(changeListener);
        jSlider = null;
    }

    @Override
    public void set(AttrEnum attrEnum, Object value) {
        if (AttrEnum.ATTR_VALUE == attrEnum) {
            Integer progress = (Integer) value;
            jSlider.setValue(progress);
        } else if (AttrEnum.ATTR_VALUE_CHANGE_LISTENER == attrEnum
                && value != null
                && (value instanceof OnValueChangedListener)) {
            OnValueChangedListener
                    onValueChangedListener =
                    (OnValueChangedListener) value;
            setOnValueChangedListener(onValueChangedListener);
        } else {
            super.set(attrEnum, value);
        }
    }

    @Override
    public Object get(AttrEnum attrEnum) {
        if (AttrEnum.ATTR_VALUE == attrEnum) {
            return jSlider.getValue();
        }
        return null;
    }

}
