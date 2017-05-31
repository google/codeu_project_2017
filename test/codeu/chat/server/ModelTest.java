package codeu.chat.server;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

import codeu.chat.common.BasicController;
import codeu.chat.common.Conversation;
import codeu.chat.common.Message;
import codeu.chat.common.User;
import codeu.chat.util.Uuid;
import codeu.chat.util.Time;


public final class ModelTest {

    private Model model;

    @Before
    public void doBefore() {
        model = new Model();
    }

    @Test
    public void testdeleteUser() {

        User mathangi = new User(new Uuid(1), "Mathangi", new Time(500));
        User jess = new User(new Uuid(2), "JESS", new Time(600));
        User sarah = new User(new Uuid(4), "SaraH", new Time(800));

        model.add(mathangi);
        model.add(jess);
        model.add(sarah);

        assertEquals(true, model.deleteUser(mathangi));

    }

}
