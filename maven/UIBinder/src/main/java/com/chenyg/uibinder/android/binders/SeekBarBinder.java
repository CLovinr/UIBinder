package com.chenyg.uibinder.android.binders;


import android.view.View;
import android.widget.SeekBar;
import com.chenyg.uibinder.AttrEnum;
import com.chenyg.uibinder.OnValueChangedListener;
import com.chenyg.uibinder.android.AndroidBinder;

/**
 * Created by ZhuiFeng on 2015/6/12.
 */
public class SeekBarBinder extends AndroidBinder
{
    private SeekBar seekBar;

    public SeekBarBinder(SeekBar seekBar)
    {
        super(seekBar);
        this.seekBar = seekBar;
    }

    private void setOnValueChangedListener(final OnValueChangedListener onValueChangedListener)
    {
        this.onValueChangedListener = onValueChangedListener;
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                currentValue = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                doOnchange();
            }
        });

    }

    @Override
    public void release()
    {
        super.release();
        seekBar = null;
    }

    @Override
    public void set(AttrEnum attrEnum, Object value)
    {
        if (AttrEnum.ATTR_VALUE == attrEnum)
        {
            Integer progress = (Integer) value;
            seekBar.setProgress(progress);
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
            return seekBar.getProgress();
        }
        return super.get(attrEnum);
    }


}
