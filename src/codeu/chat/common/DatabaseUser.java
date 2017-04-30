package codeu.chat.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import codeu.chat.util.Serializer;
import codeu.chat.util.Serializers;

// DatabaseUser is used to parse the json object from the database with Gson.
public class DatabaseUser{
    public final String id;
    public final String displayName;
    public final String pswd;
    public final String name;

    public DatabaseUser(String id, String displayName, String pswd, String name){
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.pswd = pswd;
    }

    public boolean checkLogin(String loginUsername, String loginPswd){
        return this.name.equals(loginUsername) && this.pswd.equals(loginPswd);
    }
    public String toString(){
        return "Username: " + this.name + "\nDisplay name: " + this.displayName;
    }
}