package com.chenyg.uibinder.android.binders;

import android.view.View;
import android.widget.RatingBar;
import com.chenyg.uibinder.AttrEnum;
import com.chenyg.uibinder.OnValueChangedListener;
import com.chenyg.uibinder.android.AndroidBinder;

/**
 * Created by ZhuiFeng on 2015/6/12.
 */
public class RatingBarBinder extends AndroidBinder
{
    private RatingBar ratingBar;

    public RatingBarBinder(RatingBar ratingBar)
    {
        super(ratingBar);
        this.ratingBar = ratingBar;
    }

    private void setOnValueChangedListener(final OnValueChangedListener onValueChangedListener)
    {
        this.onValueChangedListener = onValueChangedListener;
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener()
        {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser)
            {
                currentValue = ratingBar.getProgress();
            }
        });
        ratingBar.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                doOnchange();
//                if (currentValue != null) {
//                    Integer value = currentValue;
//                    currentValue = null;
//                    doOnchange(tiedFun1,value);
//                    doOnchange(tiedFun2,value);
//                }
            }

//            private void doOnchange(String tiedFun,Object value){
//                if (tiedFun!=null){
//                    onValueChangedListener.onChanged(prefix,tiedFun,varName,value);
//                }
//            }
        });
    }

    @Override
    public void release()
    {
        super.release();
        ratingBar = null;
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
            Integer _rate = (Integer) value;
            ratingBar.setProgress(_rate);
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

    /**
     * @param attrEnum
     * @return Integer
     */
    @Override
    public Object get(AttrEnum attrEnum)
    {
        if (AttrEnum.ATTR_VALUE == attrEnum)
        {
            return ratingBar.getProgress();
        }
        return super.get(attrEnum);
    }

}
