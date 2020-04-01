package com.smh.fzsd.Rx;


import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


public abstract class BaseSubscriber<T> implements Observer<T> {


    @Override
    public void onError(Throwable e) {
        // todo error somthing
        if (e instanceof ExceptionHandle.ResponeThrowable) {
            onError(e);
        } else {
            onError(new ExceptionHandle.ResponeThrowable(e, ExceptionHandle.ERROR.UNKNOWN));
        }
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onNext(T t) {
        if (t != null)
            next(t);
        else {
            onError(new ExceptionHandle.ResponeThrowable(new Throwable("返回的数据是null或者返回数据的格式错误"), 0));
        }
    }

    public abstract void next(T t);

    public abstract void onError(ExceptionHandle.ResponeThrowable e);

}