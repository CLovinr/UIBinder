package com.chenyg.uibinder.j2se.form;

import org.json.JSONArray;
import org.json.JSONException;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.*;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 输入数组
 * Created by 宇宙之灵 on 2015/9/18.
 */
public class ArrayInputEdit extends JPanel
{
    private Stack<TextBtn> jTextFields = new Stack<TextBtn>();
    private String pattern = null;
    private JPanel contentPanel;
    private JButton addBtn;
    private OnChangeListener onChangeListener;


    public interface OnChangeListener
    {
        void onChange(JSONArray jsonArray);
    }

    private static class TextBtn
    {
        JTextField _jt;
        JButton _jb;
        JPanel jPanel;

        public TextBtn()
        {
            _jt = new JTextField();
            _jb = new JButton("<--");
            jPanel = new JPanel();
            jPanel.setLayout(new BorderLayout());
            jPanel.add(_jt, "Center");
            jPanel.add(_jb, "East");
        }
    }

    public ArrayInputEdit()
    {

        setLayout(new FlowLayout());


        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new FlowLayout());
        add(btnPanel);

        addBtn = new JButton(" + ");
        btnPanel.add(addBtn);
        addBtn.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                setOnChangeListener(pushTextEdit());
            }
        });
        contentPanel = new JPanel();
        contentPanel.setLayout(new FlowLayout());
        add(contentPanel);
        setOnChangeListener(pushTextEdit());//初始添加一个输入框
    }

    public void setOnChangeListener(OnChangeListener onChangeListener)
    {
        this.onChangeListener = onChangeListener;
    }


    /**
     * 得到正则表达式
     *
     * @return
     */
    public String getPattern()
    {
        return pattern;
    }

    /**
     * @param pattern 不需要加^和$
     */
    public void setPattern(String pattern)
    {
        this.pattern = pattern;
    }

    private class Temp
    {
        String currentValue;

        void doChange()
        {
            if (currentValue != null)
            {
                currentValue = null;
                if (onChangeListener != null)
                {
                    onChangeListener.onChange(getValue());
                }
            }
        }
    }

    private void setOnChangeListener(final JTextField jTextField)
    {
        final Temp temp = new Temp();
        jTextField.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                temp.currentValue = jTextField.getText();
            }

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                temp.currentValue = jTextField.getText();
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {

            }
        });
        jTextField.addMouseListener(new MouseInputAdapter()
        {
            @Override
            public void mouseExited(MouseEvent e)
            {
                temp.doChange();
                jTextField.setFocusable(false);
                jTextField.setFocusable(true);
            }
        });

        jTextField.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusLost(FocusEvent e)
            {
                temp.doChange();
            }
        });
    }


    private JTextField pushTextEdit()
    {
        final TextBtn tb = new TextBtn();
        jTextFields.push(tb);

        tb._jb.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                for (int i = 0; i < jTextFields.size(); i++)
                {
                    if (jTextFields.get(i).jPanel == tb.jPanel)
                    {
                        removeTextEdit(i);
                        break;
                    }
                }
            }
        });

        final JTextField jTextField = tb._jt;

        jTextField.setColumns(20);
        contentPanel.add(tb.jPanel);

        if (getParent() != null)
        {
            getParent().validate();
        }
        updateUI();

        return jTextField;
    }

    private boolean checkPattern(String txt)
    {
        if (pattern == null || txt == null || txt.equals("")) return true;
        Matcher m = Pattern.compile(pattern).matcher(txt);
        return m.find();
    }

    public void setValue(JSONArray jsonArray)
    {
        boolean has = !jTextFields.empty();
        while (!jTextFields.empty())
        {
            TextBtn tb = jTextFields.pop();
            contentPanel.remove(tb.jPanel);
        }
        try
        {
            for (int i = 0; i < jsonArray.length(); i++)
            {
                String txt = jsonArray.getString(i);
                if (checkPattern(txt))
                {
                    JTextField jtf = pushTextEdit();
                    jtf.setText(txt);
                    setOnChangeListener(jtf);
                }
            }
        } catch (JSONException e)
        {
            throw new RuntimeException(e.toString());
        }
        if (has && jsonArray.length() > 0 && onChangeListener != null)
        {
            onChangeListener.onChange(getValue());
        }
        if (getParent() != null)
        {
            getParent().validate();
        }
        updateUI();
    }

    public JSONArray getValue()
    {

        JSONArray jsonArray = new JSONArray();
        for (TextBtn tb : jTextFields)
        {
            String txt = tb._jt.getText();
            if (txt != null && !"".equals(txt))
            {
                jsonArray.put(txt);
            }
        }
        return jsonArray.length() == 0 ? null : jsonArray;
    }


    private void removeTextEdit(int index)
    {
        if (jTextFields.empty()) return;
        TextBtn tb = jTextFields.get(index);
        jTextFields.removeElementAt(index);
        JTextField jTextField = tb._jt;
        contentPanel.remove(tb.jPanel);
        if (onChangeListener != null && jTextField.getText() != null && !jTextField.getText().equals(""))
        {
            onChangeListener.onChange(getValue());
        }
        Container container = getParent();
        if (container != null) container.validate();
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);
        addBtn.setEnabled(enabled);
        for (TextBtn tb : jTextFields)
        {
            tb._jt.setEnabled(enabled);
            tb._jb.setEnabled(enabled);
        }
    }

    @Override
    public void requestFocus()
    {
        if (jTextFields.empty())
        {
            addBtn.requestFocus();
        } else
        {
            jTextFields.peek()._jt.requestFocus();
        }
    }
}
