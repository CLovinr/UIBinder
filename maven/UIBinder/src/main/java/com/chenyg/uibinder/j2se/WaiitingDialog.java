package com.chenyg.uibinder.j2se;

import javax.swing.*;
import java.awt.*;

/**
 * Created by 宇宙之灵 on 2015/9/17.
 */
class WaiitingDialog
{
    JPanel mainPanel;
    JLabel infoLabel;

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$()
    {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 0));
        final JLabel label1 = new JLabel();
        label1.setIcon(new ImageIcon(getClass().getResource("/c2l/uibinder/j2se/waitting.gif")));
        label1.setText("");
        mainPanel.add(label1, BorderLayout.CENTER);
        infoLabel = new JLabel();
        infoLabel.setText("");
        mainPanel.add(infoLabel, BorderLayout.SOUTH);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$()
    {
        return mainPanel;
    }
}
