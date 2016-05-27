package com.chenyg.uibinder;

/**
 * Created by ZhuiFeng on 2015/6/11.
 */
public class UiId
{
    private String id;

    public UiId(String id, String idPrefix)
    {
        if (id == null) throw new NullPointerException("id is null!");
        this.id = id.substring(idPrefix.length());
    }

    @Override
    public String toString()
    {
        return id;
    }

    /**
     * 得到id
     *
     * @return
     */
    public String getId()
    {
        return id;
    }

    @Override
    public int hashCode()
    {
        return id.length() + id.charAt(0);
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == null || (o instanceof UiId))
        {
            return false;
        } else
        {
            UiId uiId = (UiId) o;
            return id.equals(uiId.id);
        }
    }
}
