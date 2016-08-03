package com.chenyg.uibinder.base;

import com.chenyg.wporter.base.AppValues;
import com.chenyg.wporter.base.JResponse;
import com.squareup.okhttp.OkHttpClient;


import java.net.*;

/**
 * Created by 宇宙之灵 on 2016/2/15.
 */
public class HttpDeliveryWithOption
{
    private String urlPrefix;
    private CookieHandler cookieHandler;


    public HttpDeliveryWithOption(String urlPrefix, boolean useCookie)
    {
        this.urlPrefix = urlPrefix;
        CookieManager cookieManager = new CookieManager();
        if (useCookie)
        {
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        } else
        {
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_NONE);
        }
        cookieHandler = cookieManager;
    }


    public JResponse delivery(HttpMethod httpMethod, AppValues appValues, String porterPrefix,
            String tiedFun, HttpOption httpOption, JRCallback jrCallback) throws HttpDelivery.DeliveryException
    {
        OkHttpClient okHttpClient = HttpUtil.getClient(httpOption.useCookie ? cookieHandler : null);
        HttpDelivery delivery = new HttpDelivery(urlPrefix, okHttpClient);
        HttpUtil.doHttpOption(okHttpClient, httpOption);
        if (httpOption.method != null)
        {
            httpMethod = httpOption.method;
        }
        return delivery.delivery(httpMethod, appValues, porterPrefix, tiedFun, jrCallback);
    }
}
