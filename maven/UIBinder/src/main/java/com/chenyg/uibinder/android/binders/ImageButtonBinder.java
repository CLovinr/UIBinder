package com.chenyg.uibinder.android.binders;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageButton;
import com.chenyg.uibinder.AttrEnum;
import com.chenyg.uibinder.android.AndroidBinder;

/**
 * Created by ZhuiFeng on 2015/6/12.
 */
public class ImageButtonBinder extends AndroidBinder
{
    private ImageButton imageButton;

    private View.OnClickListener onClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            onOccur();
        }
    };

    public ImageButtonBinder(ImageButton imageButton)
    {
        super(imageButton);
        this.imageButton = imageButton;
        imageButton.setOnClickListener(onClickListener);
    }

    @Override
    public void release()
    {
        super.release();
        imageButton = null;
        onClickListener = null;
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
            imageButton.setImageDrawable((Drawable) value);
        } else
        {
            super.set(attrEnum, value);
        }
    }

    /**
     * @param attrEnum
     * @return Drawable
     */
    @Override
    public Object get(AttrEnum attrEnum)
    {
        if (AttrEnum.ATTR_VALUE == attrEnum)
        {
            return imageButton.getDrawable();
        }
        return super.get(attrEnum);
    }

}
