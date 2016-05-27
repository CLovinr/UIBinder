package com.chenyg.uibinder.android.binders;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import com.chenyg.uibinder.AttrEnum;
import com.chenyg.uibinder.OnValueChangedListener;
import com.chenyg.uibinder.android.AndroidBinder;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by ZhuiFeng on 2015/6/12.
 */
public class SpinnerBinder extends AndroidBinder
{
    private Spinner spinner;

    public SpinnerBinder(Spinner spinner)
    {
        super(spinner);
        this.spinner = spinner;
    }


    private void setOnValueChangedListener(final OnValueChangedListener onValueChangedListener)
    {
        this.onValueChangedListener = onValueChangedListener;
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                currentValue = position;
                doOnchange();
//                doOnchange(tiedFun1,position);
//                doOnchange(tiedFun2,position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                currentValue = -1;
                doOnchange();
//                doOnchange(tiedFun1,-1);
//                doOnchange(tiedFun2,-1);
            }

//            private void doOnchange(String tiedFun, Object value) {
//                if (tiedFun != null) {
//                    onValueChangedListener.onChanged(prefix, tiedFun, varName, value);
//                }
//            }
        });
    }

    @Override
    public void release()
    {
        super.release();
        spinner = null;
    }

    /**
     * @param attrEnum
     * @param value
     */
    @Override
    public void set(AttrEnum attrEnum, Object value)
    {
        if (AttrEnum.ATTR_VALUE == attrEnum)
        {
            Integer _selection = (Integer) value;
            spinner.setSelection(_selection);
        } else if (AttrEnum.ATTR_VALUE_CHANGE_LISTENER == attrEnum && value != null && (value instanceof
                OnValueChangedListener))
        {
            OnValueChangedListener onValueChangedListener = (OnValueChangedListener) value;
            setOnValueChangedListener(onValueChangedListener);
        } else if (attrEnum == AttrEnum.ATTR_VALUE_OTHER)
        {
            if (value != null)
            {
                if (value instanceof SpinnerAdapter)
                {
                    SpinnerAdapter adapter = (SpinnerAdapter) value;
                    spinner.setAdapter(adapter);
                } else if (value instanceof String[])
                {
                    SpinnerAdapter adapter = new ArrayAdapter<String>(viewType.getContext(),
                            android.R.layout.simple_list_item_1, (String[]) value);
                    spinner.setAdapter(adapter);
                } else if (value instanceof JSONArray)
                {
                    JSONArray arr = (JSONArray) value;
                    String[] strs = new String[arr.length()];

                    try
                    {
                        for (int i = 0; i < strs.length; i++)
                        {
                            strs[i] = arr.getString(i);
                        }
                        SpinnerAdapter adapter = new ArrayAdapter<String>(viewType.getContext(),
                                android.R.layout.simple_list_item_1, (strs));
                        spinner.setAdapter(adapter);
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }


                } else
                {
                    throw new RuntimeException("unknown value type " + value.getClass());
                }

            }

        } else
        {
            super.set(attrEnum, value);
        }
    }


    /**
     * @param attrEnum
     * @return spinner.getSelectedItem
     */
    @Override
    public Object get(AttrEnum attrEnum)
    {
        if (AttrEnum.ATTR_VALUE == attrEnum)
        {
            return spinner.getSelectedItemPosition();
        }
        return super.get(attrEnum);
    }

}
