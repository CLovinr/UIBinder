package com.chenyg.uibinder.android.xmlui;


import com.chenyg.uibinder.BaseUI;
import com.chenyg.wporter.base.AppValues;

/**
 * Created by ZhuiFeng on 2015/6/7.
 */
public class XmlUIValues {
    public static AppValues forInput(final int maxLines, final int maxLength, final BaseUI.InputCallback inputCallback, final XmlInputType inputType, final String titlle){
        return  new AppValues() {
            @Override
            public String[] getNames() {
                return new String[]{"maxLines","maxLength","inputCallback","inputType","title"};
            }

            @Override
            public Object[] getValues() {
                return new Object[]{maxLines,maxLength,inputCallback,inputType,titlle};
            }
        };
    }
}
