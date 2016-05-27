package com.chenyg.uibinder.android.binders;

import android.view.View;
import android.widget.Button;
import com.chenyg.uibinder.AttrEnum;
import com.chenyg.uibinder.android.AndroidBinder;

/**
 * Created by ZhuiFeng on 2015/6/11.
 */
public class ButtonBinder extends AndroidBinder
{
    private Button button;

    private View.OnClickListener onClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            onOccur();
        }
    };

    public ButtonBinder(Button button)
    {
        super(button);
        this.button = button;
        button.setOnClickListener(onClickListener);
    }

    @Override
    public void release()
    {
        super.release();
        button = null;
        onClickListener = null;
    }

    @Override
    public void set(AttrEnum attrEnum, Object value)
    {
        if (AttrEnum.ATTR_VALUE == attrEnum)
        {
            button.setText(value.toString());
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
            return button.getText();
        }
        return super.get(attrEnum);
    }


}
