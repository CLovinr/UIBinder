package com.chenyg.uibinder.js.android;

import com.chenyg.androidjs.WEBView;
import com.chenyg.uibinder.js.JsPage;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;

/**
 * Created by 宇宙之灵 on 2016/5/21.
 */
public class JsInterface
{

    private JsPage jsPage;

    /**
     * 用于get，获取属性值。
     *
     * @param webView
     * @param returnData
     */
    public void jsBinderGetter(WEBView webView, String cid, JSONObject returnData)
    {
        JsPageBuilder.onGet(cid, returnData);
    }

    public void jsBinder(WEBView webView, JSONObject data) throws Exception
    {
        ExecutorService executorService = webView.getExecutor();
        if (jsPage != null)
        {
            jsPage.release();
        }
        try
        {
            jsPage = JsPageBuilder.build(webView, executorService, data);
        } catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }

    }

    public void jsBinderRelease(WEBView webView)
    {
        if (jsPage != null)
        {
            jsPage.release();
            jsPage = null;
        }
    }
}
