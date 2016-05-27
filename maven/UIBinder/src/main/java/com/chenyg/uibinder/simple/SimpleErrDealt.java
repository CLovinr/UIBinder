package com.chenyg.uibinder.simple;

import com.chenyg.uibinder.*;
import com.chenyg.wporter.base.JResponse;
import com.chenyg.wporter.base.ResultCode;
import com.chenyg.wporter.log.LogUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 错误的简单处理（针对缺少必须参数和输入参数格式不对）
 * Created by 宇宙之灵 on 2015/9/13.
 */
public class SimpleErrDealt implements ErrListener
{
    private static final SimpleErrDealt simpleErrDealt = new SimpleErrDealt();

    public static SimpleErrDealt getSimpleErrDealt()
    {
        return simpleErrDealt;
    }

    public static BinderData deal(JResponse jResponse, String funName) throws JSONException
    {
        try
        {
            if (jResponse.getCode() == ResultCode.LACK_NECE_PARAMS)
            {
                JSONArray array = jResponse.getNeedArgs();
                JSONObject jsonObject = array.getJSONObject(0);
                String name = jsonObject.getString(JResponse.NEED_ARGS_NAME_FIELD);
                String desc = jsonObject.has(JResponse.NEED_ARGS_DESC_FIELD) ? jsonObject
                        .getString(JResponse.NEED_ARGS_DESC_FIELD) : name;

                BaseUI.getBaseUI()
                        .toast("'" + desc + "'" + LangMap.getLangMap().get(LangMap.CommonStr.CANNOT_BE_EMPTY));
                BinderData binderData = new BinderData();
                binderData.addSetTask(new BinderSet(funName, name, AttrEnum.ATTR_FOCUS_REQUEST, null));
                return binderData;

            } else if (jResponse.getCode() == ResultCode.ILLEGAL_PARAM)
            {
                JSONArray array = jResponse.getIllegalArgs();
                JSONObject jsonObject = array.getJSONObject(0);
                String name = jsonObject.getString("name");
                String udesc = jsonObject.has("udesc") ? ":\n" + jsonObject.getString("udesc") : "";
                BaseUI.getBaseUI().toast(LangMap.getLangMap().get(LangMap.CommonStr.ILLEGAL_PARAM)+udesc);
                BinderData binderData = new BinderData();
                binderData.addSetTask(new BinderSet(funName, name, AttrEnum.ATTR_FOCUS_REQUEST, null));
                return binderData;
            } else
            {
                LogUtil.printPosLn(jResponse);
                BaseUI.getBaseUI()
                        .alert(LangMap.getLangMap().get(LangMap.CommonStr.EXCEPTION), jResponse.getDescription(),
                                null);
            }

        } catch (JSONException e)
        {
            throw e;
        }
        return null;
    }

    @Override
    public BinderData onErr(JResponse jResponse, String porterPrefix, String funName)
    {
        BinderData binderData = null;
        try
        {
            binderData = deal(jResponse, funName);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return binderData;
    }

    @Override
    public void onException(Exception e, String porterPrefix)
    {
        LogUtil.printPosLn(porterPrefix, "\n");
        e.printStackTrace();
    }
}
