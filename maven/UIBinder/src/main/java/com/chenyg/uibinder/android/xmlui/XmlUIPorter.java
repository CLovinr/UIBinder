package com.chenyg.uibinder.android.xmlui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.*;
import android.widget.*;
import com.chenyg.uibinder.BaseUI;
import com.chenyg.uibinder.C2LDialog;
import com.chenyg.uibinder.android.AndroidC2LDialog;
import com.chenyg.uibinder.android.base.AppMain;
import com.chenyg.uibinder.base.ChooseCallback;
import com.chenyg.uibinder.LangMap;
import com.chenyg.wporter.WPObject;
import com.chenyg.wporter.WebPorter;
import com.chenyg.wporter.a.app.AppPorterUtil;
import com.chenyg.wporter.annotation.ChildIn;
import com.chenyg.wporter.annotation.FatherIn;
import com.chenyg.wporter.base.CheckType;
import com.chenyg.wporter.base.JResponse;
import com.chenyg.wporter.base.ResultCode;
import com.chenyg.wporter.base.SimpleAppValues;
import com.chenyg.wporter.log.LogUtil;
import com.chenyg.wporter.util.WPTool;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;


@FatherIn(tiedName = "xmlui/", useClassName = false)
public class XmlUIPorter extends WebPorter
{

    public static final String LAYOUT_CLASS = "layout", ID_CLASS = "id", STRING_CLASS = "string";
    static final String WAITTING_LAYOUT = "xmlui_waiting", INPUT_LAYOUT = "xmlui_input";
    static final String EDIT_TEXT_NAME = "xmlui_editText";
    private static AlertDialog waitingDialog;
    private static AtomicInteger waitingCount = new AtomicInteger(0);

    private static OnWaitingBack onWaitingBack;

    public XmlUIPorter()
    {
    }


    public static void setOnWaitingBack(OnWaitingBack onWaitingBack)
    {
        XmlUIPorter.onWaitingBack = onWaitingBack;
    }

