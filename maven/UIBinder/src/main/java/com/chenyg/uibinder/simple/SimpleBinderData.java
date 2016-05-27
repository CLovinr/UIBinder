package com.chenyg.uibinder.simple;


import com.chenyg.uibinder.AttrEnum;
import com.chenyg.uibinder.BinderData;
import com.chenyg.uibinder.BinderSet;
import com.chenyg.wporter.annotation.MayNULL;
import com.chenyg.wporter.annotation.NotNULL;
import com.chenyg.wporter.base.AppValues;
import com.chenyg.wporter.base.RequestMethod;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by 刚帅 on 2015/12/2.
 */
public class SimpleBinderData
{
    public enum Ctrl
    {
        /**
         * 设置值
         */
        SET_VALUE,
        /**
         * 设置可见性
         */
        SET_VISIBLE,
        /**
         * 设置控件可用性
         */
        SET_ENABLE
    }

    public static class CtrlData
    {
        /**
         * 控件的接口方法绑定名
         */
        String tiedFunName;

        /**
         * 接口中对应的参数名称
         */
        String paramName;
        /**
         * 值
         */
        Object value;

        Ctrl ctrl;

        /**
         * @param tiedFunName 控件的接口方法绑定名
         * @param paramName   接口中对应的参数名称
         * @param value       值
         */
        public CtrlData(String tiedFunName, String paramName, Object value, Ctrl ctrl)
        {
            this.tiedFunName = tiedFunName;
            this.paramName = paramName;
            this.value = value;
            this.ctrl = ctrl;
        }

        public CtrlData(String paramName, Object value, Ctrl ctrl)
        {
            this.paramName = paramName;
            this.value = value;
            this.ctrl = ctrl;
        }
    }

    static class AlertMsg
    {
        String title;
        String content;

        public AlertMsg(String title, String content)
        {
            this.title = title;
            this.content = content;
        }
    }

    static class UIDealt
    {
        String uiFun;
        HashMap<String, Object> hashMap;

        UIDealt(String uiFun)
        {
            this.uiFun = uiFun;
            this.hashMap = new HashMap<>();
        }

        UIDealt put(String key, Object value)
        {
            hashMap.put(key, value);
            return this;
        }
    }

    BinderData binderData;
    private List<BinderSet> binderSets;
    private List<AlertMsg> alertMsgs;
    private List<UIDealt> uiDealts;

    public SimpleBinderData()
    {
        binderData = new BinderData();
    }

    private void checkUIDealts()
    {
        if (uiDealts == null)
        {
            uiDealts = new ArrayList<>();
        }
    }


    public SimpleBinderData addInvoke(@NotNULL String tiedFun, @MayNULL AppValues appValues,
            @MayNULL RequestMethod method)
    {
        checkUIDealts();
        UIDealt uiDealt = new UIDealt("invoke");
        JSONObject params = new JSONObject();

        if (appValues != null)
        {
            String names[] = appValues.getNames();
            Object[] objects = appValues.getValues();
            try
            {
                for (int i = 0; i < names.length; i++)
                {

                    params.put(names[i], objects[i]);

                }
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        if (method != null)
        {
            uiDealt.put("method", method.name());
        }

        uiDealt.put("tiedFun", tiedFun).put("params", params);
        uiDealts.add(uiDealt);

        return this;
    }

    public SimpleBinderData addAlert(String title, String content)
    {
        if (alertMsgs == null)
        {
            alertMsgs = new ArrayList<>();
        }
        alertMsgs.add(new AlertMsg(title, content));
        return this;
    }

    public SimpleBinderData addCtrlData(CtrlData... ctrlDatas)
    {
        if (binderSets == null)
        {
            binderSets = binderData.addSetTask();
        }
        for (CtrlData ctrlData : ctrlDatas)
        {
            AttrEnum attrEnum = null;
            switch (ctrlData.ctrl)
            {

                case SET_VALUE:
                    attrEnum = AttrEnum.ATTR_VALUE;
                    break;
                case SET_VISIBLE:
                    attrEnum = AttrEnum.ATTR_VISIBLE;
                    break;
                case SET_ENABLE:
                    attrEnum = AttrEnum.ATTR_ENABLE;
                    break;
            }
            binderSets.add(new BinderSet(ctrlData.tiedFunName, ctrlData.paramName, attrEnum, ctrlData.value));
        }
        return this;
    }


    JSONObject[] toWhatHowJSON(String porterPrefix) throws JSONException
    {
        JSONObject[] resultJSONs = new JSONObject[1 + (alertMsgs != null ? 1 : 0) + (uiDealts == null ? 0 : uiDealts
                .size())];
        int index = 0;
        if (binderData != null && binderData.size() > 0)
        {
            JSONObject jsonObject = new JSONObject();
            resultJSONs[index++] = jsonObject;
            jsonObject.put("what", "SSD");
            JSONObject how = new JSONObject();
            how.put("porterPrefix", porterPrefix);
            jsonObject.put("how", how);

            JSONArray sets = new JSONArray();
            how.put("sets", sets);

            List<BinderData.Task> tasks = binderData.getTasks();
            for (int i = 0; i < tasks.size(); i++)
            {
                BinderData.Task task = tasks.get(i);
                if (task.method == AttrEnum.METHOD_SET)
                {
                    List<BinderSet> binderSets = (List<BinderSet>) task.data;
                    for (int j = 0; j < binderSets.size(); j++)
                    {
                        BinderSet binderSet = binderSets.get(j);

                        JSONObject _set = new JSONObject();
                        _set.put("attrEnum", binderSet.attrEnum);
                        _set.put("tiedFun", binderSet.tiedFunName);
                        _set.put("value", binderSet.value);
                        _set.put("varName", binderSet.paramName);
                        sets.put(_set);

                    }

                }

            }
        }

        if (alertMsgs != null && alertMsgs.size() > 0)
        {

            JSONObject jsonObject = new JSONObject();
            resultJSONs[index++] = jsonObject;
            jsonObject.put("what", "UI");
            JSONObject how = new JSONObject();
            how.put("porterPrefix", porterPrefix);
            jsonObject.put("how", how);
            how.put("uiFun", "alert");


            JSONArray content = new JSONArray();
            how.put("content", content);

            for (int i = 0; i < alertMsgs.size(); i++)
            {
                JSONObject _msg = new JSONObject();
                AlertMsg msg = alertMsgs.get(i);
                _msg.put("title", msg.title);
                _msg.put("content", msg.content);
                content.put(_msg);
            }
        }

        if (uiDealts != null)
        {
            for (int i = 0; i < uiDealts.size(); i++)
            {
                UIDealt uiDealt = uiDealts.get(i);
                JSONObject jsonObject = new JSONObject();
                resultJSONs[index++] = jsonObject;
                jsonObject.put("what", "UI");
                JSONObject how = new JSONObject();
                how.put("porterPrefix", porterPrefix);
                jsonObject.put("how", how);
                how.put("uiFun", uiDealt.uiFun);

                Iterator<String> names = uiDealt.hashMap.keySet().iterator();
                while (names.hasNext())
                {
                    String name = names.next();
                    how.put(name, uiDealt.hashMap.get(name));
                }
            }
        }

        return resultJSONs;
    }


}
