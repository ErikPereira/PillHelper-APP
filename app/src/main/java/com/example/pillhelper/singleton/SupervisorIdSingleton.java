package com.example.pillhelper.singleton;

public class SupervisorIdSingleton {

    private static SupervisorIdSingleton mInstance= null;

    public String supervisorId;

    protected SupervisorIdSingleton(){}

    public static synchronized SupervisorIdSingleton getInstance() {
        if(null == mInstance){
            mInstance = new SupervisorIdSingleton();
        }
        return mInstance;
    }

    public String getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(String supervisorId){
        this.supervisorId = supervisorId;
    }

}
