package com.chenyg.uibinder.simple;


import com.chenyg.uibinder.*;
import com.chenyg.uibinder.base.ChooseCallback;
import com.chenyg.uibinder.base.HttpDelivery;
import com.chenyg.uibinder.base.HttpUtil;
import com.chenyg.wporter.a.app.AppPorterUtil;
import com.chenyg.wporter.annotation.ThinkType;
import com.chenyg.wporter.base.AppValues;
import com.chenyg.wporter.base.JResponse;
import com.chenyg.wporter.base.RequestMethod;
import com.chenyg.wporter.base.SimpleAppValues;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class AutoPrefix extends SimpleDealtPrefix
{


    public AutoPrefix(String idPrefix, String porterPrefix,
            ThinkType thinkType, String urlPrefix)
    {
        super(idPrefix, porterPrefix, null, SimpleErrDealt.getSimpleErrDealt(),
                new Params(thinkType, new HttpDelivery(urlPrefix,
                        HttpUtil.getHttpClient()), getSimpleDealt(), true));
    }

    public AutoPrefix(String idPrefix, String porterPrefix, String urlPrefix)
    {
        this(idPrefix, porterPrefix, ThinkType.DEFAULT, urlPrefix);
    }


    public static SimpleDealt getSimpleDealt()
    {
        return new DefaultSimpleDealt();
    }


    static class DefaultSimpleDealt implements SimpleDealt
    {

        void dealSRD(JSONObject how) throws JSONException
        {
            LangMap langMap = LangMap.getLangMap();
            BaseUI baseUI = BaseUI.getBaseUI();
            String desc = how.getString("desc");
            if (desc != null)
            {
                baseUI.alert(desc);
            } else
            {
                switch (how.getInt("type"))
                {
                    case 0://add
                        baseUI.alert(langMap.getString(" ", LangMap.CommonStr.ADD,
                                        how.getBoolean("rs") ? LangMap.CommonStr.SUCCESS : LangMap.CommonStr.FAILED),
                                how.getBoolean("rs") ? "" : "\n" + langMap.get(
                                        LangMap.CommonStr.DESC) + ":" + how.getString("desc"));
                        break;
                    case 1://delete

                        baseUI.alert(langMap.getString(" ", LangMap.CommonStr.DELETE,
                                        how.getBoolean("rs") ? LangMap.CommonStr.SUCCESS : LangMap.CommonStr.FAILED),
                                how.getBoolean("rs") ? "" : "\n" + langMap.get(
                                        LangMap.CommonStr.DESC) + ":" + how.getString("desc"));
                        break;
                    case 2://update

                        baseUI.alert(langMap.getString(" ", LangMap.CommonStr.UPDATE,
                                        how.getBoolean("rs") ? LangMap.CommonStr.SUCCESS : LangMap.CommonStr.FAILED),
                                how.getBoolean("rs") ? "" : "\n" + langMap.get(
                                        LangMap.CommonStr.DESC) + ":" + how.getString("desc"));
                        break;
                    case 3://query
                        if (!how.getBoolean("rs"))
                        {
                            baseUI.alert(langMap.getString(" ", LangMap.CommonStr.QUERY, LangMap.CommonStr.FAILED),
                                    "\n",
                                    langMap.get(
                                            LangMap.CommonStr.DESC), ":", how.getString("desc"));
                        }

                        break;
                    default:
                        throw new RuntimeException("服务器响应数据格式错误!\n" + how);
                }
            }

        }

        void dealSSD(String porterPrefix, String tiedFun, JSONObject how) throws JSONException
        {

            JSONArray jsonArray = how.getJSONArray("sets");
            BinderData binderData = new BinderData();
            List<BinderSet> list = binderData.addSetTask();

            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject setObj = jsonArray.getJSONObject(i);
                String _tiedFun = setObj.has("tiedFun") ? setObj.getString("tiedFun") : tiedFun;
                String varName = setObj.getString("varName");
                AttrEnum attrEnum = AttrEnum.valueOf(setObj.getString("attrEnum"));
                Object value = setObj.get("value");
                if (attrEnum != null)
                {
                    list.add(new BinderSet(_tiedFun, varName, attrEnum,
                            value));
                }

            }
            BaseUI.getBaseUI().sendBinderData(porterPrefix, binderData, false);
        }


        private void alerts(final JSONObject alertsObj)
        {

            try
            {
                JSONArray array = alertsObj.getJSONArray("alerts");
                int index = alertsObj.getInt("index");
                if (index >= array.length())
                {
                    return;
                }

                JSONObject jsonObject = array.getJSONObject(index);
                alertsObj.put("index", index + 1);

                BaseUI.getBaseUI().alert(jsonObject.getString("title"), jsonObject.getString("content"),
                        new ChooseCallback()
                        {
                            @Override
                            public void onOk()
                            {
                                alerts(alertsObj);
                            }

                            @Override
                            public void onCancel()
                            {
                            }
                        });
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        void dealUI(String porterPrefix, JSONObject how) throws JSONException
        {
            String uiFun = how.getString("uiFun");
            switch (uiFun)
            {
                case "alert":
                {
                    JSONArray alerts = how.getJSONArray("content");
                    if (alerts.length() == 0)
                    {
                        return;
                    }
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("index", 0);
                    jsonObject.put("alerts", alerts);
                    alerts(jsonObject);
                }
                break;

                case "invoke":
                {
                    AppValues appValues = SimpleAppValues
                            .fromJSON(how.has("params") ? how.getJSONObject("params") : null);
                    RequestMethod requestMethod = how.has("method") ? RequestMethod
                            .valueOf(how.getString("method")) : RequestMethod.GET;
                    AppPorterUtil.getPorterObject(porterPrefix, how.getString("tiedFun"), appValues, requestMethod);
                }
                break;
            }

        }

        @Override
        public void onSuccess(String _porterPrefix, String tiedFun, JResponse jResponse)
        {

            try
            {
                JSONArray resultArr = (JSONArray) jResponse.getResult();
                for (int i = 0; i < resultArr.length(); i++)
                {
                    JSONObject json = resultArr.getJSONObject(i);
                    JSONObject how = json.getJSONObject("how");
                    String porterPrefix = _porterPrefix;
                    if (how.has("porterPrefix"))
                    {
                        porterPrefix = how.getString("porterPrefix");
                    }
                    switch (json.getString("what"))
                    {
                        case "SRD":
                            dealSRD(how);
                            break;
                        case "SSD":
                            dealSSD(porterPrefix, tiedFun, how);
                            break;
                        case "UI":
                            dealUI(porterPrefix, how);
                            break;
                        default:
                            throw new RuntimeException("服务器响应数据格式错误!\n" + json);
                    }
                }

            } catch (Exception e)
            {
                e.printStackTrace();
                BaseUI.getBaseUI().alert(LangMap.getLangMap().get(LangMap.CommonStr.EXCEPTION), e.getMessage(), null);
            }
        }

        @Override
        public void onException(String porterPrefix, String tiedFun, HttpDelivery.DeliveryException e)
        {
            e.printStackTrace();
            BaseUI.getBaseUI().alert(LangMap.getLangMap().get(LangMap.CommonStr.EXCEPTION), e.getMessage(), null);
        }

        @Override
        public void onException(String porterPrefix, String tiedFun, JResponse jResponse)
        {
            try
            {
                SimpleErrDealt.deal(jResponse, tiedFun);

            } catch (JSONException e)
            {
                BaseUI.getBaseUI()
                        .alert(LangMap.getLangMap().get(LangMap.CommonStr.EXCEPTION), jResponse.toString(), null);
            }

        }
    }

}
