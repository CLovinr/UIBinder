package com.chenyg.uibinder.simple;

import com.chenyg.wporter.base.JSONHeader;
import com.chenyg.wporter.base.JResponse;
import com.chenyg.wporter.base.ResultCode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by 刚帅 on 2015/12/2.
 */
public class SimpleDealtMessage implements JSONHeader
{
    private String porterPrefix;
    private SimpleBinderData binderData;
    private JSONObject srdHow;


    public SimpleDealtMessage(String porterPrefix)
    {
        this.porterPrefix = porterPrefix;
    }

    public SimpleDealtMessage()
    {
        this(null);
    }

    public void setAddResponse(boolean isSuccess, String desc)
    {
        setADUResponse(0, isSuccess, desc);
    }

    public void setDeleteResponse(boolean isSuccess, String desc)
    {
        setADUResponse(1, isSuccess, desc);
    }

    private void setADUResponse(int type, boolean isSuccess, String desc)
    {
        JSONObject jobj = new JSONObject();
        try
        {
            jobj.put("rs", isSuccess);
            jobj.put("type", type);
            jobj.put("desc", desc);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        srdHow = jobj;
    }

    public void setUpdateResponse(boolean isSuccess, String desc)
    {
        setADUResponse(2, isSuccess, desc);
    }

    public void setQueryResponse(boolean isSuccess, String desc, JSONArray as)
    {
        JSONObject jobj = new JSONObject();
        try
        {
            jobj.put("rs", isSuccess);
            jobj.put("type", 3);
            jobj.put("desc", desc);
            jobj.put("as", as);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        srdHow = jobj;
    }

    public SimpleBinderData getSimpleBinderData()
    {
        if (binderData == null)
        {
            binderData = new SimpleBinderData();
        }
        return binderData;
    }

    @Override
    public String toString()
    {

        JResponse jr = new JResponse();
        try
        {
            JSONArray resultArr = new JSONArray();
            if (binderData != null)
            {
                JSONObject[] jsonObjects = binderData.toWhatHowJSON(porterPrefix);
                for (int i = 0; i < jsonObjects.length; i++)
                {
                    resultArr.put(jsonObjects[i]);
                }
            }
            if (srdHow != null)
            {
                JSONObject
                        jsonObject = new JSONObject();
                jsonObject.put("what", "SRD");
                jsonObject.put("how", srdHow);
                resultArr.put(jsonObject);

                jsonObject.getJSONObject("how").put("porterPrefix", porterPrefix);
            }

            jr.setCode(ResultCode.SUCCESS);
            jr.setResult(resultArr);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        return jr.toString();
    }
}
