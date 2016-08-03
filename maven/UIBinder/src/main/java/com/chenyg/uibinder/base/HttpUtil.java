package com.chenyg.uibinder.base;

import com.chenyg.wporter.WPForm;
import com.chenyg.wporter.WPObject;
import com.chenyg.wporter.base.AppValues;
import com.chenyg.wporter.base.ContentType;
import com.chenyg.wporter.base.JResponse;
import com.chenyg.wporter.base.ResultCode;
import com.chenyg.wporter.simple.WPFormBuilder;
import com.chenyg.wporter.util.WPTool;
import com.squareup.okhttp.*;
import okio.BufferedSink;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

/**
 * Created by 宇宙之灵 on 2015/9/12.
 */
public class HttpUtil
{

    private static class OkHttpClientImpl extends OkHttpClient
    {
        @Override
        public OkHttpClient setCookieHandler(CookieHandler cookieHandler)
        {
            return this;
        }

        @Override
        public CookieHandler getCookieHandler()
        {
            return null;
        }

        public OkHttpClient _setCookieHandler(CookieHandler cookieHandler)
        {
            return super.setCookieHandler(cookieHandler);
        }
    }


    private static final int SET_CONNECTION_TIMEOUT = 10 * 1000;
    private static final int SET_SOCKET_TIMEOUT = 20 * 1000;
    private static OkHttpClientImpl defaultClient;


    public static void doHttpOption(OkHttpClient okHttpClient, HttpOption httpOption)
    {
        if (httpOption == null)
        {

            return;
        }
        if (httpOption.conn_timeout != null)
        {
            okHttpClient.setConnectTimeout(httpOption.conn_timeout, TimeUnit.MILLISECONDS);
        }

        if (httpOption.so_timeout != null)
        {
            okHttpClient.setReadTimeout(httpOption.so_timeout, TimeUnit.MILLISECONDS);
            okHttpClient.setWriteTimeout(httpOption.so_timeout, TimeUnit.MILLISECONDS);
        }

    }

    private static OkHttpClientImpl _getClient(CookieHandler cookieHandler)
    {

        OkHttpClientImpl okHttpClient = new OkHttpClientImpl();
        okHttpClient.setConnectTimeout(SET_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);
        okHttpClient.setReadTimeout(SET_SOCKET_TIMEOUT, TimeUnit.MILLISECONDS);
        okHttpClient.setWriteTimeout(SET_SOCKET_TIMEOUT, TimeUnit.MILLISECONDS);
        if (cookieHandler == null)
        {
            CookieManager cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_NONE);
            okHttpClient._setCookieHandler(cookieManager);
        } else
        {
            okHttpClient._setCookieHandler(cookieHandler);
        }

