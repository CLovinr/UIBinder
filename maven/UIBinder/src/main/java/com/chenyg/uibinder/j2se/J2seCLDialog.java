package com.chenyg.uibinder.j2se;

import com.chenyg.uibinder.C2LDialog;

import javax.swing.*;
import java.awt.*;

/**
 * Created by 宇宙之灵 on 2015/9/12.
 */
public class J2seCLDialog extends C2LDialog
{

    public Container container;
    public JDialog jDialog;

    private JButton okBtn, cancelBtn;

    public J2seCLDialog(Container container)
    {
        this.container = container;
    }

    public void setBtns(JButton cancelBtn, JButton okBtn)
    {
        this.cancelBtn = cancelBtn;
        this.okBtn = okBtn;
    }

    public void disShowOk()
    {
        if (okBtn != null)
        {
            okBtn.setVisible(false);
        }
    }

    public void disShowCancel()
    {
        if (cancelBtn != null)
        {
            cancelBtn.setVisible(false);
        }
    }
}
