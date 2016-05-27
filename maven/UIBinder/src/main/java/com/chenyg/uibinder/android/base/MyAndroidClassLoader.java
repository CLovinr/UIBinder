package com.chenyg.uibinder.android.base;

import android.app.Activity;
import com.chenyg.wporter.log.LogUtil;
import com.chenyg.wporter.util.MyClassLoader;
import dalvik.system.DexFile;

import java.io.IOException;
import java.util.*;

public class MyAndroidClassLoader extends MyClassLoader
{

    private static TreeNode rooTreeNode;
    private Activity activity;
    private String[] packages;

    class TreeNode
    {
        HashMap<String, TreeNode> children = new HashMap<String, TreeNode>(0);
        private String name;

        public TreeNode(String name)
        {
            this.name = name;
        }

        public String getName()
        {
            return name;
        }
    }

    public MyAndroidClassLoader(Activity activity)
    {
        super(Thread.currentThread().getContextClassLoader());
        this.activity = activity;
    }

    private boolean isInPackages(String className)
    {
        boolean in = packages == null;
        if (!in)
        {
            for (int i = 0; i < packages.length; i++)
            {
                if (className.startsWith(packages[i]))
                {
                    in = true;
                    break;
                }
            }
        }
        return in;
    }

    @Override
    public synchronized void seek()
    {
        if (rooTreeNode != null)
        {
            return;
        }

        String path = activity.getApplication().getPackageResourcePath();
        System.out.println("MyClassLoader.getResource()");
        System.out.println(path);
        DexFile dexFile = null;
        try
        {
            rooTreeNode = new TreeNode("");
            HashMap<String, TreeNode> rootTreeMap = rooTreeNode.children;
            dexFile = new DexFile(path);
            Enumeration<String> enumeration = dexFile.entries();
            while (enumeration.hasMoreElements())
            {
                String className = enumeration.nextElement();
                if (!isInPackages(className))
                {
                    continue;
                }
                String[] strs = className.split("\\.");
                TreeNode root = rootTreeMap.get(strs[0]);
                if (root == null)
                {
                    root = new TreeNode(strs[0]);
                    rootTreeMap.put(strs[0], root);
                }
                HashMap<String, TreeNode> map = root.children;
                for (int i = 1; i < strs.length; i++)
                {
                    String name = strs[i];
                    TreeNode node = map.get(name);
                    if (node == null)
                    {
                        node = new TreeNode(strs[i]);
                        map.put(strs[i], node);
                    }
                    map = node.children;
                }
            }

        } catch (IOException e)
        {
            e.printStackTrace();
        } finally
        {
            if (dexFile != null)
            {
                try
                {
                    dexFile.close();
                } catch (IOException e)
                {
                    // TODO Auto-generated catch block
                }
            }
        }
    }


    private TreeNode getTreeNode(String resName)
    {
        String[] strs = resName.split("\\.");
        HashMap<String, TreeNode> map = rooTreeNode.children;
        TreeNode treeNode = rooTreeNode;
        for (int i = 0; i < strs.length; i++)
        {
            treeNode = map.get(strs[i]);
            if (treeNode == null)
            {
                break;
            }
            if (i < strs.length - 1)
            {
                map = treeNode.children;
            }
        }
        return treeNode;
    }

    @Override
    public List<String> getClassNames(String packageName, boolean childPackage)
    {

        ArrayList<String> list = new ArrayList<String>();
        TreeNode treeNode = getTreeNode(packageName);

        if (treeNode != null)
        {
            if (treeNode.children.size() == 0)
            {
                list.add(packageName);
            } else
            {
                Iterator<TreeNode> iterator = treeNode.children.values().iterator();
                while (iterator.hasNext())
                {
                    TreeNode node = iterator.next();
                    String string = packageName + "." + node.getName();
                    if (node.children.size() == 0)
                    {
                        list.add(string);
                    } else if (childPackage)
                    {
                        List<String> list2 = getClassNames(string, childPackage);
                        for (int i = 0; i < list2.size(); i++)
                        {
                            list.add(list2.get(i));
                        }
                    }
                }
            }
        }

        return list;
    }

    @Override
    public void setPackages(String... packages)
    {
        String[] strs = new String[packages.length];
        for (int i = 0; i < strs.length; i++)
        {
            strs[i] = packages[i] + '.';
        }
        this.packages = strs;
    }

    @Override
    public void release()
    {
        LogUtil.printErrPosLn("no clear!");

    }

    public static void clear()
    {
        if (rooTreeNode != null)
        {
            rooTreeNode.children.clear();
            rooTreeNode = null;
        }
    }

}
