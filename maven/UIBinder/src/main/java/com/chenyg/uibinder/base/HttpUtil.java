package com.chenyg.uibinder.base;

import com.chenyg.wporter.WPForm;
import com.chenyg.wporter.WPObject;
import com.chenyg.wporter.base.AppValues;
import com.chenyg.wporter.base.ContentType;
import com.chenyg.wporter.base.JResponse;
import com.chenyg.wporter.base.ResultCode;
import com.chenyg.wporter.log.LogUtil;
import com.chenyg.wporter.simple.WPFormBuilder;
import com.chenyg.wporter.util.WPTool;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.*;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 宇宙之灵 on 2015/9/12.
 */
public class HttpUtil
{
    private static final int SET_CONNECTION_TIMEOUT = 10 * 1000;
    private static final int SET_SOCKET_TIMEOUT = 20 * 1000;


    public static void doHttpOption(AbstractHttpClient httpClient, HttpOption httpOption)
    {
        if (httpOption == null)
        {

            return;
        }
        if (httpOption.conn_timeout != null)
        {
            httpClient.getParams()
                    .setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, httpOption.conn_timeout.intValue());
        }

        if (httpOption.so_timeout != null)
        {
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, httpOption.so_timeout.intValue());
        }

    }


    /**
     * 支持https,使用同一个HttpClient可以保持会话。
     *
     * @return
     */

    public static AbstractHttpClient getHttpClient()
    {
        return getHttpClient(null);
    }

    public static AbstractHttpClient getHttpClient(CookieStore cookieStore)
    {
        DefaultHttpClient client;
        try
        {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();

            HttpConnectionParams.setConnectionTimeout(params, 10000);
            HttpConnectionParams.setSoTimeout(params, 10000);

            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            HttpConnectionParams.setConnectionTimeout(params, SET_CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(params, SET_SOCKET_TIMEOUT);
            client = new DefaultHttpClient(ccm, params);


        } catch (Exception e)
        {
            client = new DefaultHttpClient();
        }

        HttpParams httpParams = client.getParams();
        if (cookieStore != null)
        {
            client.setCookieStore(cookieStore);
            httpParams.setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BEST_MATCH);
        } else
        {
            try
            {
                LogUtil.printErrPosLn(CookiePolicy.IGNORE_COOKIES);
                CookiePolicy.class.getField("IGNORE_COOKIES");
                httpParams.setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.IGNORE_COOKIES);
            } catch (Exception e)
            {
                //e.printStackTrace();
            }

        }

        return client;
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


    private static void addPostParams(List<NameValuePair> params, String[] names, Object[] values)
    {
        if (names == null)
        {
            return;
        }
        for (int i = 0; i < names.length; i++)
        {
            if (values[i] != null)
            {
                params.add(new BasicNameValuePair(names[i], values[i] + ""));
            }
        }
    }

    private static void dealBodyParams(WPObject wpObject, HttpEntityEnclosingRequestBase requestBase) throws
            UnsupportedEncodingException
    {
        if (wpObject == null || wpObject.inNames == null)
        {
            return;
        }
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        addPostParams(params, wpObject.inNames.cnNames, wpObject.cns);
        addPostParams(params, wpObject.inNames.cuNames, wpObject.cus);
        addPostParams(params, wpObject.inNames.fnNames, wpObject.fns);
        addPostParams(params, wpObject.inNames.fuNames, wpObject.fus);

        requestBase.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
    }


    /**
     * 连接服务器，并获取请求结果。
     *
     * @param appValues
     * @param httpMethod
     * @param httpClient
     * @param url
     * @return
     * @throws IOException
     */
    public static JResponse request(AppValues appValues, HttpMethod httpMethod, HttpClient httpClient,
            String url) throws IOException
    {
        return requestWPorter(appValues == null ? null : new WPObject(appValues), httpMethod, httpClient, url);
    }


    public static HttpResponse wpFormRequest(boolean isPost, final WPForm wpForm, HttpClient httpClient,
            String url) throws IOException
    {
        HttpResponse httpResponse;
        try
        {

            HttpEntityEnclosingRequestBase requestBase;
            if (isPost)
            {
                requestBase = new HttpPost(url);
            } else
            {
                requestBase = new HttpPut(url);
            }
            requestBase.setHeader(ContentType.HEADER_NAME, "application/octet-stream");
            requestBase.setHeader(WPForm.HEADER_NAME, ContentType.WPORTER_FORM.getType());
            ContentProducer producer = new ContentProducer()
            {
                @Override
                public void writeTo(OutputStream outputStream) throws IOException
                {
                    WPFormBuilder.write(wpForm, outputStream);
                }
            };
            requestBase.setEntity(new EntityTemplate(producer));
            httpResponse = httpClient.execute(requestBase);

        } catch (IOException e)
        {
            throw e;
        }

        return httpResponse;
    }

    /**
     * 把请求进行转发。
     *
     * @param wpObject   可以为null
     * @param httpMethod
     * @param httpClient
     * @param url
     * @return
     */
    public static HttpResponse request(WPObject wpObject, HttpMethod httpMethod, HttpClient httpClient,
            String url) throws IOException
    {
        HttpResponse httpResponse = null;
        try
        {
            HttpUriRequest request = null;
            if (httpMethod == null)
            {
                httpMethod = HttpMethod.GET;
            }
            switch (httpMethod)
            {

                case PUT:
                {
                    HttpPut httpPut = new HttpPut(url);
                    dealBodyParams(wpObject, httpPut);
                    request = httpPut;
                }
                break;
                case POST:
                {
                    HttpPost httpPost = new HttpPost(url);
                    dealBodyParams(wpObject, httpPost);
                    request = httpPost;
                }
                break;
                case GET:
                {
                    url = dealUrlParams(wpObject, url);
                    request = new HttpGet(url);
                }

                break;
                case DELETE:
                {
                    url = dealUrlParams(wpObject, url);
                    request = new HttpDelete(url);
                }
                break;
            }
            httpResponse = httpClient.execute(request);
        } catch (IOException e)
        {
            throw e;
        }

        return httpResponse;
    }

    public static JResponse requestWPorter(AppValues appValues, HttpMethod httpMethod, HttpClient httpClient,
            String url)
    {
        return requestWPorter(appValues == null ? null : new WPObject(appValues), httpMethod, httpClient, url);
    }

    public static JResponse wpFormRequestServer(boolean isPost, final WPForm wpForm, HttpClient httpClient,
            String url) throws IOException
    {
        JResponse jResponse;
        try
        {
            HttpResponse httpResponse = wpFormRequest(isPost, wpForm, httpClient, url);
            jResponse = _requestWPorter(httpResponse);
        } catch (IOException e)
        {
            jResponse = new JResponse();
            jResponse.setCode(ResultCode.NET_EXCEPTION);
            jResponse.setDescription(e.toString());
            jResponse.setExCause(e);
        }
        return jResponse;
    }

    private static JResponse _requestWPorter(HttpResponse httpResponse)
    {
        JResponse jResponse;
        boolean needClose = true;
        try
        {
            int code = httpResponse.getStatusLine().getStatusCode();
            if (code == 200 || code == 201)
            {
                HttpEntity httpEntity = httpResponse.getEntity();
                String json = EntityUtils.toString(httpEntity);
                needClose = false;
                jResponse = JResponse.fromJSON(json);
            } else if (code == 204)
            {
                jResponse = new JResponse(ResultCode.SUCCESS);
            } else
            {
                jResponse = new JResponse(ResultCode.toResponseCode(code));
                jResponse.setDescription(httpResponse.getStatusLine().getReasonPhrase());
            }
        } catch (IOException e)
        {
            jResponse = new JResponse();
            jResponse.setCode(ResultCode.NET_EXCEPTION);
            jResponse.setDescription(e.toString());
            jResponse.setExCause(e);
        } catch (JResponse.JResponseFormatException e)
        {
            jResponse = new JResponse();
            jResponse.setCode(ResultCode.SERVER_EXCEPTION);
            jResponse.setDescription(e.toString());
            jResponse.setExCause(e);
        } finally
        {
            if (needClose)
            {
                try
                {
                    WPTool.close(httpResponse.getEntity().getContent());
                } catch (Exception e)
                {
                    //e.printStackTrace();
                }
            }

        }
        return jResponse;
    }

    /**
     * 把数据发向服务器，并接受响应结果。（同步的）
     *
     * @param wpObject   可以为null
     * @param httpMethod 像服务器发起的请求方法
     * @param httpClient
     * @param url        url地址
     * @return
     * @see #requestWPorter(WPObject, HttpMethod, HttpClient, String)
     */
    public static JResponse requestWPorter(WPObject wpObject, HttpMethod httpMethod, HttpClient httpClient,
            String url)
    {
        JResponse jResponse;
        try
        {
            HttpResponse httpResponse = request(wpObject, httpMethod, httpClient, url);
            jResponse = _requestWPorter(httpResponse);
        } catch (IOException e)
        {
            jResponse = new JResponse();
            jResponse.setCode(ResultCode.NET_EXCEPTION);
            jResponse.setDescription(e.toString());
            jResponse.setExCause(e);
        }
        return jResponse;
    }


}
