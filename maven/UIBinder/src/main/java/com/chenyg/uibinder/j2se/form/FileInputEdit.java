package com.chenyg.uibinder.j2se.form;


import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by 宇宙之灵 on 2015/9/17.
 */
public class FileInputEdit extends JPanel
{
    private JTextField textField;
    private JButton chooseFileBtn;
    private File[] selectedFiles;
    private File lastDir;
    private OnFileChangeListener onFileChangeListener;
    private boolean multiple = false;
    private String extensionDesc;
    private String extensions;
    private boolean selectDir;

    public interface OnFileChangeListener
    {
        void onChange(File[] files);
    }

    public FileInputEdit()
    {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());
        chooseFileBtn = new JButton("...");
        jPanel.add(chooseFileBtn, "East");
        textField = new JTextField("     ");
        textField.setColumns(20);

        textField.setEditable(false);
        jPanel.add(textField, "Center");
        setLayout(new BorderLayout());
        add(jPanel, "Center");
        chooseFileBtn.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JFileChooser jFileChooser = new JFileChooser(lastDir);
                if (extensions != null) jFileChooser.setFileFilter(
                        new FileNameExtensionFilter(extensionDesc == null ? "" : extensionDesc, extensions.split(";")));
                jFileChooser
                        .setFileSelectionMode(isSelectDir() ? JFileChooser.DIRECTORIES_ONLY : JFileChooser.FILES_ONLY);
                jFileChooser.setMultiSelectionEnabled(isMultiple());
                int n = jFileChooser.showOpenDialog(FileInputEdit.this);
                lastDir = jFileChooser.getCurrentDirectory();
                if (n == JFileChooser.APPROVE_OPTION)
                {
                    if (isMultiple())
                    {
                        File[] files = jFileChooser.getSelectedFiles();
                        if (onFileChangeListener != null)
                        {
                            int p = files == null ? 0 : files.length;
                            int m = selectedFiles == null ? 0 : selectedFiles.length;
                            if (p != m)
                            {
                                onFileChangeListener.onChange(files);
                            }
                        }
                        selectedFiles = files;
                    } else
                    {
                        File file = jFileChooser.getSelectedFile();
                        if (file != null)
                        {
                            if (selectedFiles != null && (file == selectedFiles[0]))
                            {
                                return;
                            }
                            selectedFiles = new File[]{file};
                            textField.setText(file.getName());
                            Container container = getParent();
                            if (container != null) container.validate();
                            updateUI();
                            if (onFileChangeListener != null)
                            {
                                onFileChangeListener.onChange(selectedFiles);
                            }
                        }

                    }

                }
            }
        });
    }

    /**
     * 是否为选择目录
     *
     * @return
     */
    public boolean isSelectDir()
    {
        return selectDir;
    }

    public void setSelectDir(boolean selectDir)
    {
        this.selectDir = selectDir;
    }

    /**
     * @param extensions 后缀名，不含".",用分号隔开
     */
    public void setExtensions(String extensions)
    {
        this.extensions = extensions;
    }

    /**
     * 得到后缀名,不含".",用分号隔开
     *
     * @return
     */
    public String getExtensions()
    {
        return extensions;
    }

    /**
     * 设置文件过滤的描述
     *
     * @param extensionDesc
     */
    public void setExtensionDesc(String extensionDesc)
    {
        this.extensionDesc = extensionDesc;
    }

    /**
     * 得到文件过滤的描述
     *
     * @return
     */
    public String getExtensionDesc()
    {
        return extensionDesc;
    }

    /**
     * 是否是多文件选择
     *
     * @return
     */
    public boolean isMultiple()
    {
        return multiple;
    }

    public void setMultiple(boolean multiple)
    {
        this.multiple = multiple;
        if (!multiple && selectedFiles != null && selectedFiles.length > 1)
        {
            selectedFiles = new File[]{selectedFiles[0]};
            textField.setText(selectedFiles[0].getName());
        }
    }

    public void setOnFileChangeListener(OnFileChangeListener onFileChangeListener)
    {
        this.onFileChangeListener = onFileChangeListener;
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);
        chooseFileBtn.setEnabled(enabled);
    }


    public void setFiles(File[] files)
    {
        if (files == null)
        {
            files = new File[0];
        }
        if (!isMultiple() && files.length > 1)
        {
            files = new File[]{files[0]};
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < files.length; i++)
        {
            stringBuilder.append(files[i].getName()).append(";");
        }
        if (stringBuilder.length() > 0) stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        textField.setText(stringBuilder.toString());
        selectedFiles = files;
    }

    public File[] getFiles()
    {
        return selectedFiles;
    }

    @Override
    public void requestFocus()
    {
        chooseFileBtn.requestFocus();
    }

    public void setLastDir(File lastDir)
    {
        this.lastDir = lastDir;
    }
}
