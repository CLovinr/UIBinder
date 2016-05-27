package com.chenyg.uibinder.simple;


import com.chenyg.uibinder.*;
import com.chenyg.uibinder.base.HttpDelivery;
import com.chenyg.uibinder.base.HttpMethod;
import com.chenyg.wporter.annotation.ThinkType;
import com.chenyg.wporter.base.AppValues;
import com.chenyg.wporter.base.JResponse;
import com.chenyg.wporter.base.ResultCode;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by 刚帅 on 2015/12/1.
 */
public class SimpleDealtPrefix extends Prefix
{
    public static class Params
    {
        public final boolean showWaitting;
        public final SimpleDealt simpleDealt;
        public final String[] exceptFunTieds;
        public final ThinkType thinkType;
        public final HttpDelivery httpDelivery;


        /**
         * @param thinkType      若为REST,则在向远程地址发送请求时不会添加函数绑定名
         * @param httpDelivery   用于具体操作数据转发的对象
         * @param simpleDealt    异步调用.
         * @param showWaitting   是否显示等待框
         * @param exceptFunTieds 排除的绑定函数
         */
        public Params(ThinkType thinkType, HttpDelivery httpDelivery, SimpleDealt simpleDealt,
                boolean showWaitting,
                String... exceptFunTieds)
        {
            this.thinkType = thinkType;
            this.httpDelivery = httpDelivery;
            this.simpleDealt = simpleDealt;
            this.showWaitting = showWaitting;
            this.exceptFunTieds = exceptFunTieds;
            Arrays.sort(this.exceptFunTieds);
        }
    }

    public final Params params;
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    public SimpleDealtPrefix(String idPrefix, String porterPrefix, String bindCallbackMethod, ErrListener errListener,
            Params params)
    {
        super(idPrefix, porterPrefix, bindCallbackMethod, errListener);
        this.params = params;
    }

    public boolean inExcept(String tiedFun)
    {
        return Arrays.binarySearch(params.exceptFunTieds, tiedFun) >= 0;
    }

    public void doOccur(final String porterPrefix, final String tiedFun, final AppValues appValues,
            final HttpMethod httpMethod)
    {

        executorService.execute(new Runnable()
        {
            @Override
            public void run()
            {
                _toServer(httpMethod, appValues, porterPrefix, tiedFun, params.thinkType, params.showWaitting,
                        params.httpDelivery, params.simpleDealt, errListener);
            }
        });

    }


    public static void _toServer(HttpMethod httpMethod, AppValues appValues, String porterPrefix, String tiedFun,
            ThinkType thinkType, boolean showWaitting, HttpDelivery httpDelivery, SimpleDealt simpleDealt,
            ErrListener errListener)
    {
        JResponse jr;
        BaseUI baseUI = BaseUI.getBaseUI();
        if (porterPrefix != null)
        {
            BinderData binderData = new BinderData();
            binderData.addSetTask(new BinderSet(tiedFun, tiedFun, AttrEnum.ATTR_ENABLE, false));
            baseUI.sendBinderData(porterPrefix, binderData, false);
        }
        try
        {
            if (showWaitting)
            {
                baseUI.waitingShow();
            }
            jr = httpDelivery
                    .delivery(httpMethod, appValues, porterPrefix, thinkType == ThinkType.REST ? "" : tiedFun);
            if (showWaitting)
            {
                baseUI.waitingDisShow();
            }
            dealResponse(httpMethod, httpDelivery, jr, errListener, simpleDealt, porterPrefix, tiedFun);
        } catch (HttpDelivery.DeliveryException e)
        {
            simpleDealt.onException(porterPrefix, tiedFun, e);
        } finally
        {
            if (porterPrefix != null)
            {
                BinderData binderData = new BinderData();
                binderData.addSetTask(new BinderSet(tiedFun, tiedFun, AttrEnum.ATTR_ENABLE, true));
                baseUI.sendBinderData(porterPrefix, binderData, false);
            }
        }
    }

    public static void dealResponse(HttpMethod httpMethod, HttpDelivery httpDelivery, JResponse jr,
            ErrListener errListener, SimpleDealt simpleDealt, String porterPrefix, String tiedFun)
    {
        if (jr.getCode() == ResultCode.SUCCESS)
        {
            simpleDealt.onSuccess(porterPrefix, tiedFun, jr);
        } else if (jr.getCode() == ResultCode.NOT_AVAILABLE)
        {
            BaseUI.getBaseUI().alert(LangMap.getLangMap().get(LangMap.CommonStr.NOT_AVAILABLE), "(", httpMethod.name(),
                    ":",
                    httpDelivery.getUrlPrefix() + porterPrefix + tiedFun,
                    ")");
        } else
        {
            BinderData binderData = errListener.onErr(jr, porterPrefix, tiedFun);
            if (binderData != null)
            {
                BaseUI.getBaseUI().sendBinderData(porterPrefix, binderData, false);
            }
            simpleDealt.onException(porterPrefix, tiedFun, jr);
        }
    }

}
