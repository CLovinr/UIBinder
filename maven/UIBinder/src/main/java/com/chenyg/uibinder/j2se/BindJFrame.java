package com.chenyg.uibinder.j2se;


import com.chenyg.uibinder.*;
import com.chenyg.uibinder.base.BinderPorter;
import com.chenyg.uibinder.base.ChooseCallback;
import com.chenyg.uibinder.j2se.binders.*;
import com.chenyg.uibinder.j2se.form.ArrayInputEdit;
import com.chenyg.uibinder.j2se.form.FileInputEdit;
import com.chenyg.wporter.a.app.AppPorterUtil;
import com.chenyg.wporter.base.AppValues;
import com.chenyg.wporter.base.SimpleAppValues;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class BindJFrame extends JFrame
{
    private static UISeeker uiSeeker;
    private Integer providerId;


    class J2seBaseUI extends BaseUI
    {

        private void close(JDialog jDialog)
        {
            jDialog.dispose();
        }

        private JDialog jDialogWaitting;
        private WaiitingDialog waiitingDialog;
        private BinderFactory<JComponent> binderFactory = new BinderFactory<JComponent>(JComponent.class);

        J2seBaseUI()
        {
            binderFactory.put(JTextComponent.class, JTextComponentBinder.class);
            binderFactory.put(JButton.class, JButtonBinder.class);
            binderFactory.put(JComboBox.class, JComboBoxBinder.class);
            binderFactory.put(JToggleButton.class, JToggleButtonBinder.class);
            binderFactory.put(FileInputEdit.class, FileInputEditBinder.class);
            binderFactory.put(ArrayInputEdit.class, ArrayInputEditBinder.class);
            binderFactory.put(JSlider.class, JSliderBinder.class);
            binderFactory.put(JSpinner.class, JSpinnerBinder.class);
            binderFactory.put(JProgressBar.class, JProgressBarBinder.class);
            binderFactory.put(JLabel.class, JLabelBinder.class);
            binderFactory.put(JTable.class, JTableBinder.class);
        }

        @Override
        public <T> BinderFactory<T> getBinderFactory(Class<T> t)
        {
            return (BinderFactory<T>) binderFactory;
        }

        @Override
        public void sendBinderData(String porterPrefix, BinderData binderData, boolean toAll)
        {
            BindJFrame.sendBinderData(porterPrefix, binderData, toAll);
        }

        @Override
        public void alert(String... contents)
        {
            StringBuilder stringBuilder = new StringBuilder();
            for (String str : contents)
            {
                stringBuilder.append(str);
            }
            JLabel jLabel = new JLabel("<html><pre>" + stringBuilder + "</pre></html>");
            alert(LangMap.getLangMap().get(LangMap.CommonStr.ALERT), jLabel, null, true, false, false, null);
        }

        @Override
        public void alert(String title, String content, final ChooseCallback chooseCallback)
        {
            JLabel jLabel = new JLabel("<html><pre>" + content + "</pre></html>");
            alert(title, jLabel, chooseCallback, true, false, false, null);
        }

        @Override
        public void toast(String content)
        {
            alert(content);
        }

        @Override
        public void popBinder()
        {
            _modalTempStack.peek().pop();
        }

        @Override
        public void waitingShow(String... contents)
        {
            final StringBuilder stringBuilder = new StringBuilder();
            for (String str : contents)
            {
                if (str != null)
                {
                    stringBuilder.append(str);
                }
            }
            if (jDialogWaitting != null)
            {
                try
                {
                    SwingUtilities.invokeAndWait(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            waiitingDialog.infoLabel.setText(stringBuilder.toString());
                            jDialogWaitting.pack();
                        }
                    });
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                } catch (InvocationTargetException e)
                {
                    e.printStackTrace();
                }

                return;
            }
            final JDialog jDialog = pushModal(true);
            //jDialog.setTitle("waitting-show");
            jDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            WaiitingDialog waiitingDialog = new WaiitingDialog();
            jDialog.setContentPane(waiitingDialog.mainPanel);
            //jDialog.setUndecorated(true);
            jDialog.setResizable(false);
            jDialog.pack();
            jDialog.setLocationRelativeTo(BindJFrame.this);
            waiitingDialog.infoLabel.setText(stringBuilder.toString());
            jDialog.doLayout();
            jDialog.setVisible(true);
            this.jDialogWaitting = jDialog;
            this.waiitingDialog = waiitingDialog;
        }

        @Override
        public void waitingDisShow()
        {
            if (jDialogWaitting != null)
            {
                close(jDialogWaitting);
                jDialogWaitting = null;
                waiitingDialog = null;
            }
        }

        private MyJDialog alert(String title, Component content, final ChooseCallback chooseCallback, boolean hasOk,
                boolean undecorated, boolean resizable, J2seCLDialog j2seCLDialog)
        {
            final MyJDialog jDialog = (MyJDialog) pushModal(true);
            jDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            if (undecorated)
                jDialog.setUndecorated(undecorated);
            jDialog.setTitle(title);
            jDialog.getContentPane().setLayout(new BorderLayout());
            AlertDialog alertDialog = new AlertDialog();
            LangMap langMap = LangMap.getLangMap();
            alertDialog.cancelButton.setText(langMap.get(LangMap.CommonStr.CANCEL));
            alertDialog.oKButton.setText(langMap.get(LangMap.CommonStr.OK));
            if (j2seCLDialog != null)
            {
                j2seCLDialog.setBtns(alertDialog.cancelButton, alertDialog.oKButton);
            }
            jDialog.getContentPane().add(alertDialog.mainPanel, "Center");
            if (chooseCallback != null)
            {
                alertDialog.oKButton.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        close(jDialog);
                        chooseCallback.onOk();
                    }
                });
