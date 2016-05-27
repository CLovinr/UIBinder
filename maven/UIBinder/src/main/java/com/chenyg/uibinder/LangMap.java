package com.chenyg.uibinder;

import android.os.Bundle;
import com.chenyg.uibinder.android.BinderActivity;
import com.chenyg.uibinder.android.xmlui.XmlUIPorter;
import com.chenyg.wporter.util.WPTool;

import java.util.HashMap;

/**
 * 用于设置语言
 * Created by ZhuiFeng on 2015/7/9.
 */
public class LangMap
{
    public enum CommonStr
    {
        OK("确定"),
        CANCEL("取消"),
        DESC("描述"),
        NOT_AVAILABLE("不可用"),
        CANNOT_BE_EMPTY("不能为空"),
        ILLEGAL_PARAM("值无效"),
        EXCEPTION("异常"),
        SUCCESS("成功"),
        FAILED("失败"),
        DEAL("处理"),
        DELETE("删除"),
        ADD("添加"),
        ALERT("提示"),
        UPDATE("修改"),
        QUERY("查询"),
        COUNT("数量");
        private String txt;

        CommonStr(String txt)
        {
            this.txt = txt;
        }

        String getTxt()
        {
            return txt;
        }
    }

    private HashMap<String, String> langs;

    private static LangMap langmap = new LangMap();

    private LangMap()
    {
        langs = new HashMap<String, String>();
        CommonStr[] commonStrs = CommonStr.values();
        for (CommonStr commonStr : commonStrs)
        {
            langs.put(commonStr.name(), commonStr.getTxt());
        }
    }


    public static LangMap getLangMap()
    {
        return langmap;
    }

    /**
     * @param commonStr
     * @param value
     * @return
     * @see #put(String, String)
     */
    public LangMap put(CommonStr commonStr, String value)
    {
        return put(commonStr.name(), value);
    }

    public LangMap put(String name, String value)
    {
        if (WPTool.isEmpty(value)) throw new IllegalArgumentException("the value is empty!");
        langs.put(name, value);
        return this;
    }


    /**
     * @param commonStr
     * @param stringId
     * @return
     * @see #putAndroid(String, int)
     */
    public LangMap putAndroid(CommonStr commonStr, int stringId)
    {
        return putAndroid(commonStr.name(), stringId);
    }

    /**
     * 只对安卓有效。
     * 需要在{@linkplain BinderActivity#onCreate(Bundle)}中调用，且在super.onCreate(savedInstanceState)之后.
     *
     * @param name     键名
     * @param stringId R.string.xxx
     * @return
     */
    public LangMap putAndroid(String name, int stringId)
    {
        put(name, XmlUIPorter.getString(null, stringId));
        return this;
    }

    /**
     * 根据string的名称或string的id值得到字符串，只有安卓有效。
     *
     * @param name     字符串的名称
     * @param stringId R.string.xxx
     * @return
     */
    public static String getAndroidString(String name, Integer stringId)
    {
        return XmlUIPorter.getString(name, stringId);
    }

    public static String getAndroidStrings(Object... idsOrStrings)
    {
        StringBuilder stringBuilder = new StringBuilder();
        for (Object obj : idsOrStrings)
        {
            if (obj == null)
            {
                continue;
            }
            if (obj instanceof Integer)
            {
                stringBuilder.append(getAndroidString(null, (Integer) obj));
            }else{
                stringBuilder.append(obj);
            }
        }
        return stringBuilder.toString();
    }

    public String get(String name)
    {
        return langs.get(name);
    }

    public String get(CommonStr commonStr)
    {
        return langs.get(commonStr.name());
    }

    /**
     * @param blank
     * @param names 为String或{@linkplain com.chenyg.uibinder.LangMap.CommonStr}构成的数组
     * @return
     */
    public String getString(String blank, Object... names)
    {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < names.length - 1; i++)
        {
            stringBuilder.append(names[i] instanceof CommonStr ? get((CommonStr) names[i]) : (String) names[i])
                    .append(blank);
        }
        if (names.length > 0)
        {
            stringBuilder.append(names[names.length - 1] instanceof CommonStr ? get(
                    (CommonStr) names[names.length - 1]) : (String) names[names.length - 1]);
        }
        return stringBuilder.toString();
    }
}
