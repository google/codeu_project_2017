package codeu.chat.common;

public class DatabaseUser{
    private String username;
    private String pswd;
    private String name;

    public DatabaseUser(String username, String pswd, String name){
        this.name = name;
        this.username = username;
        this.pswd = pswd;
    }

    public boolean checkLogin(String loginUsername, String loginPswd){
        return this.username.equals(loginUsername) && this.pswd.equals(loginPswd);
    }
    public String toString(){
        return "Name: " + this.name + "\nUsername: " + this.username;
    }
}