//                jDialog.addWindowListener(new WindowAdapter()
//                {
//                    @Override
//                    public void windowClosing(WindowEvent e)
//                    {
//                        close(jDialog);
//                        chooseCallback.onCancel();
//                    }
//                });
                alertDialog.cancelButton.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        close(jDialog);
                        chooseCallback.onCancel();
                    }
                });
            } else
            {
                if (hasOk)
                {
                    alertDialog.cancelButton.setVisible(false);
                    alertDialog.oKButton.addActionListener(new ActionListener()
                    {
                        @Override
                        public void actionPerformed(ActionEvent e)
                        {
                            close(jDialog);
                        }
                    });
                } else
                {
                    alertDialog.btnPanel.setVisible(false);
                }

            }

            alertDialog.contentPanel.add(content, "Center");
            jDialog.setResizable(resizable);
            jDialog.pack();
            jDialog.setLocationRelativeTo(BindJFrame.this);
            jDialog.setVisible(true);


            return jDialog;
        }

        @Override
        public void alert(String title, C2LView c2LView, ChooseCallback.CallbackWithInit callbackWithInit,
                Prefix prefix,
                AppValues callbackValues)
        {

            J2seCLDialog j2seCLDialog = null;
            J2seCLView j2seCLView = (J2seCLView) c2LView;
            if (callbackWithInit != null)
            {
                j2seCLDialog = new J2seCLDialog(jDialogWaitting);
            }

            MyJDialog jDialog = alert(title, j2seCLView.container, callbackWithInit, false, false, true, j2seCLDialog);

            binderJDialog(jDialog, prefix, callbackValues);
            if (callbackWithInit != null)
            {
                j2seCLDialog.jDialog = jDialog;
                callbackWithInit.onInit(j2seCLDialog);
            }
        }

        @Override
        public void input(final boolean multiRow, final int maxLength, String title, String initTxt,
                final InputCallback inputCallback)
        {

            if (initTxt != null && initTxt.length() > maxLength)
            {
                initTxt = initTxt.substring(0, maxLength);
            }
            InputDialog inputDialog = new InputDialog();

            final JTextComponent jTextComponent;

            if (multiRow)
            {
                inputDialog.textField.setVisible(false);
                jTextComponent = inputDialog.textArea;
            } else
            {
                inputDialog.textArea.setVisible(false);
                jTextComponent = inputDialog.textField;
            }


            PlainDocument plainDocument = new PlainDocument()
            {
                @Override
                public void insertString(int offs, String str, AttributeSet a) throws BadLocationException
                {
                    if (str == null) return;
                    int dlen = maxLength - jTextComponent.getText().length();
                    str = str.substring(0, dlen > str.length() ? str.length() : dlen);
                    super.insertString(offs, str, a);
                }
            };
            jTextComponent.setDocument(plainDocument);
            final JDialog jDialog = pushModal(true);
            jDialog.setTitle(title);
            jDialog.getContentPane().setLayout(new BorderLayout());
            LangMap langMap = LangMap.getLangMap();
            inputDialog.cancelButton.setText(langMap.get(LangMap.CommonStr.CANCEL));
            inputDialog.okButton.setText(langMap.get(LangMap.CommonStr.OK));

            jDialog.getContentPane().add(inputDialog.mainPanel, "Center");

            inputDialog.okButton.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    close(jDialog);
                    if (inputCallback != null)
                    {
                        String txt = jTextComponent.getText();
                        inputCallback.onOk(txt);
                    }
                }
            });
            jDialog.addWindowListener(new WindowAdapter()
            {
                @Override
                public void windowClosing(WindowEvent e)
                {
                    close(jDialog);
                    if (inputCallback != null)
                    {
                        inputCallback.onCancel();
                    }
                }
            });
            inputDialog.cancelButton.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    close(jDialog);
                    if (inputCallback != null)
                    {
                        inputCallback.onCancel();
                    }
                }
            });

            if (initTxt != null)
            {
                jTextComponent.setText(initTxt);
            }

            if (!multiRow)
            {
                jDialog.setResizable(false);
            } else
            {
                jDialog.setResizable(true);
            }

            jDialog.setVisible(true);
            jDialog.pack();
            jDialog.setLocationRelativeTo(BindJFrame.this);
        }
    }

    /**
     * 开始自动绑定（应在控件内容设置完成后）
     */
    public void bindUI(Container container, Prefix prefix, AppValues callbackValues)
    {
        setContentPane(container);
        repaint();
        validate();
        Integer providerId = push(this, prefix, callbackValues);

        if (this.providerId == null)
        {
            this.providerId = providerId;
            bindJFrameHashMap.put(providerId, this);
        }

        _modalTempStack.push(new ModalTemp(container, providerId, prefix, callbackValues));


    }

    /**
     * 会调用{@linkplain #bindUI(Container, Prefix, AppValues)}
     *
     * @param contentPane    要显示的内容控件
     * @param prefix         接口前缀
     * @param callbackValues 绑定完成时带的回调参数
     * @param modal          是否是模态
     * @see #BindJFrame(boolean)
     */
    public BindJFrame(Container contentPane, Prefix prefix, AppValues callbackValues, final boolean modal)
    {
        this(modal);
        bindUI(contentPane, prefix, callbackValues);
    }


    private boolean isModal;

    /**
     * @param modal 是否是模态窗口。若是，则其他BindJFrame均不可操作。
     */
    public BindJFrame(boolean modal)
    {

        if (uiSeeker == null)
        {
            uiSeeker = new UISeeker(new J2seBaseUI());
            bindJFrameHashMap = new HashMap<Integer, BindJFrame>();
        }
        this.isModal = modal;
        _modalTempStack.push(modal ? new ModalTemp(this, true) : new ModalTemp(this));

        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent windowEvent)
            {
                BindJFrame.this.dispose();
            }
        });

    }

    @Override
    public void dispose()
    {
        super.dispose();
        if (providerId != null && uiSeeker != null)
        {
            uiSeeker.remove(providerId);
            if (isModal)
            {
                restoreAll();
            }
            bindJFrameHashMap.remove(providerId);
            providerId = null;
        }
        if (bindJFrameHashMap.isEmpty() && AppPorterUtil.getAppPorterMain() != null)
        {
            AppPorterUtil.getAppPorterMain().stop();
        }

    }

    /**
     * @param container      awt容器
     * @param _prefix        接口参数
     * @param callbackValues 回调参数
     * @return 返回providerId
     */
    private static int push(Container container, Prefix _prefix, AppValues callbackValues)
    {
        if (uiSeeker == null)
        {
            throw new RuntimeException("There is no " + BindJFrame.class + " instance!");
        }
        UIProvider uiProvider = new J2seUIProvider(_prefix, container);
        int providerId = uiProvider.getId();
        uiSeeker.push(uiProvider);
        if (_prefix.bindCallbackMethod != null)
        {
            SimpleAppValues simpleAppValues = new SimpleAppValues(BinderPorter.CALLBACK_VIEW,
                    BinderPorter.CALLBACK_PORTER_PREFIX)
                    .values(container, _prefix.porterPrefix).add(callbackValues);
            AppPorterUtil.getPorterObject(_prefix.porterPrefix, _prefix.bindCallbackMethod, simpleAppValues);
        }

        return providerId;
    }

    private class Temp
    {
        Integer pId = null;
        MyJDialog jDialog;
        Prefix prefix;
        AppValues appValues;


        Temp(MyJDialog _jDialog, Prefix _prefix, AppValues _appValues)
        {
            this.jDialog = _jDialog;
            _jDialog.temp = this;

            this.prefix = _prefix;
            this.appValues = _appValues;

            pId = push(jDialog, prefix, appValues);
            prefix = null;
            appValues = null;
        }

        void pop()
        {
            if (pId != null && uiSeeker != null)
            {
                uiSeeker.remove(pId);
                pId = null;
                alertDialogStack.pop();
                if (!alertDialogStack.empty())
                {
                    Temp temp = alertDialogStack.peek();
                    showAgainCallback(temp.prefix, temp.appValues, temp.jDialog);
                }
            }
        }
    }

    private static void showAgainCallback(Prefix prefix, AppValues appValues, Component component)
    {
        if (prefix.getShowAgainCallback() != null)
        {
            SimpleAppValues simpleAppValues = new SimpleAppValues(BinderPorter.CALLBACK_VIEW,
                    BinderPorter.CALLBACK_PORTER_PREFIX)
                    .values(component, prefix.porterPrefix).add(appValues);
            AppPorterUtil
                    .getPorterObject(prefix.porterPrefix, prefix.getShowAgainCallback(), simpleAppValues);
        }
    }

    /**
     * 会自动进行绑定
     *
     * @param jDialog   要绑定的对话框对象
     * @param prefix    接口参数
     * @param appValues 回调参数
     */
    private void binderJDialog(MyJDialog jDialog, Prefix prefix, AppValues appValues)
    {
        if (!(jDialog instanceof MyJDialog))
        {
            throw new RuntimeException("JDialog should from " + BindJFrame.class + ".pushModal(or pushUnModal)");
        }
        alertDialogStack.push(new Temp(jDialog, prefix, appValues));
    }


    /**
     * 发送
     *
     * @param porterPrefix 接口前缀
     * @param binderData   要发送的相关数据
     */
    private static void sendBinderData(String porterPrefix,
            BinderData binderData, boolean toAll)
    {
        if (uiSeeker == null)
            throw new RuntimeException("There is no " + BindJFrame.class);
        uiSeeker.sendBinderData(porterPrefix, binderData, toAll);
    }

    private static AtomicInteger count = new AtomicInteger();

    private class MyJDialog extends JDialog
    {
        boolean modalToAll;
        int id;
        private boolean isDisposed = false;

        Temp temp;

        MyJDialog(Window owner, boolean modal, boolean modalToAll)
        {
            super(owner);
            id = count.incrementAndGet();
            if (modal)
            {
                this.modalToAll = modalToAll;
                // super.setModal(true);
            } else
            {
                this.modalToAll = false;
                // super.setModal(false);
            }
            super.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        }


        @Override
        public void setDefaultCloseOperation(int operation)
        {

        }


        @Override
        public void dispose()
        {
            if (isDisposed) return;
            BindJFrame.this.removeDialog(this);
            if (temp != null)
            {
                temp.pop();
            }

            super.dispose();
            isDisposed = true;
        }

        public int getId()
        {
            return id;
        }

        @Override
        public void setModal(boolean modal)
        {

        }


    }

    private class ModalTemp
    {
        Container window;
        int forDiabled = 0;
        int hasForDiabledAll = 0;
        boolean unmodal;

        Integer containerPId;
        Prefix containerPrefix;
        AppValues containerAppValues;

        ModalTemp(Container container, Integer providerId, Prefix prefix, AppValues callbackValues)
        {
            this.window = container;
            this.containerPId = providerId;
            this.containerPrefix = prefix;
            this.containerAppValues = callbackValues;
        }

        ModalTemp(Window window)
        {
            this.window = window;
            unmodal = true;
        }

        ModalTemp(Window window, boolean modalToAll)
        {
            this.window = window;
            unmodal = false;
        }


        void disabled()
        {
            forDiabled++;
            if (forDiabled == 1)
            {
                window.setEnabled(false);
            }

        }

        void enabled()
        {
            forDiabled--;
            if (forDiabled <= 0)
            {
                if (hasForDiabledAll <= 0)
                {
                    window.setEnabled(true);
                }
                forDiabled = 0;
            }
        }

        void pop()
        {
            if (window instanceof Window)
            {
                ((Window) window).dispose();
            } else
            {
                _modalTempStack.pop();
                uiSeeker.remove(containerPId);
                ModalTemp mt = _modalTempStack.peek();
                if (mt.window instanceof BindJFrame)
                {
                    BindJFrame.this.dispose();
                } else if (!(mt.window instanceof Window))
                {
                    setContentPane(mt.window);
                    showAgainCallback(mt.containerPrefix, mt.containerAppValues, mt.window);
                }
            }
        }

        void forDisabledAll()
        {
            hasForDiabledAll++;
            if (hasForDiabledAll > 1)
            {
                return;
            }
            window.setEnabled(false);
        }

        void restoreForAllEnabled()
        {
            hasForDiabledAll--;
            if (hasForDiabledAll <= 0)
            {
                hasForDiabledAll = 0;
                if (forDiabled <= 0)
                {
                    window.setEnabled(true);
                }
            }
        }
    }

    private static HashMap<Integer, BindJFrame> bindJFrameHashMap;
    private Stack<ModalTemp> _modalTempStack = new Stack<ModalTemp>();
    private Stack<Temp> alertDialogStack = new Stack<>();
    private HashMap<Integer, ModalTemp> unmodalMap = new HashMap<Integer, ModalTemp>();


    private void disabledAll()
    {
        Iterator<BindJFrame> iterator = bindJFrameHashMap.values().iterator();
        while (iterator.hasNext())
        {
            BindJFrame bindJFrame = iterator.next();
            for (ModalTemp modalTemp : bindJFrame._modalTempStack)
            {
                modalTemp.forDisabledAll();
            }
        }

    }

    private void restoreAll()
    {
        Iterator<BindJFrame> iterator = bindJFrameHashMap.values().iterator();
        while (iterator.hasNext())
        {
            BindJFrame bindJFrame = iterator.next();
            for (ModalTemp modalTemp : bindJFrame._modalTempStack)
            {
                modalTemp.restoreForAllEnabled();
            }
        }
    }

    /**
     * 得到非模特对话框
     *
     * @return
     */
    public JDialog pushUnModal()
    {
        MyJDialog jDialog = new MyJDialog(this, false, false);
//        jDialog.addWindowListener(new WindowAdapter()
//        {
//            @Override
//            public void windowClosing(WindowEvent e)
//            {
//                MyJDialog myJDialog = (MyJDialog) e.getWindow();
//                removeDialog(myJDialog);
//            }
//
//        });
        ModalTemp modalTemp = new ModalTemp(jDialog);
        unmodalMap.put(jDialog.getId(), modalTemp);
        _modalTempStack.push(modalTemp);
        return jDialog;
    }

    /**
     * 得到一个模态话框对象.若已经显示了等待框，则会取消显示。
     *
     * @param toAll 是否针对所有窗口:是，则针对所有窗口（包括对话框）；否，则只针对栈顶的窗口（包括对话框）.
     * @return
     */
    public JDialog pushModal(boolean toAll)
    {
        BaseUI.getBaseUI().waitingDisShow();
        Window window = null;
        for (int i = _modalTempStack.size() - 1; i >= 0; i--)
        {
            if (_modalTempStack.get(i).window instanceof Window)
            {
                window = (Window) _modalTempStack.get(i).window;
                break;
            }
        }
        MyJDialog jDialog = new MyJDialog(window, true, toAll);
//        jDialog.addWindowListener(new WindowAdapter()
//        {
//            @Override
//            public void windowClosing(WindowEvent e)
//            {
//                MyJDialog myJDialog = (MyJDialog) e.getWindow();
//                removeDialog(myJDialog);
//            }
//
//        });

        if (toAll)
        {
            disabledAll();
        } else
        {
            _modalTempStack.peek().disabled();
        }
        _modalTempStack.push(new ModalTemp(jDialog, toAll));

        return jDialog;

    }

    //关闭对话框
    private synchronized void removeDialog(MyJDialog myJDialog)
    {

        for (int i = 1; i < _modalTempStack.size(); i++)
        {
            ModalTemp modalTemp = _modalTempStack.get(i);
            if ((modalTemp.window instanceof MyJDialog) && myJDialog.getId() == ((MyJDialog) modalTemp.window).getId())
            {
                _modalTempStack.removeElementAt(i);
                if (!modalTemp.unmodal)
                {
                    if (myJDialog.modalToAll)
                    {
                        restoreAll();
                    } else if (i == _modalTempStack.size() - 1)
                    {
                        _modalTempStack.peek().enabled();
                    }
                } else
                {
                    unmodalMap.remove(myJDialog.getId());
                }

                break;

            }
        }
    }
}
