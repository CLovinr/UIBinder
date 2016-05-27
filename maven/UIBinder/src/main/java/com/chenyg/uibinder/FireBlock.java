package com.chenyg.uibinder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by 刚帅 on 2015/11/22.
 */
public class FireBlock
{

    boolean isExcept;
    private Map<String, Set<String>> map = new HashMap<>();

    /**
     * @param isExcept 是否是排除
     */
    public FireBlock(boolean isExcept)
    {
        this.isExcept = isExcept;
    }

    public FireBlock add(String porterPrefix, String... tiedFuns)
    {
        Set<String> set = map.get(porterPrefix);
        if (set == null)
        {
            set = new HashSet<>();
            map.put(porterPrefix, set);
        }
        for (int i = 0; i < tiedFuns.length; i++)
        {
            set.add(tiedFuns[i]);
        }

        return this;
    }

    /**
     * 判断是否会被触发
     * @param porterPrefix
     * @param tiedFun
     * @return
     */
    public boolean willFire(String porterPrefix, String tiedFun)
    {
        Set<String> set = map.get(porterPrefix);
        if (isExcept)
        {
            return set == null || !set.contains(tiedFun);
        } else
        {
            return set != null && set.contains(tiedFun);
        }

    }
}
