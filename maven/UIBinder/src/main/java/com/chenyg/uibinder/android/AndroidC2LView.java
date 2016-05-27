package com.chenyg.uibinder.android;

import com.chenyg.uibinder.C2LView;
import com.chenyg.uibinder.android.xmlui.XmlUIEnum;

/**
 * Created by 宇宙之灵 on 2015/9/12.
 */
public class AndroidC2LView extends C2LView
{
    public int contentType;
    public Object content;
    public int needConfirm;

    /**
     * 见{@link XmlUIEnum#alert}
     *
     * @param contentType
     * @param content
     * @param needConfirm
     */
    public AndroidC2LView(int contentType, Object content, int needConfirm)
    {
        this.contentType = contentType;
        this.content = content;
        this.needConfirm = needConfirm;
    }
}