        return okHttpClient;
    }


    /**
     * @param cookieHandler 为null时，表示使用默认的对象，并且不支持cookie。
     * @return
     */
    public static synchronized OkHttpClient getClient(CookieHandler cookieHandler)
    {

        if (cookieHandler == null)
        {
            if (defaultClient == null)
            {
                defaultClient = _getClient(null);
            }
            return defaultClient;
        }
        return _getClient(cookieHandler);
    }


    /**
     * 移除末尾指定的字符(若存在的话)。
     *
     * @param sb
     * @param c  要移除的字符
     */
    private static void removeEndChar(StringBuilder sb, char c)
    {
        if (sb.length() > 0 && sb.charAt(sb.length() - 1) == c)
        {
            sb.deleteCharAt(sb.length() - 1);
        }
    }

    /**
     * 添加地址参数
     *
     * @param url        地址
     * @param nameValues 如name=123&age=12
     * @param afterSharp 是否放在#后面
     */
    public static String addUrlParam(String url, String nameValues, boolean afterSharp)
    {
        int index = url.lastIndexOf(afterSharp ? "#" : "?");
        if (index == -1)
        {
            return url + (afterSharp ? "#" : "?") + nameValues;
        } else
        {
            return url + "&" + nameValues;
        }
    }

    private static void addUrlParams(StringBuilder stringBuilder, String[] names,
            Object[] values, String encoding) throws UnsupportedEncodingException
    {
        if (names == null)
        {
            return;
        }
        for (int i = 0; i < names.length; i++)
        {
            if (values[i] != null)
            {
                stringBuilder.append(URLEncoder.encode(names[i], encoding)).append("=")
                        .append(URLEncoder.encode(values[i] + "", encoding)).append('&');
            }
        }
    }

    private static String dealUrlParams(WPObject wpObject, String url) throws UnsupportedEncodingException
    {
        if (wpObject == null || wpObject.inNames == null)
        {
            return url;
        }
        StringBuilder stringBuilder = new StringBuilder();
        String encoding = "utf-8";

        addUrlParams(stringBuilder, wpObject.inNames.cnNames, wpObject.cns, encoding);
        addUrlParams(stringBuilder, wpObject.inNames.cuNames, wpObject.cus, encoding);
        addUrlParams(stringBuilder, wpObject.inNames.fnNames, wpObject.fns, encoding);
        addUrlParams(stringBuilder, wpObject.inNames.fuNames, wpObject.fus, encoding);

        if (stringBuilder.length() > 0)
        {
            removeEndChar(stringBuilder, '&');
            if (url.indexOf('?') == -1)
            {
                url += "?" + stringBuilder;
            } else
            {
                url += "&" + stringBuilder;
            }
        }
        return url;
    }


    private static void addPostParams(FormEncodingBuilder formEncodingBuilder, String[] names, Object[] values)
    {
        if (names == null)
        {
            return;
        }
        for (int i = 0; i < names.length; i++)
        {
            if (values[i] != null)
            {
                formEncodingBuilder.addEncoded(names[i], String.valueOf(values[i]));
            }
        }
    }

    private static RequestBody dealBodyParams(WPObject wpObject) throws
            UnsupportedEncodingException
    {
        if (wpObject == null || wpObject.inNames == null)
        {
            return null;
        }
        FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();

        addPostParams(formEncodingBuilder, wpObject.inNames.cnNames, wpObject.cns);
        addPostParams(formEncodingBuilder, wpObject.inNames.cuNames, wpObject.cus);
        addPostParams(formEncodingBuilder, wpObject.inNames.fnNames, wpObject.fns);
        addPostParams(formEncodingBuilder, wpObject.inNames.fuNames, wpObject.fus);
        return formEncodingBuilder.build();
    }


    /**
     * 连接服务器，并获取请求结果。
     *
     * @param appValues
     * @param httpMethod
     * @param okHttpClient
     * @param url
     * @param jrCallback
     * @return
     * @throws IOException
     */
    public static JResponse request(AppValues appValues, HttpMethod httpMethod, OkHttpClient okHttpClient,
            String url, JRCallback jrCallback) throws IOException
    {
        return requestWPorter(appValues == null ? null : new WPObject(appValues), httpMethod, okHttpClient, url,
                jrCallback);
    }


    public static Response wpFormRequest(boolean isPostOrPut, final WPForm wpForm, OkHttpClient okHttpClient,
            String url, Callback callback) throws IOException
    {
        Response response = null;
        try
        {

            RequestBody requestBody = new RequestBody()
            {
                @Override
                public MediaType contentType()
                {
                    return MediaType.parse("application/octet-stream");
                }

                @Override
                public void writeTo(BufferedSink bufferedSink) throws IOException
                {

                    WPFormBuilder.write(wpForm, bufferedSink.outputStream());
                }
            };

            Request.Builder builder = new Request.Builder().url(url);

            Request request = null;
            if (isPostOrPut)
            {
                builder.post(requestBody);
            } else
            {
                builder.put(requestBody);
            }
            builder.addHeader(WPForm.HEADER_NAME, ContentType.WPORTER_FORM.getType());
            request = builder.build();
            if (callback == null)
            {
                response = okHttpClient.newCall(request).execute();
            } else
            {
                okHttpClient.newCall(request).enqueue(callback);
            }
        } catch (IOException e)
        {
            throw e;
        }

        return response;
    }

    /**
     * 把请求进行转发。
     *
     * @param wpObject     可以为null
     * @param httpMethod
     * @param okHttpClient
     * @param url
     * @param callback     为null表示同步,则返回response；否则表示异步，返回的response一定为null。
     * @return
     * @throws IOException
     */
    public static Response request(WPObject wpObject, HttpMethod httpMethod, OkHttpClient okHttpClient,
            String url, Callback callback) throws IOException
    {
        Response response = null;
        try
        {
            Request.Builder builder = new Request.Builder();
            Request request = null;
            if (httpMethod == null)
            {
                httpMethod = HttpMethod.GET;
            }
            switch (httpMethod)
            {

                case PUT:
                {
                    RequestBody requestBody = dealBodyParams(wpObject);
                    request = builder.url(url).put(requestBody).build();
                }
                break;
                case POST:
                {
                    RequestBody requestBody = dealBodyParams(wpObject);
                    request = builder.url(url).post(requestBody).build();
                }
                break;
                case GET:
                {
                    url = dealUrlParams(wpObject, url);
                    request = builder.url(url).get().build();
                }

                break;
                case DELETE:
                {
                    url = dealUrlParams(wpObject, url);
                    request = builder.url(url).delete().build();
                }
                break;
            }
            if (callback == null)
            {
                response = okHttpClient.newCall(request).execute();
            } else
            {
                okHttpClient.newCall(request).enqueue(callback);
            }
        } catch (IOException e)
        {
            throw e;
        }

        return response;
    }

    public static JResponse requestWPorter(AppValues appValues, HttpMethod httpMethod, OkHttpClient okHttpClient,
            String url, JRCallback jrCallback)
    {
        return requestWPorter(appValues == null ? null : new WPObject(appValues), httpMethod, okHttpClient, url,
                jrCallback);
    }

    public static JResponse wpFormRequestServer(boolean isPostOrPut, final WPForm wpForm, OkHttpClient okHttpClient,
            String url, final JRCallback jrCallback) throws IOException
    {
        JResponse jResponse = null;
        try
        {
            if (jrCallback == null)
            {
                Response response = wpFormRequest(isPostOrPut, wpForm, okHttpClient, url, null);
                jResponse = toJResponse(response);
            } else
            {
                wpFormRequest(isPostOrPut, wpForm, okHttpClient, url, new Callback()
                {
                    @Override
                    public void onFailure(Request request, IOException e)
                    {
                        jrCallback.onResult(onIOException(e));
                    }

                    @Override
                    public void onResponse(Response response) throws IOException
                    {
                        jrCallback.onResult(toJResponse(response));
                    }
                });
            }
        } catch (IOException e)
        {
            jResponse = onIOException(e);
            if (jrCallback != null)
            {
                jrCallback.onResult(jResponse);
            }
        }
        return jResponse;
    }

    private static JResponse toJResponse(Response response)
    {
        JResponse jResponse;
        ResponseBody responseBody = null;
        try
        {
            int code = response.code();
            if (code == 200 || code == 201)
            {
                responseBody = response.body();
                String json = responseBody.string();
                jResponse = JResponse.fromJSON(json);
            } else if (code == 204)
            {
                jResponse = new JResponse(ResultCode.SUCCESS);
            } else
            {
                jResponse = new JResponse(ResultCode.toResponseCode(code));
                jResponse.setDescription(response.message());
            }
        } catch (IOException e)
        {
            jResponse = onIOException(e);
        } catch (JResponse.JResponseFormatException e)
        {
            jResponse = new JResponse();
            jResponse.setCode(ResultCode.SERVER_EXCEPTION);
            jResponse.setDescription(e.toString());
            jResponse.setExCause(e);
        } finally
        {
            WPTool.close(responseBody);
        }
        return jResponse;
    }

    /**
     * 把数据发向服务器，并接受响应结果。（同步的）
     *
     * @param wpObject     可以为null
     * @param httpMethod   像服务器发起的请求方法
     * @param okHttpClient
     * @param url          url地址
     * @param jrCallback
     * @return
     */
    public static JResponse requestWPorter(WPObject wpObject, HttpMethod httpMethod, OkHttpClient okHttpClient,
            String url, final JRCallback jrCallback)
    {
        JResponse jResponse = null;
        try
        {

            if (jrCallback == null)
            {
                Response response = request(wpObject, httpMethod, okHttpClient, url, null);
                jResponse = toJResponse(response);
            } else
            {
                request(wpObject, httpMethod, okHttpClient, url, new Callback()
                {
                    @Override
                    public void onFailure(Request request, IOException e)
                    {
                        jrCallback.onResult(onIOException(e));
                    }

                    @Override
                    public void onResponse(Response response) throws IOException
                    {
                        jrCallback.onResult(toJResponse(response));
                    }
                });
            }
        } catch (IOException e)
        {
            jResponse = onIOException(e);
            if (jrCallback != null)
            {
                jrCallback.onResult(jResponse);
            }
        }
        return jResponse;
    }

    private static JResponse onIOException(IOException e)
    {
        JResponse jResponse = new JResponse();
        jResponse.setCode(ResultCode.NET_EXCEPTION);
        jResponse.setDescription(e.toString());
        jResponse.setExCause(e);
        return jResponse;
    }


}
