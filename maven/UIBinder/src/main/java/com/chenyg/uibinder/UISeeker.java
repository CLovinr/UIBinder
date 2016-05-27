package com.chenyg.uibinder;


import com.chenyg.uibinder.base.HttpMethod;
import com.chenyg.uibinder.simple.SimpleDealtPrefix;
import com.chenyg.wporter.a.app.AppPorterUtil;
import com.chenyg.wporter.base.*;
import com.chenyg.wporter.log.LogUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 扫描器
 * Created by ZhuiFeng on 2015/6/11.
 */
public class UISeeker implements BinderDataSender
{

    private static class Temp
    {
        Temp(Binder binder, String name)
        {
            this.binder = binder;
            this.name = name;
        }

        @Override
        public int hashCode()
        {
            return name.length() + name.charAt(0);
        }

        @Override
        public boolean equals(Object o)
        {
            if (o != null && (o instanceof Temp))
            {
                Temp temp = (Temp) o;
                return name.equals(temp.name);
            } else
            {
                return false;
            }
        }

        public void release()
        {
            name = null;
            if (binder != null)
            {
                binder.release();
                binder = null;
            }
        }

        Binder binder;
        String name;
    }

    private class NamesOccurStore
    {
        //<接口方法绑定名，<参数名，Temp>>
        private HashMap<String, HashMap<String, Temp>>
                namesMap =
                new HashMap<String, HashMap<String, Temp>>(5);
        private HashMap<String, Temp> occursMap = new HashMap<String, Temp>(3);
        private ErrListener errListener;
        private UIAttrGetter uiAttrGetter;
        private int id;

        public NamesOccurStore(UIProvider uiProvider)
        {
            this.uiAttrGetter = uiProvider.getUIAttrGetter();
            this.id = uiProvider.getId();
            errListener = uiProvider.getErrListener();
            Iterator<UiId> iterator = uiProvider.getUIs().iterator();
            while (iterator.hasNext())
            {
                UiId uiId = iterator.next();
                Binder binder = uiProvider.getBinder(uiId);
                binder.supportSync = uiAttrGetter.supportSync();
                seek(uiId, binder, uiProvider.getPrefix());
            }
        }

        /**
         * 清理
         */
        public void clear()
        {
            Iterator<Temp> iterator = occursMap.values().iterator();
            while (iterator.hasNext())
            {
                iterator.next().release();
            }
            Iterator<HashMap<String, Temp>>
                    iterator1 =
                    namesMap.values().iterator();
            while (iterator1.hasNext())
            {
                Iterator<Temp> iterator2 = iterator1.next().values().iterator();
                while (iterator2.hasNext())
                {
                    Temp temp = iterator2.next();
                    temp.release();
                }
            }
            namesMap.clear();
            occursMap.clear();
        }

        private void addBinder(String prefix, String tiedFun, String paramName,
                Binder binder)
        {
            if (tiedFun == null)
                return;
            HashMap<String, Temp> map = namesMap.get(tiedFun);
            if (map == null)
            {
                map = new HashMap<String, Temp>();
                namesMap.put(tiedFun, map);
            }
            map.put(paramName, new Temp(binder, paramName));
        }

