package com.chenyg.uibinder.base;

import com.chenyg.wporter.base.JResponse;

/**
 * Created by 宇宙之灵 on 2016/7/3.
 */
public interface JRCallback
{
    public void onResult(JResponse jResponse);

    public static final JRCallback EMPTY = new JRCallback()
    {
        @Override
        public void onResult(JResponse jResponse)
        {

        }
    };
}
