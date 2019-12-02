package com.uav.autodebit.Interface;

public class ServiceClick {
    private OnSuccess onSuccess;
    private OnError onError;

    public ServiceClick(OnSuccess onSuccess, OnError onError){
        this.onSuccess  = onSuccess;
        this.onError = onError;
    }

    public void onSuccess(Object s){
        onSuccess.onSuccess(s);
    }

    public void onError(String s) {
        onError.onError(s);
    }

    public interface OnSuccess {
        void onSuccess(Object s);
    }

    public interface OnError {
        void onError(String s);
    }

}
