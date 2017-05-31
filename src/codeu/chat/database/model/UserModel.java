package codeu.chat.database.model;

/**
 * Created by greg on 5/30/17.
 */
public class UserModel {
    public String name;
    public String creation;

    public UserModel(String name, String creation) {
        this.name = name;
        this.creation = creation;
    }
    public UserModel() {
    }
}
