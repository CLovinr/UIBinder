package com.chenyg.uibinder.base;

import com.chenyg.wporter.WPObject;
import com.chenyg.wporter.WebPorter;
import com.chenyg.wporter.annotation.ChildIn;
import com.chenyg.wporter.base.AppValues;
import com.chenyg.wporter.base.JResponse;
import com.chenyg.wporter.base.SimpleAppValues;
import com.chenyg.wporter.annotation.FatherIn;
import com.chenyg.wporter.log.LogUtil;
import com.squareup.okhttp.OkHttpClient;


import java.lang.annotation.*;
import java.lang.reflect.Method;


public class HttpDelivery
{

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.ANNOTATION_TYPE)
    @Inherited
    @Documented
    public @interface NV
    {
        /**
         * 名称
         *
         * @return 名称
         */
        String name();

        /**
         * 值
         *
         * @return 值
         */
        String value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Inherited
    @Documented
    public @interface DConfig
    {
        /**
         * 请求url的父绑定名，默认为""(表示使用{@linkplain FatherIn}所表示的绑定名)
         *
         * @return 远程接口绑定名
         */
        String tiedName() default "";

        /**
         * 添加的全局参数
         *
         * @return 全局参数
         */
        NV[] params() default {};
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Documented
    public @interface DFun
    {
        /**
         * 请求url的函数绑定名，默认为""(表示使用{@linkplain ChildIn}所表示的绑定名)
         *
         * @return 远程接口函数的绑定名
         */
        String tiedName() default "";

        /**
         * 添加的参数
         *
         * @return 添加的参数
         */
        NV[] params() default {};
    }

    public static class DeliveryException extends RuntimeException
    {
        public DeliveryException(String info)
        {
            super(info);
        }
    }

    public static class Param
    {
        public String porterPrefix, tiedFun;
    }

    private String urlPrefix;
    private OkHttpClient okHttpClient;

    /**
     * @param urlPrefix    转发的目的地的地址前缀
     * @param okHttpClient
     */
    public HttpDelivery(String urlPrefix, OkHttpClient okHttpClient)
    {
        this.urlPrefix = urlPrefix;
        this.okHttpClient = okHttpClient;
    }

    public String getUrlPrefix()
    {
        return urlPrefix;
    }

    private void checkMethod(Class<?> c, Method method)
    {
        if (!method.isAnnotationPresent(ChildIn.class))
        {
            throw new DeliveryException(
                    "the method '" + c + "." + method.getName() + "' has no '@" + ChildIn.class + "'");
        }
    }

    /**
     * @param httpMethod
     * @param wpObject
     * @return
     * @throws DeliveryException
     */
    public JResponse delivery(HttpMethod httpMethod, WPObject wpObject, JRCallback jrCallback) throws DeliveryException
    {
        return _delivery(httpMethod, wpObject, 1, null, jrCallback);
    }


    /**
     * 转发
     *
     * @param httpMethod
     * @param wpObject
     * @param porterPrefix 接口前缀
     * @param tiedFun
     * @return
     * @throws DeliveryException
     */
    public JResponse delivery(HttpMethod httpMethod, WPObject wpObject, String porterPrefix,
            String tiedFun, JRCallback jrCallback) throws DeliveryException
    {
        JResponse jr;
        String url = urlPrefix + porterPrefix + tiedFun;
        jr = HttpUtil.requestWPorter(wpObject, httpMethod, okHttpClient, url, jrCallback);
        return jr;
    }

    public JResponse delivery(HttpMethod httpMethod, AppValues appValues, String porterPrefix,
            String tiedFun, JRCallback jrCallback) throws DeliveryException
    {
        return delivery(httpMethod, appValues == null ? null : new WPObject(appValues), porterPrefix, tiedFun,
                jrCallback);
    }

    /**
     * 转发
     *
     * @param httpMethod
     * @param wpObject
     * @param wporterClass
     * @param methodName
     * @return
     * @throws DeliveryException
     */
    public JResponse delivery(HttpMethod httpMethod, WPObject wpObject,
            Class<? extends WebPorter> wporterClass, String methodName, Param forParam,
            JRCallback jrCallback) throws DeliveryException
    {
        JResponse jr = null;
        try
        {
            Class<? extends WebPorter> c = wporterClass;
            Method method = c.getMethod(methodName, WPObject.class);
            checkMethod(c, method);
            SimpleAppValues simpleAppValues = getNVAppValues(c, method);
            addWPObjectParams(simpleAppValues, wpObject);
            String url = getUrl(c, method, forParam);
            jr = HttpUtil.requestWPorter(simpleAppValues, httpMethod, okHttpClient, url, jrCallback);
        } catch (NoSuchMethodException e)
        {
            throw new DeliveryException("getMethod EX:" + e.toString());
        }
        return jr;
    }

    /**
     * 转发
     *
     * @param httpMethod
     * @param wpObject
     * @param stack      直接调用则为0，间隔一个函数则为1.
     * @return
     * @throws DeliveryException
     */
    public JResponse _delivery(HttpMethod httpMethod, WPObject wpObject, int stack,
            Param forParam, JRCallback jrCallback) throws DeliveryException
    {
        JResponse jr = null;

        try
        {
            Object[] names = LogUtil.methodAndClass(stack + 2);
            Class<? extends WebPorter> c = (Class<? extends WebPorter>) Class.forName((String) names[1]);
            jr = delivery(httpMethod, wpObject, c, (String) names[0], forParam, jrCallback);
        } catch (ClassNotFoundException e)
        {
            throw new DeliveryException("getClass EX:" + e.toString());
        }
        return jr;
    }

    /**
     * 得到函数绑定名
     *
     * @param c
     * @param method
     * @return
     * @throws NoSuchMethodException
     */
    public static String getTiedFunName(Class<?> c, String method) throws NoSuchMethodException
    {
        Method m = c.getMethod(method, WPObject.class);
        return WebPorter.getFunTiedName(m);
    }

    private String getUrl(Class<?> c, Method method, Param forParam)
    {
        String url = urlPrefix;
        String porterPrefix;
        if (c.isAnnotationPresent(DConfig.class))
        {
            DConfig dc = c.getAnnotation(DConfig.class);
            if (dc.tiedName().equals(""))
            {
                porterPrefix = WebPorter.getTiedName(c);
            } else
            {
                porterPrefix = dc.tiedName();
            }

        } else
        {
            porterPrefix = WebPorter.getTiedName(c);
        }

        String tiedFun;

        if (method.isAnnotationPresent(DFun.class))
        {
            DFun df = method.getAnnotation(DFun.class);
            if (df.tiedName().equals(""))
            {
                tiedFun = WebPorter.getFunTiedName(method);
            } else
            {
                tiedFun = df.tiedName();
            }
        } else
        {
            tiedFun = WebPorter.getFunTiedName(method);
        }

        if (forParam != null)
        {
            forParam.porterPrefix = porterPrefix;
            forParam.tiedFun = tiedFun;
        }

        return url + porterPrefix + tiedFun;
    }

    private void addWPObjectParams(SimpleAppValues simpleAppValues, WPObject wpObject)
    {
        AppValues appValues = new SimpleAppValues(wpObject.inNames.cnNames).values(wpObject.cns);
        simpleAppValues.add(appValues);
        appValues = new SimpleAppValues(wpObject.inNames.cuNames).values(wpObject.cus);
        simpleAppValues.add(appValues);
        appValues = new SimpleAppValues(wpObject.inNames.fnNames).values(wpObject.fns);
        simpleAppValues.add(appValues);
        appValues = new SimpleAppValues(wpObject.inNames.fuNames).values(wpObject.fus);
        simpleAppValues.add(appValues);
    }

    private SimpleAppValues getNVAppValues(Class<?> c, Method method)
    {
        SimpleAppValues sv = new SimpleAppValues();
        if (c.isAnnotationPresent(DConfig.class))
        {
            DConfig dc = c.getAnnotation(DConfig.class);
            addFromNV(sv, dc.params());
        }
        if (method.isAnnotationPresent(DFun.class))
        {
            addFromNV(sv, method.getAnnotation(DFun.class).params());
        }
        return sv;
    }

    private void addFromNV(SimpleAppValues sv, NV[] nvs)
    {
        String[] names = new String[nvs.length];
        Object[] objs = new Object[nvs.length];
        for (int i = 0; i < names.length; i++)
        {
            names[i] = nvs[i].name();
            objs[i] = nvs[i].value();
        }
        sv.add(new SimpleAppValues(names).values(objs));
    }

}
