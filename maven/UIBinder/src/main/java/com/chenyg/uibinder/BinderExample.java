package com.chenyg.uibinder;


import com.chenyg.uibinder.base.HttpMethod;
import com.chenyg.wporter.util.WPTool;

import java.util.ArrayList;

/**
 * <pre>
 *     1.以“_”结尾的是用来触发的，以“__”结尾的表示最后一个是请求方法
 *     2.全部以“_”分割
 *     例子，id前缀为test_
 *     1)test_name_setName_setInfo:控件绑定的变量名为name,与方法setName和setInfo绑定值一起
 *     2)test_getName_refresh_:控件与getName,refresh进行绑定。
 *     3)test_getName_refresh_POST__:控件与getName,refresh进行绑定,请求方法为POST。
 * </pre>
 *
 * @param <T>
 */
public abstract class BinderExample<T> extends Binder<T>
{

    private static final Object EMPTY_OBJ = new Object();
    protected Object currentValue = EMPTY_OBJ;

    public BinderExample(T viewType)
    {
        super(viewType);
    }

    @Override
    protected IdDealResult dealId(UiId id, String prefix)
    {
        if (WPTool.isEmpty(id.getId()))
        {
            return null;
        }
        IdDealResult idDealResult = new IdDealResult();


        String[] strIds = id.getId().split("_");
        if (strIds.length == 0)
        {
            return null;
        }

        idDealResult.setIsOccur(id.getId().endsWith("_"));
        ArrayList<String> list = new ArrayList<>(strIds.length);
        if (idDealResult.isOccur())
        {

            for (int i = 0; i < strIds.length - 1; i++)
            {
                if (WPTool.notNullAndEmpty(strIds[0]))
                {
                    list.add(strIds[i]);
                }
            }

            HttpMethod httpMethod;
            if (id.getId().endsWith("__"))
            {
                try
                {
                    httpMethod = HttpMethod.valueOf(strIds[strIds.length - 1]);
                } catch (IllegalArgumentException e)
                {
                    throw e;
                }
            } else
            {
                httpMethod = HttpMethod.GET;
                list.add(strIds[strIds.length - 1]);
            }

            idDealResult.setMethod(httpMethod);
            idDealResult.setFunNames(list.toArray(new String[0]));

        } else
        {
            idDealResult.setVarName(strIds[0]);
            for (int i = 1; i < strIds.length; i++)
            {
                if (WPTool.notNullAndEmpty(strIds[0]))
                {
                    list.add(strIds[i]);
                }
            }
            idDealResult.setFunNames(list.toArray(new String[0]));
        }

        idDealResult.setPrefix(prefix);
        return idDealResult;
    }


    protected void doOnchange()
    {
        if (currentValue != EMPTY_OBJ && onValueChangedListener != null)
        {
            Object value = currentValue;
            currentValue = EMPTY_OBJ;
            String[] funs = idDealResult.getFunNames();
            doOnchange(idDealResult.getPrefix(), funs[0], idDealResult.getVarName(), value);
        }


    }

    protected void onOccur()
    {
        if (porterOccur != null)
        {
            String[] funs = idDealResult.getFunNames();
            for (int i = 0; i < funs.length; i++)
            {
                porterOccur.doPorter(idDealResult.getPrefix(), funs[i], idDealResult.getMethod());
            }
        }
    }

    private void doOnchange(String prefix, String tiedFun, String varName, Object value)
    {
        if (tiedFun != null)
        {
            onValueChangedListener
                    .onChanged(prefix, tiedFun, varName,
                            value);
        }
    }

}
