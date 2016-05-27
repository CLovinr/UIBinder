package com.chenyg.uibinder.base;

import com.chenyg.wporter.base.AppValues;
import com.chenyg.wporter.base.JResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.BasicCookieStore;

/**
 * Created by 宇宙之灵 on 2016/2/15.
 */
public class HttpDeliveryWithOption
{
    private String urlPrefix;
    private CookieStore cookieStore;

    public HttpDeliveryWithOption(String urlPrefix, boolean useCookie)
    {
        this.urlPrefix = urlPrefix;
        if (useCookie)
        {
            cookieStore = new BasicCookieStore();
        }
    }


    public JResponse delivery(HttpMethod httpMethod, AppValues appValues, String porterPrefix,
            String tiedFun, HttpOption httpOption) throws HttpDelivery.DeliveryException
    {
        AbstractHttpClient httpClient = HttpUtil.getHttpClient(httpOption.useCookie ? cookieStore : null);
        HttpDelivery delivery = new HttpDelivery(urlPrefix, httpClient);
        HttpUtil.doHttpOption(httpClient, httpOption);
        if (httpOption.method != null)
        {
            httpMethod = httpOption.method;
        }
        return delivery.delivery(httpMethod, appValues, porterPrefix, tiedFun);
    }
}
