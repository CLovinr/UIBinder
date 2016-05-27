package com.chenyg.uibinder.j2se.binders;

import com.chenyg.uibinder.AttrEnum;
import com.chenyg.uibinder.OnValueChangedListener;
import com.chenyg.uibinder.j2se.form.FileInputEdit;
import com.chenyg.uibinder.j2se.J2seBinder;

import java.io.File;


/**
 * Created by 宇宙之灵 on 2015/9/17.
 */
public class FileInputEditBinder extends J2seBinder
{

    private FileInputEdit fileInputEdit;
    private FileInputEdit.OnFileChangeListener onFileChangeListener;

    public FileInputEditBinder(FileInputEdit fileInputEdit)
    {
        super(fileInputEdit);
        this.fileInputEdit = fileInputEdit;
    }


    @Override
    public void release()
    {
        super.release();
        fileInputEdit.setOnFileChangeListener(null);
        fileInputEdit = null;
    }

    private void setOnValueChangedListener(final OnValueChangedListener listener)
    {
        super.onValueChangedListener = listener;
        if (onFileChangeListener == null)
        {
            onFileChangeListener = new FileInputEdit.OnFileChangeListener()
            {
                @Override
                public void onChange(File[] files)
                {
                    currentValue = files;
                    doOnchange();
                }
            };
        }
        fileInputEdit.setOnFileChangeListener(onFileChangeListener);
    }

    /**
     * @param attrEnum
     * @param value
     */
    @Override
    public void set(AttrEnum attrEnum, Object value)
    {
        if (AttrEnum.ATTR_VALUE == attrEnum)
        {
            File[] files;
            if (value != null && (value instanceof File))
            {
                files = new File[]{(File) value};
            } else
            {
                files = (File[]) value;
            }
            fileInputEdit.setFiles(files);
        } else if (attrEnum == AttrEnum.ATTR_VALUE_OTHER)
        {
            File file = (File) value;
            fileInputEdit.setLastDir(file);
        } else if (AttrEnum.ATTR_VALUE_CHANGE_LISTENER == attrEnum
                && value != null
                && (value instanceof OnValueChangedListener))
        {
            OnValueChangedListener
                    onValueChangedListener =
                    (OnValueChangedListener) value;
            setOnValueChangedListener(onValueChangedListener);
        } else
        {
            super.set(attrEnum, value);
        }
    }

    /**
     * @param attrEnum
     * @return spinner.getSelectedItem
     */
    @Override
    public Object get(AttrEnum attrEnum)
    {
        if (AttrEnum.ATTR_VALUE == attrEnum)
        {
            return fileInputEdit.getFiles();
        }
        return null;
    }


}
