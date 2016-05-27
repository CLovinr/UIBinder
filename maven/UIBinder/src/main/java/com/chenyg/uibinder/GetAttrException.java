package com.chenyg.uibinder;

/**
 * Created by 宇宙之灵 on 2016/4/28.
 */
public class GetAttrException extends Exception
{
    public GetAttrException(Throwable throwable)
    {
        super(throwable);
    }

    public GetAttrException()
    {

    }

    public static class GetAttrTimeoutException extends GetAttrException
    {
        public GetAttrTimeoutException(Throwable throwable)
        {
            super(throwable);
        }

        public GetAttrTimeoutException()
        {

        }
    }

}
