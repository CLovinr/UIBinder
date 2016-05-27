package com.chenyg.uibinder.simple;

import com.chenyg.uibinder.base.HttpDelivery;
import com.chenyg.wporter.base.JResponse;

/**
 * Created by 刚帅 on 2015/12/1.
 */
public interface SimpleDealt
{
    void onSuccess(String porterPrefix, String tiedFun, JResponse jResponse);
    void onException(String porterPrefix, String tiedFun, HttpDelivery.DeliveryException e);
    void onException(String porterPrefix, String tiedFun, JResponse jResponse);
}
