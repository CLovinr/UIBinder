package com.chenyg.uibinder;

import com.chenyg.wporter.base.JResponse;
import com.chenyg.wporter.base.ResultCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhuiFeng on 2015/6/12.
 */
public class BinderData
{

    public class Task
    {
        public Object data;
        public AttrEnum method;

        public Task(AttrEnum method, Object data)
        {
            this.method = method;
            this.data = data;
        }
    }

    class GetTask
    {
        public BinderGetListener binderGetListener;
        public List<BinderGet> binderGets;

        public GetTask(BinderGetListener binderGetListener, List<BinderGet> binderGets)
        {
            this.binderGetListener = binderGetListener;
            this.binderGets = binderGets;
        }
    }

    private ArrayList<Task> list = new ArrayList<Task>(1);

    public BinderData()
    {

    }


    /**
     * @param method
     * @param data
     * @return 返回自己
     */
    private BinderData addTask(AttrEnum method, Object data)
    {
        list.add(new Task(method, data));
        return this;
    }

    /**
     * 用于获取值
     *
     * @param binderGetListener
     * @param binderGets
     * @return
     */
    public List<BinderGet> addGetListener(BinderGetListener binderGetListener, BinderGet... binderGets)
    {
        if (binderGetListener == null)
        {
            throw new NullPointerException();
        }
        List<BinderGet> list = new ArrayList<BinderGet>(5);
        for (BinderGet binderGet : binderGets)
        {
            list.add(binderGet);
        }

        GetTask getTask = new GetTask(binderGetListener, list);
        addTask(AttrEnum.METHOD_GET, getTask);

        return list;
    }

    public int size(){
        return list.size();
    }

    public List<Task> getTasks()
    {
        return list;
    }

    /**
     * 添加异步设置任务。
     *
     * @param receiver
     * @return
     */
    public BinderData addAsynSetTask(AsynSetListener.Receiver receiver)
    {
        return addTask(AttrEnum.METHOD_ASYN_SET, receiver);
    }

    /**
     * 添加设置任务.
     *
     * @param binderSets
     * @return 返回的对象可以用于继续添加
     */
    public List<BinderSet> addSetTask(BinderSet... binderSets)
    {
        List<BinderSet> list = new ArrayList<BinderSet>();
        for (BinderSet binderSet : binderSets)
        {
            list.add(binderSet);
        }

        addTask(AttrEnum.METHOD_SET, list);
        return list;
    }

    public JResponse toResponse()
    {
        JResponse jResponse = new JResponse();
        jResponse.setCode(ResultCode.SUCCESS);
        jResponse.setResult(this);
        return jResponse;
    }

}
