package com.chenyg.uibinder;

import com.chenyg.wporter.base.SimpleAppValues;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;


public abstract class UIProvider
{
    private Prefix prefix;
    private ErrListener errListener;
    private static AtomicInteger atomicInteger = new AtomicInteger();
    private int id;

    /**
     * @param prefix 接口参数
     */
    public UIProvider(Prefix prefix)
    {
        setErrListener(prefix.errListener);
        this.prefix = prefix;
        id = atomicInteger.incrementAndGet();
    }

    /**
     * 得到id
     *
     * @return 该UIProvider的id
     */
    public int getId()
    {
        return id;
    }

    public Prefix getPrefix()
    {
        return prefix;
    }

    /**
     * 得到UiId
     *
     * @return
     */
    public abstract Set<UiId> getUIs();

    /**
     * 返回一个默认支持同步获取的UIAttrGetter,当然相关Binder本身必须支持同步。
     *
     * @return
     */
    public UIAttrGetter getUIAttrGetter()
    {
        return new UIAttrGetter()
        {
            @Override
            public boolean supportSync()
            {
                return true;
            }

            @Override
            public void asynGetAttrs(Listener listener, Binder[] binders, AttrEnum[] types)
            {
                Object[] values = new Object[binders.length];
                String[] names = new String[binders.length];
                for (int i = 0; i < values.length; i++)
                {
                    if(binders[i]==null){
                        continue;
                    }
                    values[i] = binders[i].get(types.length==1?types[0]:types[i]);
                    names[i] = binders[i].getIdDealResult().getVarName();
                }
                listener.onGet(new SimpleAppValues(names).values(values));
            }
        };
    }

    /**
     * 得到绑定对象
     *
     * @param uiId
     * @return
     */
    public abstract Binder getBinder(UiId uiId);

    public void setErrListener(ErrListener errListener)
    {
        this.errListener = errListener;
    }

    public ErrListener getErrListener()
    {
        return errListener;
    }

}