    /**
     * 显示等待框
     */
    public static void waitingShow(String info)
    {
        waitingCount.incrementAndGet();
        if (waitingDialog != null)
        {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(AppMain.getActivity());
        View view;
        TextView infoTView;
        try
        {
            view = getView(getLayout(WAITTING_LAYOUT), AppMain.getActivity());
            infoTView = (TextView) view.findViewById(getId("xmlui_waitting_textView"));
        } catch (Exception e)
        {
            LogUtil.printErrPosLn(e.toString());
            LinearLayout linearLayout = new LinearLayout(AppMain.getActivity());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setGravity(Gravity.CENTER);

            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            linearLayout.setLayoutParams(layoutParams);

            ProgressBar progressBar = new ProgressBar(AppMain.getActivity());
            linearLayout.addView(progressBar, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            infoTView = new TextView(AppMain.getActivity());
            linearLayout.addView(infoTView);
            view = linearLayout;
        }
        builder.setView(view);
        if (WPTool.isNullOrEmpty(info))
        {
            infoTView.setVisibility(View.GONE);
        } else
        {
            infoTView.setText(info);
        }

        waitingDialog = builder.create();
        waitingDialog.setOnKeyListener(new DialogInterface.OnKeyListener()
        {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
            {

                if (keyCode == KeyEvent.KEYCODE_BACK && event
                        .getAction() == KeyEvent.ACTION_UP && onWaitingBack != null)
                {
                    onWaitingBack.onBack();
                }
                return true;
            }
        });
        waitingDialog.show();

    }

    /**
     * 取消显示等待框
     */
    public static void waitingDisShow()
    {
        if (waitingDialog != null && waitingCount.get() > 0)
        {
            if (waitingCount.decrementAndGet() > 0)
            {
                return;
            }
            waitingDialog.dismiss();
            waitingDialog = null;
        }
    }

    private static String getClassR()
    {
        return AppMain.getClassR().getName();
    }

    private Activity getActivity()
    {
        return AppMain.getActivity();
    }


    @ChildIn(tiedName = "getString", unneceParams = {"name", "id"}, checkType = CheckType.NONE)
    public JResponse getString(WPObject wpObject)
    {
        JResponse jResponse = new JResponse();
        try
        {
            Integer id;
            String name = (String) wpObject.cus[0];
            if (name != null)
            {
                id = get(name, STRING_CLASS);
            } else
            {
                id = (Integer) wpObject.cus[1];
            }
            String value = null;
            if (id != null)
            {
                try
                {
                    Resources resources = getActivity().getResources();
                    value = resources.getString(id);
                } catch (Exception e)
                {

                }

            }
            if (value != null)
            {
                jResponse.setCode(ResultCode.SUCCESS);
                jResponse.setResult(value);
            } else
            {
                jResponse.setCode(ResultCode.OK_BUT_FAILED);
                jResponse.setDescription("not find name or id!");
            }

        } catch (Exception e)
        {
            jResponse.setCode(ResultCode.SERVER_EXCEPTION);
            jResponse.setDescription(e.toString());
            jResponse.setExCause(e);
        }
        return jResponse;
    }


    @ChildIn(tiedName = "getView", unneceParams = {"layoutId", "layoutName"}, checkType = CheckType.NONE)
    public JResponse getView(WPObject wpObject)
    {

        Object[] cus = wpObject.cus;
        Integer layoutId = null;
        if (cus[0] != null)
        {
            layoutId = (Integer) cus[0];

        } else if (cus[1] != null)
        {
            String layoutName = (String) cus[1];
            layoutId = getLayout(layoutName);
        }
        JResponse jResponse = new JResponse();
        if (layoutId != null)
        {
            View view = getView(layoutId, getActivity());

            jResponse.setResult(view);
            jResponse.setCode(ResultCode.SUCCESS);
        } else
        {
            jResponse.setCode(ResultCode.OK_BUT_FAILED);
            jResponse.setDescription("not found xml!");
        }
        return jResponse;
    }


    /**
     * 得到view
     *
     * @param name layout的名称
     * @param id   layout的id值
     * @return 返回view或null
     */
    public static View getView(String name, Integer id)
    {
        JResponse jResponse = (JResponse) AppPorterUtil.getPorterObject(XmlUIEnum.FTied, XmlUIEnum.getView,
                new SimpleAppValues("layoutId", "layoutName").values(id, name));
        return (View) jResponse.getResult();
    }

    /**
     * 得到view
     *
     * @param id layout的id
     * @return 返回view
     */
    private static View getView(int id, Context context)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(id, null);
        return view;
    }


    /**
     * 提示信息
     *
     * @param content 显示的内容
     */
    public static void alert(String content)
    {
        AppPorterUtil.getPorterObject(XmlUIEnum.FTied, XmlUIEnum.alert,
                new SimpleAppValues("content", "contentType", "needConfirm").values(content, 2, 1));
    }

    /**
     * 提示信息
     *
     * @param content        显示的内容
     * @param chooseCallback 回调函数
     */
    public static void alert(String content, ChooseCallback chooseCallback)
    {
        AppPorterUtil.getPorterObject(XmlUIEnum.FTied, XmlUIEnum.alert,
                new SimpleAppValues("content", "contentType", "needConfirm", "chooseCallback")
                        .values(content, 2, 2, chooseCallback));
    }

    /**
     * 提示信息
     *
     * @param title          标题
     * @param content        要显示的内容
     * @param chooseCallback 回调函数
     */
    public static void alert(String title, String content, ChooseCallback chooseCallback)
    {
        AppPorterUtil.getPorterObject(XmlUIEnum.FTied, XmlUIEnum.alert,
                new SimpleAppValues("title", "content", "contentType", "needConfirm", "chooseCallback")
                        .values(title, content, 2, 2, chooseCallback));
    }


    /**
     * 得到字符串
     *
     * @param name 字符串在string中申明的名称
     * @param id   字符串对应的id
     * @return 返回字符串或null。
     */
    public static String getString(String name, Integer id)
    {
        JResponse jResponse = (JResponse) AppPorterUtil.getPorterObject(XmlUIEnum.FTied, XmlUIEnum.getString,
                new SimpleAppValues("id", "name").values(id, name));
        return (String) jResponse.getResult();
    }


    /**
     * 得到字符串
     *
     * @param name       字符串在string中申明的名称
     * @param id         字符串对应的id
     * @param defaultStr 未找到时返回的字符串
     * @return 返回字符串或null。
     */
    public static String getString(String name, Integer id, String defaultStr)
    {
        String str = getString(name, id);

        return str == null ? defaultStr : str;
    }


    /**
     * @param view           要显示的view
     * @param chooseCallback 回调函数
     * @param title          标题
     */
    public static void alert(View view, ChooseCallback chooseCallback, String title)
    {
        AppPorterUtil.getPorterObject(XmlUIEnum.FTied, XmlUIEnum.alert,
                new SimpleAppValues("content", "contentType", "needConfirm", "chooseCallback", "title")
                        .values(view, 3, 2, chooseCallback, title));

    }

    @ChildIn(tiedName = "alert", neceParams = {"content", "needConfirm", "contentType"}, unneceParams = {"title",
            "chooseCallback"}, checkType = CheckType.NONE)
    public void alert(WPObject wpObject)
    {

        Object[] cns = wpObject.cns;

        Object content = cns[0];
        int needConfirm = (Integer) cns[1];
        if (needConfirm == 0)
        {
            Toast.makeText(AppMain.getActivity(), content.toString(), Toast.LENGTH_SHORT).show();
            return;
        }
        String title = (String) wpObject.cus[0];

        AlertDialog.Builder builder = new AlertDialog.Builder(AppMain.getActivity()).setTitle(title);
        final ChooseCallback _chooseCallback = (ChooseCallback) wpObject.cus[1];
        if (_chooseCallback == null)
        {
            needConfirm = 1;
        }
        int contentType = (Integer) cns[2];
        View view = null;
        switch (contentType)
        {
            case 0:
            {
                view = getView(null, (Integer) content);
            }
            break;
            case 1:
            {
                view = getView((String) content, null);

            }
            break;
            case 2:
                builder.setMessage(content.toString());
                break;
            case 3:
                view = (View) content;
                break;
            default:
                throw new RuntimeException("illegal contentType " + contentType);
        }

        switch (needConfirm)
        {

            case 4:
            {
                builder.setPositiveButton(LangMap.getLangMap().get(LangMap.CommonStr.CANCEL),
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                                if (_chooseCallback != null)
                                {
                                    _chooseCallback.onCancel();
                                }
                            }
                        });
            }
            break;
            case 1:
            {
                builder.setPositiveButton(LangMap.getLangMap().get(LangMap.CommonStr.OK),
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                                if (_chooseCallback != null)
                                {
                                    _chooseCallback.onOk();
                                }
                            }
                        });
            }
            break;
            case 2:
            {
                builder.setPositiveButton(LangMap.getLangMap().get(LangMap.CommonStr.OK),
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                                if (_chooseCallback != null)
                                {
                                    _chooseCallback.onOk();
                                }
                            }
                        });
                builder.setNegativeButton(LangMap.getLangMap().get(LangMap.CommonStr.CANCEL),
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                                if (_chooseCallback != null)
                                {
                                    _chooseCallback.onCancel();
                                }
                            }
                        });
            }
            break;
            case 3:
                break;
            default:
        }


        if (view != null)
        {
            builder.setView(view);
        }

        AlertDialog alertDialog = builder.create();
        alertDialog.show();


        if (_chooseCallback != null)
        {

            alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener()
            {
                boolean isCanceled = false;

                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
                {
                    if (keyCode == KeyEvent.KEYCODE_BACK && !isCanceled)
                    {
                        isCanceled = true;
                        _chooseCallback.onCancel();
                    }
                    return false;
                }
            });
        }


        if (_chooseCallback != null && (_chooseCallback instanceof ChooseCallback.CallbackWithInit))
        {
            ChooseCallback.CallbackWithInit callback = (ChooseCallback.CallbackWithInit) _chooseCallback;
            callback.onInit(new AndroidC2LDialog(view, alertDialog));
        }

    }

    /**
     * 得到input使用的编辑框
     *
     * @return
     */
    private View getInputEditView()
    {
        View view;
        try
        {
            view = getView(getLayout(INPUT_LAYOUT), AppMain.getActivity());
        } catch (Exception e)
        {
            view = new EditText(AppMain.getActivity());
        }
        return view;
    }


    public static void input(int maxLength, boolean multiRow, BaseUI.InputCallback inputCallback,
            XmlInputType inputType, String title, String initText)
    {
        AppPorterUtil.getPorterObject(XmlUIEnum.FTied, XmlUIEnum.input,
                new SimpleAppValues("maxLength", "multiRow", "inputType", "title", "inputCallback", "initText")
                        .values(maxLength, multiRow, inputType, title == null || title.equals("") ? " " : title,
                                inputCallback, initText));
    }

    @ChildIn(tiedName = "input", neceParams = {"maxLength", "multiRow", "inputCallback", "inputType", "title"},
            unneceParams = {"initText"}, checkType = CheckType.NONE)
    public void input(WPObject wpObject)
    {
        Object[] cns = wpObject.cns;
        final int maxLength = (Integer) cns[0];
        final boolean multiRow = (Boolean) cns[1];
        final BaseUI.InputCallback inputCallback = (BaseUI.InputCallback) cns[2];
        final XmlInputType inputType = (XmlInputType) cns[3];
        final String initText = (String) wpObject.cus[0];
        String title = (String) cns[4];


        ChooseCallback.CallbackWithInit callback = new ChooseCallback.CallbackWithInit()
        {
            private EditText editText;

            @Override
            public void onInit(C2LDialog c2LDialog)
            {
                AndroidC2LDialog androidC2LDialog = (AndroidC2LDialog) c2LDialog;
                View view = androidC2LDialog.view;
                editText = view instanceof EditText ? (EditText) view : (EditText) view
                        .findViewById(getId(EDIT_TEXT_NAME));
                switch (inputType)
                {

                    case number:
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                        break;
                    case any:
                        editText.setInputType(InputType.TYPE_CLASS_TEXT);
                        break;
                    case password:
                        editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        editText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                        break;
                }

                InputFilter[] inputFilters = null;

                if (multiRow)
                {
                    editText.setInputType(
                            editText.getInputType() | InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE | InputType
                                    .TYPE_TEXT_FLAG_MULTI_LINE);
                    editText.setSingleLine(false);
                    inputFilters = new InputFilter[1];

//                    /**
//                     * 最大'\n'数
//                     */
//                    final int maxLns = mLines - 1;
//                    inputFilters[1] = new InputFilter() {//用于控制最大的换行数。
//                        //当前的'\n'数
//                        int currentLins = 0;
//
//                        /**
//                         * 得到'\n'的个数
//                         * @param str
//                         * @param start
//                         * @param end
//                         * @return
//                         */
//                        private int lnCount(CharSequence str, int start, int end) {
//                            int c = 0;
//                            for (int i = start; i < end; i++) {
//                                if ('\n' == str.charAt(i)) {
//                                    c++;
//                                }
//                            }
//                            return c;
//                        }
//
//                        /**
//                         *
//                         * @param sequence
//                         * @param start
//                         * @param end
//                         * @param count 从末尾开始删除的'\n'的个数
//                         * @return
//                         */
//                        private CharSequence decLines(CharSequence sequence, int start, int end, int count) {
//                            if (count == 0) {
//                                return sequence.subSequence(start, end);
//                            } else {
//                                char[] chars = new char[end - start - count];
//                                for (int i = end - 1, j = chars.length - 1; i >= start; i--) {
//                                    if (sequence.charAt(i) == '\n') {
//                                        if (count > 0) {
//                                            count--;
//                                        } else {
//                                            chars[j--] = '\n';
//                                        }
//                                    } else {
//                                        chars[j--] = sequence.charAt(i);
//                                    }
//                                }
//                                return new String(chars);
//                            }
//                        }
//
//                        @Override
//                        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart,
//                                int dend) {
//                            int slns = lnCount(source, start, end);
//                            int dlns = lnCount(dest, dstart, dend);
//                            int clns = currentLins - dlns + slns;
//                            int d;
//                            if (maxLns >= clns) {
//                                d = 0;
//                                currentLins = clns;
//                            } else {
//                                d = clns - maxLns;
//                                currentLins = maxLns;
//                            }
//                            return decLines(source, start, end, d);
//                        }
//                    };
                } else
                {
                    inputFilters = new InputFilter[1];
                }
                inputFilters[0] = new InputFilter.LengthFilter(maxLength);
                editText.setFilters(inputFilters);
                if (initText != null)
                {
                    editText.setText(initText);
                }
            }

            @Override
            public void onOk()
            {
                String text = editText.getText().toString();
                if (text.length() > 0)
                {
                    inputCallback.onOk(text);
                } else
                {
                    inputCallback.onCancel();
                }
            }

            @Override
            public void onCancel()
            {
                inputCallback.onCancel();
            }
        };
        View view = getInputEditView();
        AppPorterUtil.getPorterObject(XmlUIEnum.FTied, XmlUIEnum.alert,
                new String[]{"contentType", "content", "needConfirm", "title", "chooseCallback"},
                new Object[]{3, view, 2, title, callback});

    }


    private static Integer get(String fieldName, String className)
    {
        try
        {
            Class<?> layoutClass = Class.forName(getClassR() + "$" + className);
            Field field = layoutClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            int id = field.getInt(null);
            return id;
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }


    private static Integer getId(String fieldName)
    {
        return get(fieldName, ID_CLASS);
    }

    private static Integer getLayout(String layoutName)
    {
        return get(layoutName, LAYOUT_CLASS);
    }


}
