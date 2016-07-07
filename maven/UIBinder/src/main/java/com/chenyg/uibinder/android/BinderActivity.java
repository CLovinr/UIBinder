package com.chenyg.uibinder.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.chenyg.uibinder.*;
import com.chenyg.uibinder.android.base.AppMain;
import com.chenyg.uibinder.android.base.C2LActivity;
import com.chenyg.uibinder.android.binders.*;
import com.chenyg.uibinder.android.xmlui.XmlInputType;
import com.chenyg.uibinder.base.BinderPorter;
import com.chenyg.uibinder.base.ChooseCallback;
import com.chenyg.uibinder.Prefix;
import com.chenyg.uibinder.android.xmlui.XmlUIEnum;
import com.chenyg.uibinder.android.xmlui.XmlUIPorter;
import com.chenyg.wporter.Config;
import com.chenyg.wporter.WPObject;
import com.chenyg.wporter.WebPorter;
import com.chenyg.wporter.a.app.AppPorterUtil;
import com.chenyg.wporter.base.AppValues;
import com.chenyg.wporter.base.SimpleAppValues;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * <pre>
 *     1.请记住，每次调用{@linkplain #onResume()},都会先清除所有normal接口，再重新添加，也就是{@linkplain BinderActivity#BinderActivity(Class,
 *     DealType, Set)} 中的Set;
 *         若不希望这样，请调用{@linkplain BinderActivity#setReseekerNormal(boolean)}来更改。
 *     2.请正确传递DealType,特别是使用多个BinderActivity时。
 *          因为默认情况下，按一次返回键，并且该返回键被传递到了BinderActivity(其中，当栈为空时会直接退出程序。)，则会pop出栈顶绑定的view.
 *     3.<strong>为什么有上述要求，是因为所有的BinderActivity都共享一个接口处理和UI绑定器。而共享一个处理，
 *          是为了在返回时（弹出栈顶的绑定记录），依然能够正确响应之前的绑定，同时也是为了使得ui绑定更加简单而才这样设计的。</strong>
 *      4.对于触发binder,要设置其相关属性时，参数名为它的接口函绑定数名。
 * </pre>
 * <p/>
 * Created by ZhuiFeng on 2015/6/11.
 */
public class BinderActivity extends C2LActivity
{

    /**
     * <pre>
     * 当按返回键后,并且该返回键被传递到了BinderActivity时，采取的处理方式。当然，栈为空时，则直接退出。
     * 按返回键后，都会弹出栈顶的view和绑定，而接着，对于BACK_AUTO会把此时栈顶的view显示出来。
     * <pre>
     */
    public enum DealType
    {
        /**
         * 系统的处理方式,用于有多个BinderActivity,按返回键时返回到上一个BinderActivity.
         */
        BACK_SYS,

        /**
         * 自动，用于一个BinderActivity绑定和显示多个View.
         */
        BACK_AUTO
    }


    /**
     * Created by 宇宙之灵 on 2015/9/12.
     */
    class AndroidBaseUI extends BaseUI
    {
        BinderFactory<View> binderFactory = new BinderFactory<View>(View.class);

        AndroidBaseUI()
        {
            binderFactory.put(CompoundButton.class, CompoundButtonBinder.class);
            binderFactory.put(Button.class, ButtonBinder.class);
            binderFactory.put(EditText.class, EditTextBinder.class);
            binderFactory.put(RatingBar.class, RatingBarBinder.class);
            binderFactory.put(SeekBar.class, SeekBarBinder.class);
            binderFactory.put(ImageButton.class, ImageButtonBinder.class);
            binderFactory.put(ImageView.class, ImageViewBinder.class);
            binderFactory.put(Spinner.class, SpinnerBinder.class);
            binderFactory.put(TextView.class, TextViewBinder.class);
        }

        @Override
        public <T> BinderFactory<T> getBinderFactory(Class<T> t)
        {
            return (BinderFactory<T>) binderFactory;
        }


        @Override
        public void sendBinderData(final String porterPrefix, final BinderData binderData, final boolean toAll)
        {
            if (isMainThread())
            {
                BinderActivity.sendBinderData(porterPrefix, binderData, toAll);
            } else
            {
                AppMain.getHandler().post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        BinderActivity.sendBinderData(porterPrefix, binderData, toAll);
                    }
                });
            }


        }

        @Override
        public void alert(String... contents)
        {
            final StringBuilder stringBuilder = new StringBuilder();
            for (String str : contents)
            {
                stringBuilder.append(str);
            }
            if (isMainThread())
            {
                XmlUIPorter.alert(LangMap.getLangMap().get(LangMap.CommonStr.ALERT), stringBuilder.toString(),
                        null);
            } else
            {
                AppMain.getHandler().post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        XmlUIPorter.alert(LangMap.getLangMap().get(LangMap.CommonStr.ALERT), stringBuilder.toString(),
                                null);
                    }
                });
            }


        }

        @Override
        public void alert(final String title, final String content, final ChooseCallback chooseCallback)
        {
            if (isMainThread())
            {
                XmlUIPorter.alert(title, content, chooseCallback);
            } else
            {
                AppMain.getHandler().post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        XmlUIPorter.alert(title, content, chooseCallback);
                    }
                });
            }


        }

        @Override
        public void toast(final String content)
        {
            if (isMainThread())
            {
                Toast.makeText(BinderActivity.this, content, Toast.LENGTH_SHORT).show();
            } else
            {
                AppMain.getHandler().post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(BinderActivity.this, content, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        @Override
        public void popBinder()
        {
            BinderActivity.popBinder();
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
            if (isMainThread())
            {
                XmlUIPorter.waitingShow(stringBuilder.toString());
            } else
            {
                AppMain.getHandler().post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        XmlUIPorter.waitingShow(stringBuilder.toString());
                    }
                });
            }


        }

        @Override
        public void waitingDisShow()
        {
            if (isMainThread())
            {
                XmlUIPorter.waitingDisShow();
            } else
            {
                AppMain.getHandler().post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        XmlUIPorter.waitingDisShow();
                    }
                });
            }


        }

        @Override
        public void alert(final String title, final C2LView c2LView,
                final ChooseCallback.CallbackWithInit callbackWithInit,
                final Prefix prefix, final AppValues callbackValues)
        {
            if (isMainThread())
            {
                AndroidC2LView androidC2LView = (AndroidC2LView) c2LView;
                bindAlert(androidC2LView.contentType, androidC2LView.content, androidC2LView.needConfirm, title,
                        callbackWithInit, prefix, callbackValues);
            } else
            {
                AppMain.getHandler().post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        AndroidC2LView androidC2LView = (AndroidC2LView) c2LView;
                        bindAlert(androidC2LView.contentType, androidC2LView.content, androidC2LView.needConfirm, title,
                                callbackWithInit, prefix, callbackValues);
                    }
                });

            }

        }

        @Override
        public void input(final boolean multiRow, final int maxLength, final String title, final String initTxt,
                final InputCallback inputCallback)
        {
            if (isMainThread())
            {
                XmlUIPorter.input(maxLength, multiRow, inputCallback,
                        XmlInputType.any, title, initTxt);
            } else
            {
                AppMain.getHandler().post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        XmlUIPorter.input(maxLength, multiRow, inputCallback,
                                XmlInputType.any, title, initTxt);
                    }
                });
            }


        }
    }

    private boolean isMainThread()
    {
        return Thread.currentThread().equals(AppMain.getHandler().getLooper().getThread());
    }

    private static UISeeker seeker;
    private static Stack<Temp> stack;

    private static class Temp
    {
        private Prefix prefix;
        private View view;
        private boolean settedView;
        private Object object;

        public Temp(Prefix prefix, View view, boolean setView, Object object)
        {
            this.prefix = prefix;
            this.view = view;
            this.settedView = setView;
            this.object = object;
        }
    }

    private DealType dealType;
    private Set<WebPorter> webPorters;
    private boolean reseekerNormal = false;

    //当前BinderActivity绑定的数目
    private int bindCount = 0;
    private int id;

    //private static Set<WebPorter> sysPorter = getSysPorter();
    private static int count;
    private boolean isSysDestroy;

    /**
     * 是否在销毁时不清空接口栈，如在屏幕旋转时。
     */
    private boolean notPopAll;

    /**
     * 用于判断是否是从BinderActivity返回过来的，用于判断是否调用{@linkplain #checkShowAgain()}
     */
    private static boolean backFromBinderActivity;
    private static int binderActivityId;


    /**
     * @param classR     R.class
     * @param webPorters 待扫描的接口
     * @param dealType
     * @param config     用于初始化app接口
     */
    public BinderActivity(Class<?> classR, DealType dealType, Set<WebPorter> webPorters, Config config)
    {
        super(classR, config);
        init(webPorters, dealType);
    }

    /**
     * @param classR     R.class
     * @param dealType
     * @param webPorters 待扫描的接口
     */
    public BinderActivity(Class<?> classR, DealType dealType,Config config, WebPorter... webPorters)
    {
        super(classR,config);
        id = count++;
        Set<WebPorter> set = new HashSet<WebPorter>(webPorters.length);
        for (WebPorter webPorter : webPorters)
        {
            set.add(webPorter);
        }
        init(set, dealType);
    }

    private void init(Set<WebPorter> webPorters, DealType dealType)
    {
        this.webPorters = webPorters;
        if (dealType == null)
        {
            throw new NullPointerException("dealType is null!");
        }
        this.dealType = dealType;
        if (stack == null)
        {
            stack = new Stack<Temp>();
            seeker = new UISeeker(new AndroidBaseUI());
        }

    }

    /**
     * 是否在onResume时，重新载入normal接口.默认为false。
     *
     * @param reseekerNormal
     */
    protected void setReseekerNormal(boolean reseekerNormal)
    {
        this.reseekerNormal = reseekerNormal;
    }


    private static Set<WebPorter> getSysPorter()
    {
        HashSet<WebPorter> set = new HashSet<WebPorter>(1);
        XmlUIPorter xmlUIPorter = new XmlUIPorter();
        set.add(xmlUIPorter);
        return set;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null)
        {
            //避免重复扫描.
            bindCount = savedInstanceState.getInt("_bindCount_");
        } else
        {
            if (stack.isEmpty())
            {
                seekPorter(true, getSysPorter());
            }
            seekPorter(false, webPorters);
        }

    }


    @Override
    protected void onResume()
    {
        super.onResume();

        if (reseekerNormal)
        {
            clearPorter(false);
            seekPorter(false, webPorters);
        }

        if (backFromBinderActivity && binderActivityId != id)
        {
            checkShowAgain();
        }

        backFromBinderActivity = false;
        binderActivityId = id;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt("_bindCount_", bindCount);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        binderActivityId = id;
        backFromBinderActivity = true;
    }

    /**
     * <pre>
     * 调用它，用于绑定.View可以不是该Activity显示的对象，可以是某个alert显示的内容等。
     * </pre>
     *
     * @param temp
     * @return 返回当前绑定的数量。
     */
    private int push(Temp temp, AppValues callbackValues)
    {
        try
        {
            View view = temp.view;
            Prefix prefix = temp.prefix;
            AndroidUIProvider androidUIProvider = new AndroidUIProvider(AppMain.getClassR(), view, prefix);
            seeker.push(androidUIProvider);
            stack.push(temp);
            bindCount++;
            if (temp.settedView)
            {
                try
                {
                    setContentView(view);
                } catch (Exception e)
                {
                    temp.settedView = false;
                    throw new RuntimeException(e);
                }
            }
            if (prefix.bindCallbackMethod != null)
            {
                SimpleAppValues simpleAppValues = new SimpleAppValues(BinderPorter.CALLBACK_VIEW,
                        BinderPorter.CALLBACK_PORTER_PREFIX)
                        .values(view, prefix.porterPrefix).add(callbackValues);
                AppPorterUtil.getPorterObject(prefix.porterPrefix, prefix.bindCallbackMethod, simpleAppValues);
            }
            return bindCount;
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * 若pop后，当前的BinderActivity绑定为空，则会finish()
     *
     * @param setView       pop后，是否设置栈顶view为Activity的显示内容。
     * @param concernDialog 是否考虑Dialog
     * @param emptyFinish   当栈为空时，是否finish
     * @return 当前BinderActivity的绑定是否不为空:true表示不为空，false表示为空.
     */
    private boolean pop(boolean setView, boolean concernDialog, boolean emptyFinish, boolean isClear)
    {
        if (bindCount > 0)
        {
            seeker.pop();
            bindCount--;
            Temp temp = stack.pop();

            if (concernDialog && temp.object != null)
            {
                if (temp.object instanceof AlertDialog)
                {
                    setView = false;
                    emptyFinish = false;
                    AlertDialog alertDialog = (AlertDialog) temp.object;
                    if (alertDialog.isShowing())
                    {
                        alertDialog.dismiss();
                    } else
                    {
                        pop(setView, true, emptyFinish, false);
                    }
                } else if (temp.object instanceof PopupWindow)
                {
                    setView = false;
                    emptyFinish = false;
                    PopupWindow popupWindow = (PopupWindow) temp.object;
                    if (popupWindow.isShowing())
                    {
                        popupWindow.dismiss();
                    }
                } else if (temp.object instanceof BindViewWithoutShow)
                {
                    setView = false;
                    emptyFinish = false;
                }

            }
            if (setView && temp.settedView && bindCount > 0)
            {
                setTopView();
            }
        }
        if (isSysDestroy)
        {
            isSysDestroy = false;
        } else
        {

            if (isClear)
            {
                emptyFinish = true;
            }

            if (bindCount == 0 && emptyFinish)
            {
                finish();//针对当前BinderActivity
                if (stack.isEmpty())
                {
                    AppMain.getAppMain().stop();
                }
            }

        }

        return bindCount > 0;
    }


    /**
     * 显示栈顶的view，若有再次显示的回调函数，则进行调用，会传人view参数。
     */
    private void setTopView()
    {
        if (stack.empty())
        {
            throw new RuntimeException("the stack is empty!");
        } else
        {
            Temp temp = stack.peek();
            setContentView(temp.view);
            checkShowAgain();
        }
    }

    private void checkShowAgain()
    {
        if (stack.empty())
        {
            return;
        }
        Temp temp = stack.peek();
        Prefix prefix = temp.prefix;
        if (prefix.getShowAgainCallback() != null)
        {
            SimpleAppValues simpleAppValues = new SimpleAppValues(BinderPorter.CALLBACK_VIEW,
                    BinderPorter.CALLBACK_PORTER_PREFIX).values(temp.view, prefix.porterPrefix);
            AppPorterUtil.getPorterObject(prefix.porterPrefix, prefix.getShowAgainCallback(), simpleAppValues);
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        if (hasFocus && AppMain.isStarted() && !stack.isEmpty())
        {
            Temp temp = stack.peek();
            Prefix prefix = temp.prefix;
            AppPorterUtil.getPorterObject(prefix.porterPrefix, BinderPorter.ON_READY);
        }
    }

    /**
     * @param binderType
     * @param layoutId
     * @param prefix
     * @param callbackValues
     * @see #bind(BinderType, View, Prefix, AppValues)
     */
    protected void bind(BinderType binderType, int layoutId, Prefix prefix, AppValues callbackValues)
    {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(layoutId, null);
        bind(binderType, view, prefix, callbackValues);
    }


    protected void bindWithoutShow(View view, Prefix prefix, AppValues appValues)
    {
        push(new Temp(prefix, view, false, BindViewWithoutShow.BIND_VIEW_WITHOUT_SHOW), appValues);
    }

    protected void bindPopupWindow(PopupWindow popupWindow, Prefix prefix, AppValues appValues)
    {
        final int curentCount = push(new Temp(prefix, popupWindow.getContentView(), false, popupWindow), appValues);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener()
        {
            @Override
            public void onDismiss()
            {
                if (bindCount >= curentCount)
                {
                    pop(false, false, false, false);
                }

            }
        });
    }

    /**
     * 见{@link XmlUIEnum#alert}
     *
     * @param contentType
     * @param content
     * @param needConfirm
     * @param title
     * @param callback
     * @param prefix
     * @param callbackValues
     */
    protected void bindAlert(int contentType, Object content, int needConfirm, String title,
            final ChooseCallback.CallbackWithInit callback, final Prefix prefix, final AppValues callbackValues)
    {

        ChooseCallback.CallbackWithInit callbackWithInit = new ChooseCallback.CallbackWithInit()
        {
            AlertDialog alertDialog;
            boolean pushed = false;
            boolean isCalled = false;

            @Override
            public void onInit(C2LDialog c2LDialog)
            {
                AndroidC2LDialog androidC2LDialog = (AndroidC2LDialog) c2LDialog;
                AlertDialog alertDialog = androidC2LDialog.alertDialog;
                this.alertDialog = alertDialog;
                View view = androidC2LDialog.view;
                alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        if (pushed && !isCalled)
                        {//默认popBinder是取消。
                            isCalled = true;
                            if (callback != null)
                            {
                                callback.onCancel();
                            }
                        }
                    }
                });
                if (view != null)
                {
                    push(new Temp(prefix, view, false, alertDialog), callbackValues);
                    pushed = true;
                }
                if (callback != null)
                {
                    callback.onInit(c2LDialog);
                }
            }

            @Override
            public void onOk()
            {
                if (alertDialog.isShowing())
                {
                    alertDialog.dismiss();
                }
                if (pushed && !isCalled)
                {
                    pop(false, false, false, false);
                }
                isCalled = true;
                if (callback != null)
                {
                    callback.onOk();
                }


            }

            @Override
            public void onCancel()
            {
                if (alertDialog.isShowing())
                {
                    alertDialog.dismiss();
                }
                if (pushed && !isCalled)
                {
                    pop(false, false, false, false);
                }
                isCalled = true;
                if (callback != null)
                {
                    callback.onCancel();
                }
            }

        };

        AppPorterUtil.getPorterObject(XmlUIEnum.FTied, XmlUIEnum.alert,
                new String[]{"contentType", "content", "needConfirm", "title", "chooseCallback"},
                new Object[]{contentType, content, needConfirm, title, callbackWithInit});

    }

    /**
     * @param binderType     当为{@linkplain BinderType#POP}时，其他参数不会被使用，为null即可。
     * @param view
     * @param prefix
     * @param callbackValues
     */
    protected void bind(final BinderType binderType, final View view, final Prefix prefix,
            final AppValues callbackValues)
    {

        Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                switch (binderType)
                {
                    case PUSH_SET:
                        push(new Temp(prefix, view, true, null), callbackValues);
                        break;
                    case POP_PUSH_SET:
                        pop(false, true, false, false);
                        push(new Temp(prefix, view, true, null), callbackValues);
                        break;
                    case POP:
                        pop(true, true, true, false);
                        break;
                }
            }
        };

        if (isMainThread())
        {
            runnable.run();
        } else
        {
            AppMain.getHandler().post(runnable);
        }

    }

    protected void setNotPopAll(boolean notPopAll)
    {
        this.notPopAll = notPopAll;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (!notPopAll)
        {
            if (stack.isEmpty())
            {
                stopPorter();
            } else
            {
                isSysDestroy = true;
                _destroy();
            }
        }


    }

    private void _destroy()
    {
        clearCurrentActivityBinders();
        if (AppMain.getAppMain() != null)
        {
            AppMain.getAppMain().getAppPorterMain().getWpMain().clearPorters(webPorters, false);
        }
    }

    /**
     * 清除当前BinderActivity的所有绑定
     */
    private void clearCurrentActivityBinders()
    {
        while (pop(false, true, true, true)) ;
    }


    /**
     * 若栈顶的是Dialog，则会取消显示。若当前BinderActivity绑定为空时，则调用finish()
     */
    public static void popBinder()
    {
        BinderActivity binderActivity = getBinderActivity();
        binderActivity.pop(true, true, true, false);
    }


    /**
     * @param binderType
     * @param prefix
     * @param layoutId
     */
    public static void bindView(BinderType binderType, Prefix prefix, int layoutId)
    {
        bindView(binderType, prefix, layoutId, null, null);
    }

    /**
     * @param binderType
     * @param prefix
     * @param layoutName
     */
    public static void bindView(BinderType binderType, Prefix prefix, String layoutName)
    {
        bindView(binderType, prefix, null, layoutName, null);
    }


    /**
     * 见{@link XmlUIEnum#alert}
     *
     * @param contentType
     * @param content
     * @param needConfirm
     * @param title
     * @param callback
     * @param prefix
     * @param callbackValues
     * @see XmlUIPorter#alert(WPObject)
     */
    public static void bindAlert_(int contentType, Object content, int needConfirm, String title,
            final ChooseCallback.CallbackWithInit callback, final Prefix prefix, final AppValues callbackValues)
    {
        BinderActivity binderActivity = getBinderActivity();
        binderActivity.bindAlert(contentType, content, needConfirm, title, callback, prefix, callbackValues);
    }

    /**
     * @param binderType
     * @param prefix
     * @param view
     * @param callbackValues
     */
    public static void bindView(BinderType binderType, Prefix prefix, View view, AppValues callbackValues)
    {
        BinderActivity binderActivity = getBinderActivity();
        binderActivity.bind(binderType, view, prefix, callbackValues);
    }

    public static void setFireBlock(FireBlock fireBlock)
    {
        seeker.setFireBlock(fireBlock);
    }

    public static void removeFireBlock()
    {
        seeker.unsetFireBlock();
    }

    /**
     * @param binderType
     * @param prefix
     * @param layoutId
     * @param layoutName
     * @param callbackValues
     */
    private static void bindView(BinderType binderType, Prefix prefix, Integer layoutId, String layoutName,
            AppValues callbackValues)
    {
        BinderActivity binderActivity = getBinderActivity();
        if (layoutId == null)
        {
            if (layoutName != null)
            {
                layoutId = get(layoutName, XmlUIPorter.LAYOUT_CLASS);
            }
        }

        if (layoutId != null)
        {
            binderActivity.bind(binderType, layoutId, prefix, callbackValues);
        }
    }

    private static Integer get(String fieldName, String className)
    {
        try
        {
            Class<?> layoutClass = Class.forName(AppMain.getClassR().getName() + "$" + className);
            Field field = layoutClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            int id = field.getInt(null);
            return id;
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    static BinderActivity getBinderActivity()
    {
        C2LActivity c2LActivity = AppMain.getActivity();
        if (c2LActivity != null && (c2LActivity instanceof BinderActivity))
        {
            BinderActivity activity = (BinderActivity) c2LActivity;
            return activity;
        } else
        {
            throw new RuntimeException(
                    "the binderui system is not started or AppMain.getActivity() is not BinderActivity!");
        }
    }

    /**
     * 启动另一个BinderActivity(处于同一个进程中)
     *
     * @param c
     */
    public static void startBinderActivity(Class<? extends BinderActivity> c)
    {
        BinderActivity binderActivity = getBinderActivity();
        Intent intent = new Intent(binderActivity, c);
        binderActivity.startActivity(intent);
    }


    /**
     * 启动其他活动
     *
     * @param current
     * @param c
     */
    public static void startActivity(Activity current, Class<? extends Activity> c)
    {
        Intent intent = new Intent(current, c);
        current.startActivity(intent);
    }

    /**
     * finish当前的BinderActivity
     */
    public static void finishCurrentBinderActivity()
    {
        BinderActivity binderActivity = getBinderActivity();
        binderActivity.clearCurrentActivityBinders();

        if (!binderActivity.isFinishing())
        {
            binderActivity.finish();
        }

    }


    /**
     * 见 {@linkplain UISeeker#sendBinderData(String, BinderData, boolean)}
     *
     * @param porterPrefix 接口前缀
     * @param binderData
     */
    private static void sendBinderData(String porterPrefix, BinderData binderData, boolean toAll)
    {
        seeker.sendBinderData(porterPrefix, binderData, toAll);
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
        return XmlUIPorter.getView(name, id);
    }

    /**
     * 按一次返回键(返回键传递到该BainderActivity)，则popBinder一次，当栈为空时，退出程序或者由系统默认处理。
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK)
        {
//            popBinder();
//            if (stack.empty())
//            {
//                finish();//针对最后一个BinderActivity
//            } else
//            {
//                switch (dealType)
//                {
//                    case BACK_SYS:
//                        return super.onKeyDown(keyCode, event);
//                    case BACK_AUTO:
//                        break;
//                }
//            }
//            return true;


            switch (dealType)
            {
                case BACK_SYS:
                    return super.onKeyDown(keyCode, event);
                case BACK_AUTO:
                {
                    popBinder();
                }
                break;
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