        private void seek(UiId id, Binder binder, final Prefix prefix)
        {
            Binder.IdDealResult idDealResult = binder.dealId(id, prefix.porterPrefix);

            if (idDealResult == null)
            {
                binder.onInitFailed();
                return;
            }

            if (idDealResult.isOccur())
            {

                Binder.PorterOccur porterOccur = new Binder.PorterOccur()
                {
                    @Override
                    public void doPorter(String prefixStr, String tiedFun, HttpMethod httpMethod)
                    {
                        if (fireBlock != null && !fireBlock.willFire(prefixStr, tiedFun))
                        {
                            return;
                        }

                        onOccur(prefixStr, tiedFun, httpMethod);

                    }

                    private void onLocalOccur(final String prefixStr, final String tiedFun, final HttpMethod httpMethod)
                    {
                        Binder[] binders = nameBindersOfTiedFun(tiedFun);
                        uiAttrGetter.asynGetAttrs(new UIAttrGetter.Listener()
                        {
                            @Override
                            public void onGet(AppValues appValues)
                            {
                                Object obj = AppPorterUtil.getPorterObject(prefixStr, tiedFun, appValues,
                                        RequestMethod.valueOf(httpMethod.name()));
                                if (obj != null && (obj instanceof JResponse))
                                {
                                    JResponse jr = (JResponse) obj;
                                    if (jr.getCode() != ResultCode.SUCCESS)
                                    {
                                        if (errListener != null)
                                        {
                                            BinderData
                                                    binderData =
                                                    errListener.onErr(jr, prefixStr, tiedFun);
                                            if (binderData != null)
                                            {
                                                doResponse(prefixStr, binderData.toResponse());
                                            }
                                        }

                                    } else
                                    {
                                        doResponse(prefixStr, jr);
                                    }
                                }
                            }
                        }, binders, AttrEnum.ATTR_VALUE);
                    }

                    private void onOccur(final String prefixStr, final String tiedFun, final HttpMethod httpMethod)
                    {
                        do
                        {
                            if (!(prefix instanceof SimpleDealtPrefix))
                            {
                                break;
                            }
                            final SimpleDealtPrefix simpleDealtPrefix = (SimpleDealtPrefix) prefix;
                            if (simpleDealtPrefix.inExcept(tiedFun))
                            {
                                break;
                            }
                            //自动转发到其他地方处理

                            Binder[] binders = nameBindersOfTiedFun(tiedFun);
                            uiAttrGetter.asynGetAttrs(new UIAttrGetter.Listener()
                            {
                                @Override
                                public void onGet(AppValues appValues)
                                {
                                    simpleDealtPrefix.doOccur(prefixStr, tiedFun, appValues, httpMethod);
                                }
                            }, binders, AttrEnum.ATTR_VALUE);
                            return;

                        } while (false);

                        //本地接口处理
                        onLocalOccur(prefixStr, tiedFun, httpMethod);
                    }
                };

                binder.set(idDealResult, porterOccur);
                String[] funs = idDealResult.getFunNames();
                for (int i = 0; i < funs.length; i++)
                {
                    occursMap.put(funs[i], new Temp(binder, funs[i]));
                }
            } else
            {
                binder.set(idDealResult, null);

                String[] funs = idDealResult.getFunNames();
                for (int i = 0; i < funs.length; i++)
                {
                    String tiedFun = funs[i];
                    addBinder(prefix.porterPrefix, tiedFun, idDealResult.getVarName(), binder);
                }

            }

            binder.onInitOk();

        }


        private Binder[] nameBindersOfTiedFun(String tiedFun)
        {
            HashMap<String, Temp> map = namesMap.get(tiedFun);
            Binder[] binders;
            if (map != null)
            {
                binders = new Binder[map.size()];
                Iterator<Temp> iterator = map.values().iterator();
                int i = 0;
                while (iterator.hasNext())
                {
                    Temp temp = iterator.next();
                    binders[i++] = temp.binder;
                }

            } else
            {
                binders = new Binder[0];
            }
            return binders;
        }

//        private AppValues getParamValues(String tiedFun) throws GetTimeOutException
//        {
//            HashMap<String, Temp> map = namesMap.get(tiedFun);
//            String[] names;
//            Object[] values;
//            if (map != null)
//            {
//                names = new String[map.size()];
//                values = new Object[map.size()];
//                Iterator<Temp> iterator = map.values().iterator();
//                int i = 0;
//                while (iterator.hasNext())
//                {
//                    Temp temp = iterator.next();
//                    names[i] = temp.name;
//                    values[i++] = temp.binder.get(AttrEnum.ATTR_VALUE);
//                }
//
//            } else
//            {
//                names = new String[0];
//                values = new Object[0];
//            }
//            return new SimpleAppValues(names).values(values);
//        }


        /**
         * @param prefix    接口前缀
         * @param jResponse
         */
        private void doResponse(String prefix,
                JResponse jResponse)
        {
            try
            {
                BinderData binderData = (BinderData) jResponse.getResult();
                List<BinderData.Task> list = binderData.getTasks();
                for (int i = 0; i < list.size(); i++)
                {
                    BinderData.Task task = list.get(i);
                    if (AttrEnum.METHOD_SET == task.method)
                    {
                        List<BinderSet> _list = (List<BinderSet>) task.data;
                        setValue(prefix, _list);
                    } else if (AttrEnum.METHOD_ASYN_SET == task.method)
                    {
                        doAsynSetValue(task);
                    } else if (AttrEnum.METHOD_GET == task.method)
                    {
                        doGetValue(task);
                    }
                }

            } catch (Exception e)
            {
                if (errListener != null)
                {
                    errListener.onException(e, prefix);
                }
            }
        }

