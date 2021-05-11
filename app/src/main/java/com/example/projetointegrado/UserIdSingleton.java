package com.example.projetointegrado;

public class UserIdSingleton {
    private static UserIdSingleton mInstance= null;

    public String userId;

    protected UserIdSingleton(){}

    public static synchronized UserIdSingleton getInstance() {
        if(null == mInstance){
            mInstance = new UserIdSingleton();
        }
        return mInstance;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }
}
