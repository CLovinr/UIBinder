package com.chenyg.uibinder;

import com.chenyg.wporter.base.JResponse;

import java.io.Serializable;

/**
 * Created by ZhuiFeng on 2015/6/12.
 */
public interface ErrListener extends Serializable{
    /**
     * @param jResponse
     * @param porterPrefix
     * @param funName
     * @return 用于操作控件
     */
    BinderData onErr(JResponse jResponse, String porterPrefix, String funName);
     void onException(Exception e, String porterPrefix);
}
