package com.chenyg.uibinder;


import com.chenyg.uibinder.base.HttpMethod;


public abstract class Binder<T> implements Cloneable
{

    public interface GetBinderAttrListener
    {
        void onGet(AttrEnum attrEnum, String varName, Object value);
    }

    public static class IdDealResult
    {
        private boolean isOccur;
        private HttpMethod method;
        private String[] funNames;
        private String varName;
        /**
         * 接口前缀
         */
        private String prefix;

        @Override
        public String toString()
        {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("prefix=").append(prefix).append(",");
            if (isOccur())
            {
                stringBuilder.append("occur=true,funs:");
                for (int i = 0; i < funNames.length; i++)
                {
                    stringBuilder.append(funNames[i]).append(",");
                }
                stringBuilder.append("method=").append(method.name());
            } else
            {
                stringBuilder.append("occur=false,varName=").append(varName).append(",funs:");
                for (int i = 0; i < funNames.length; i++)
                {
                    stringBuilder.append(funNames[i]).append(",");
                }
            }
            return stringBuilder.toString();
        }

        public void setFunNames(String[] funNames)
        {
            this.funNames = funNames;
        }

        public void setVarName(String varName)
        {
            this.varName = varName;
        }

        public void setIsOccur(boolean isOccur)
        {
            this.isOccur = isOccur;
        }

        public void setPrefix(String prefix)
        {
            this.prefix = prefix;
        }

        public String getPrefix()
        {
            return prefix;
        }

        public void setMethod(HttpMethod method)
        {
            this.method = method;
        }

        public String getVarName()
        {
            return varName;
        }

        public String[] getFunNames()
        {
            return funNames;
        }

        public HttpMethod getMethod()
        {
            return method;
        }

        public boolean isOccur()
        {
            return isOccur;
        }
    }

    protected PorterOccur porterOccur;


    protected IdDealResult idDealResult;
    protected OnValueChangedListener onValueChangedListener;
    protected T viewType;
    boolean supportSync;

    public Binder(T viewType)
    {
        this.viewType = viewType;
    }


    protected abstract IdDealResult dealId(UiId id, String prefix);

    public IdDealResult getIdDealResult()
    {
        return idDealResult;
    }

    /**
     * 当初始化成功时调用。
     */
    protected void onInitOk()
    {

    }


    /**
     * 当初始化失败时调用。
     */
    protected void onInitFailed()
    {

    }

    /**
     * 设置一些绑定名等,只针对于非触发Binder
     */
    final void set(IdDealResult idDealResult, PorterOccur porterOccur)
    {
        this.idDealResult = idDealResult;
        this.porterOccur = porterOccur;
    }


    /**
     * 设置某个值
     *
     * @param attrEnum
     * @param value
     */
    public abstract void set(AttrEnum attrEnum, Object value);

    /**
     * 得到某个值
     *
     * @param attrEnum
     * @return
     */
    public abstract Object get(AttrEnum attrEnum);


    /**
     * 默认用同步方式实现。
     *
     * @param attrEnum
     * @param getBinderAttrListener
     */
    public void get(AttrEnum attrEnum, GetBinderAttrListener getBinderAttrListener)
    {
        Object val = get(attrEnum);
        getBinderAttrListener.onGet(attrEnum, getIdDealResult().varName, val);
    }

    /**
     * 是否支持同步方式获取属性值.
     *
     * @return
     */
    public boolean supportSync()
    {
        return supportSync;
    }


    /**
     * 是否是接口触发控件
     *
     * @return
     */
    public final boolean isFireBinder()
    {
        return idDealResult.isOccur();
    }

    /**
     * 释放资源
     */
    public abstract void release();

    public interface PorterOccur
    {
        void doPorter(String prefix, String tiedFun, HttpMethod httpMethod);
    }

}