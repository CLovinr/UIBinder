package com.chenyg.uibinder.android;


import android.view.View;
import com.chenyg.uibinder.AttrEnum;
import com.chenyg.uibinder.BinderExample;


/**
 * <pre>
 * {@linkplain AttrEnum#ATTR_VISIBLE}:0:VISIBLE, 1:INVISIBLE, 2:GONE.
 * </pre>
 * Created by ZhuiFeng on 2015/6/12.
 */
public abstract class AndroidBinder extends BinderExample<View>
{

    public AndroidBinder(View view)
    {
        super(view);
    }


    @Override
    public void release()
    {
        viewType = null;
    }


    @Override
    public Object get(AttrEnum attrEnum)
    {
        if (attrEnum == AttrEnum.ATTR_BOUNDS)
        {
            int[] pos = new int[4];
            pos[2] = viewType.getWidth();
            pos[3] = viewType.getHeight();
            viewType.getLocationInWindow(pos);

            return pos;
        } else
        {
            return null;
        }
    }


    @Override
    public void set(AttrEnum attrEnum, Object value)
    {
        if (AttrEnum.ATTR_ENABLE == attrEnum)
        {
            Boolean enable = (Boolean) value;
            viewType.setEnabled(enable);
        } else if (AttrEnum.ATTR_FOCUS_REQUEST == attrEnum)
        {
            viewType.setFocusable(true);
            viewType.setFocusableInTouchMode(true);
            viewType.requestFocus();
        } else if (AttrEnum.ATTR_VISIBLE == attrEnum)
        {
            if (value instanceof Boolean)
            {
                value = ((Boolean) value) ? 0 : 1;
            }
            int visible = (Integer) value;
            switch (visible)
            {
                case 0:
                    viewType.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    viewType.setVisibility(View.INVISIBLE);
                    break;
                case 2:
                    viewType.setVisibility(View.GONE);
                    break;
                default:
                    throw new RuntimeException("Illegal visible value '"
                            + visible
                            + "',0~2 accepted!");
            }
        }
    }
}
