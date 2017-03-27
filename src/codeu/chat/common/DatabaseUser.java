package codeu.chat.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import codeu.chat.util.Serializer;
import codeu.chat.util.Serializers;
public class DatabaseUser{
    /*public static final Serializer<DatabaseUser> SERIALIZER = new Serializer<DatabaseUser>() {

        @Override
        public void write(OutputStream out, DatabaseUser value) throws IOException {

        Serializers.STRING.write(out, value.display_name);
        Serializers.STRING.write(out, value.name);
        Serializers.STRING.write(out, value.pswd);

        }

        @Override
        public User read(InputStream in) throws IOException {

        return new User(
            Serializers.STRING.read(in),
            Serializers.STRING.read(in),
            Serializers.STRING.read(in)
        );

        }
    };*/

    private String display_name;
    private String pswd;
    private String name;

    public DatabaseUser(String display_name, String pswd, String name){
        this.name = name;
        this.display_name = display_name;
        this.pswd = pswd;
    }

    public boolean checkLogin(String loginUsername, String loginPswd){
        return this.name.equals(loginUsername) && this.pswd.equals(loginPswd);
    }
    public String toString(){
        return "Username: " + this.name + "\nDisplay name: " + this.display_name;
    }
}