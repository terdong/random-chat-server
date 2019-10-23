package com.chatserver_base.thread;


public class UserInfo{
    private String preNick;
    private String uNick;
    public UserInfo(String uNick) {
        this.uNick=this.preNick=uNick;
    }
    public String getPreNick() {
        return preNick;
    }
    public void setPreNick(String preNick) {
        this.preNick = preNick;
    }
    public String getuNick() {
        return uNick;
    }
    public void setuNick(String uNick) {
        this.uNick = uNick;
    }
    
    
}