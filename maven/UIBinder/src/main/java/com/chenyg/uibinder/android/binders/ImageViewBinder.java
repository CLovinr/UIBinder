package com.chenyg.uibinder.android.binders;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import com.chenyg.uibinder.AttrEnum;
import com.chenyg.uibinder.android.AndroidBinder;

/**
 * Created by 宇宙之灵 on 2015/10/19.
 */
public class ImageViewBinder extends AndroidBinder
{
    private ImageView imageView;

    private View.OnClickListener onClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            onOccur();
        }
    };

    public ImageViewBinder(ImageView imageView)
    {
        super(imageView);
        this.imageView = imageView;
        imageView.setOnClickListener(onClickListener);
    }

    @Override
    public void release()
    {
        super.release();
        imageView = null;
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
            imageView.setImageDrawable((Drawable) value);
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
            return imageView.getDrawable();
        }
        return super.get(attrEnum);
    }

}
