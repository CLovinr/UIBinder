package com.chenyg.uibinder.j2se.binders;

import com.chenyg.uibinder.AttrEnum;
import com.chenyg.uibinder.OnValueChangedListener;
import com.chenyg.uibinder.j2se.J2seBinder;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import java.awt.event.*;

/**
 * Created by ZhuiFeng on 2015/9/9.
 */
public class JTextComponentBinder extends J2seBinder {
    private JTextComponent jTextComponent;

    public JTextComponentBinder(JTextComponent jTextComponent) {
        super(jTextComponent);
        this.jTextComponent = jTextComponent;
    }

    private DocumentListener documentListener;
    private MouseListener mouseListener;
    private FocusListener focusListener;

    private void setOnValueChangedListener(
            final OnValueChangedListener onValueChangedListener) {
        this.onValueChangedListener=onValueChangedListener;
        if (documentListener == null) {
            documentListener = new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent documentEvent) {
                    currentValue = jTextComponent.getText();
                }

                @Override
                public void removeUpdate(DocumentEvent documentEvent) {
                    currentValue = jTextComponent.getText();
                }

                @Override
                public void changedUpdate(DocumentEvent documentEvent) {

                }


            };
            mouseListener = new MouseInputAdapter() {

                @Override
                public void mouseExited(MouseEvent e) {
                    doOnchange();
                    jTextComponent.setFocusable(false);
                    jTextComponent.setFocusable(true);
                }
            };
            focusListener = new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    doOnchange();
                }
            };
        }
        Document document = jTextComponent.getDocument();
        document.removeDocumentListener(documentListener);
        document.addDocumentListener(documentListener);
        jTextComponent.removeMouseListener(mouseListener);
        jTextComponent.addMouseListener(mouseListener);
        jTextComponent.removeFocusListener(focusListener);
        jTextComponent.addFocusListener(focusListener);

    }


    @Override
    public void release() {
        Document document = jTextComponent.getDocument();
        document.removeDocumentListener(documentListener);
        jTextComponent = null;
    }

    @Override
    public void set(AttrEnum attrEnum, Object value) {
        if (AttrEnum.ATTR_VALUE == attrEnum) {
            jTextComponent.setText(value == null ? "" : value + "");
        } else if (AttrEnum.ATTR_VALUE_CHANGE_LISTENER == attrEnum
                && value != null
                && (value instanceof OnValueChangedListener)) {
            OnValueChangedListener
                    onValueChangedListener =
                    (OnValueChangedListener) value;
            setOnValueChangedListener(onValueChangedListener);
        } else {
            super.set(attrEnum, value);
        }
    }

    @Override
    public Object get(AttrEnum attrEnum) {
        if (AttrEnum.ATTR_VALUE == attrEnum) {
            return jTextComponent.getText();
        }
        return null;
    }


}