        /**
         * 用于处理获取值
         *
         * @param task
         */
        private void doGetValue(BinderData.Task task)
        {
            final BinderData.GetTask getTask = (BinderData.GetTask) task.data;
            List<BinderGet> list = getTask.binderGets;

            Binder[] binders = new Binder[list.size()];
            AttrEnum[] types = new AttrEnum[list.size()];

            for (int i = 0; i < binders.length; i++)
            {
                BinderGet binderGet = list.get(i);
                HashMap<String, Temp>
                        hashMap =
                        this.namesMap.get(binderGet.tiedFunName);

                Temp temp = hashMap == null ? null : hashMap.get(binderGet.paramName);
                if (temp == null)
                {
                    temp = occursMap.get(binderGet.paramName);
                }
                if (temp != null)
                {
                    binders[i] = temp.binder;
                    types[i] = binderGet.varType;
                }
            }

            uiAttrGetter.asynGetAttrs(new UIAttrGetter.Listener()
            {
                @Override
                public void onGet(AppValues appValues)
                {
                    getTask.binderGetListener.onGet(appValues.getValues());
                }
            }, binders, types);
        }

        private void doAsynSetValue(BinderData.Task task)
        {
            AsynSetListener.Receiver
                    receiver =
                    (AsynSetListener.Receiver) task.data;
            receiver.receive(new AsynSetListener()
            {
                @Override
                public void toSet(String prefix, List<BinderSet> list)
                {
                    setValue(prefix, list);
                }
            });
        }

        private void setValue(String prefix, List<BinderSet> list)
        {

            for (int i = 0; i < list.size(); i++)
            {
                BinderSet binderSet = list.get(i);
                HashMap<String, Temp>
                        hashMap =
                        this.namesMap.get(binderSet.tiedFunName);
                Temp
                        temp =
                        hashMap == null ?
                                null :
                                hashMap.get(binderSet.paramName);
                if (temp != null)
                {
                    temp.binder.set(binderSet.attrEnum, binderSet.value);
                } else
                {
                    temp = occursMap.get(binderSet.paramName);
                    if (temp != null)
                    {
                        temp.binder.set(binderSet.attrEnum, binderSet.value);
                    }
                }
            }
        }
    }

    private Stack<NamesOccurStore> stack = new Stack<NamesOccurStore>();
    private HashMap<Integer, Integer> idsMap = new HashMap<Integer, Integer>();
    private FireBlock fireBlock;

    /**
     * @param baseUI 用于设置全局的
     */
    public UISeeker(BaseUI baseUI)
    {
        if (baseUI != null)
        {
            BaseUI.setBaseUI(baseUI);
        }
    }


    /**
     * 发送BinderData到栈顶Provider（如果不为空的话）,用于主动设置控件或得到控件的相关属性值。
     *
     * @param porterPrefix 接口前缀
     * @param binderData
     */
    @Override
    public void sendBinderData(String porterPrefix,
            BinderData binderData, boolean toAll)
    {
        if (stack.empty())
        {
            return;
        }
        if (toAll)
        {
            for (int i = 0; i < stack.size(); i++)
            {
                NamesOccurStore namesOccurStore = stack.get(i);
                namesOccurStore.doResponse(porterPrefix, binderData.toResponse());
            }
        } else
        {
            NamesOccurStore namesOccurStore = stack.peek();
            namesOccurStore.doResponse(porterPrefix, binderData.toResponse());
        }

    }

    /**
     * 压入UIProvider到栈顶
     *
     * @param provider
     */
    public void push(UIProvider provider)
    {
        Integer index = getTemp2Index(provider.getId());

        if (index != null)
        {
            stack.remove(index);
        }
        NamesOccurStore namesOccurStore = new NamesOccurStore(provider);
        stack.push(namesOccurStore);
        idsMap.put(provider.getId(), stack.size() - 1);
    }

    /**
     * 弹出栈顶的UIProvider
     */
    public void pop()
    {
        if (!stack.empty())
        {
            NamesOccurStore namesOccurStore = stack.pop();
            idsMap.remove(namesOccurStore.id);
            namesOccurStore.clear();
        }
    }

    private Integer getTemp2Index(int providerId)
    {
        Integer index = idsMap.get(providerId);
        return index;
    }

    /**
     * 移除
     *
     * @param providerId
     */
    public void remove(int providerId)
    {
        Integer index = getTemp2Index(providerId);
        if (index != null)
        {
            stack.removeElementAt(index);
            idsMap.remove(providerId);
        }
    }

    /**
     * 清除所有的
     */
    public void clear()
    {
        NamesOccurStore[] temp2s = stack.toArray(new NamesOccurStore[0]);
        stack.clear();
        for (NamesOccurStore namesOccurStore : temp2s)
        {
            namesOccurStore.clear();
        }
        idsMap.clear();
    }


    public void setFireBlock(FireBlock fireBlock)
    {
        this.fireBlock = fireBlock;
    }

    public void unsetFireBlock()
    {
        this.fireBlock = null;
    }
}
