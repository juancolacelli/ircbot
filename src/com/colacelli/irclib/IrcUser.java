package com.colacelli.irclib;

public class IrcUser {
    private String nick;
    
    IrcUser(String nick) {
        this.nick = nick;
    }
    
    public String getNick() {
        return this.nick;
    }
    
    public void setNick(String nick) {
        this.nick = nick;
    }
}
