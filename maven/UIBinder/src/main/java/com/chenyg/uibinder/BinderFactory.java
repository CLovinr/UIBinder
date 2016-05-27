package com.chenyg.uibinder;

import com.chenyg.wporter.util.WPTool;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by 宇宙之灵 on 2015/10/1.
 */
public class BinderFactory<T>
{

    private class Temp
    {
        HashMap<Class<? extends T>, Class<? extends Binder<T>>> binderHashMap = new HashMap<Class<? extends T>,
                Class<? extends Binder<T>>>();
        int n;

        Temp(int n)
        {
            this.n = n;
        }
    }


    private ArrayList<Temp> list = new ArrayList<Temp>();

    private Class<T> baseViewType;

    public BinderFactory(Class<T> baseViewType)
    {
        if (baseViewType == null) throw new NullPointerException();
        this.baseViewType = baseViewType;
    }

    public BinderFactory put(Class<? extends T> viewType, Class<? extends Binder<T>> binderClass)
    {
        int n = WPTool.subclassOf(viewType, baseViewType);
        // if (n == -1) throw new RuntimeException(viewType + " is not the type of " + baseViewType);
        HashMap<Class<? extends T>, Class<? extends Binder<T>>> binderHashMap;
        if (n >= list.size())
        {
            for (int i = n - list.size(); i >= 0; i--)
            {
                list.add(null);
            }
            Temp temp = new Temp(n);
            binderHashMap = temp.binderHashMap;
            list.set(n, temp);
        } else
        {
            Temp tmp = list.get(n);
            if (tmp == null)
            {
                tmp = new Temp(n);
                list.set(n, tmp);
            }
            binderHashMap = tmp.binderHashMap;
        }

        binderHashMap.put(viewType, binderClass);
        return this;
    }


    public Binder getBinder(T t)
    {
        int n = WPTool.subclassOf(t.getClass(), baseViewType);
        Binder<T> binder = null;
        if (n >= list.size())
        {
            n = list.size() - 1;
        }


        Class<?> tClass = t.getClass();
        for (int i = n; i >= 0; i--)
        {
            Temp temp = list.get(i);
            if (temp == null) continue;
            HashMap<Class<? extends T>, Class<? extends Binder<T>>> binderHashMap = temp.binderHashMap;
            Iterator<Class<? extends T>> keys = binderHashMap.keySet().iterator();
            while (keys.hasNext())
            {
                Class<? extends T> key = keys.next();
                if (WPTool.subclassOf(tClass, key) >= 0)
                {
                    try
                    {
                        Class<? extends Binder<T>> c = binderHashMap.get(key);
                        Constructor<Binder<T>>[] constructors =
                                (Constructor<Binder<T>>[]) c.getDeclaredConstructors();
                        for (Constructor<Binder<T>> con : constructors)
                        {
                            Class<?>[] cs = con.getParameterTypes();
                            if (cs.length == 1 && WPTool.subclassOf(cs[0], baseViewType) >= 0)
                            {
                                binder = con.newInstance(t);
                                break;
                            }
                        }
                    } catch (Exception e)
                    {
                        throw new RuntimeException(e);
                    }
                }

            }
            if (binder != null)
            {
                break;
            }
        }
        return binder;
    }
}
