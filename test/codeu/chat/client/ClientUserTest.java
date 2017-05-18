//Backend Test of ClientUser method(s)

package codeu.chat.client;

import java.util.ArrayList;
import static org.junit.Assert.*;
import static org.mockito.*;

import org.junit.Test;
import org.junit.Before;


import codeu.chat.common.BasicController;
import codeu.chat.common.Conversation;
import codeu.chat.common.Message;
import codeu.chat.common.User;
import codeu.chat.util.Logger;
import codeu.chat.util.Uuid;
import codeu.chat.util.store.Store;
import codeu.chat.util.connections.ConnectionSource;
import codeu.chat.util.connections.ClientConnectionSource;
import codeu.chat.client.Controller; 
import codeu.chat.client.ClientUser;
import codeu.chat.client.View;

@RunWith(MockitoJUnitRunner.class)
public final class ClientUserTest {


	private ClientUser user;

	@Mock
	private Controller controller;

	@Mock
	private View view;

	@Before
	public void doBefore() {

		user = new ClientUser(controller, view);

	}

	@Test
	public void testClientUserTest() {


		ArrayList<String> usersList = new ArrayList<String>();


		usersList.addAll(Arrays.asList("JESS", "jess", "SARAh", "SARAH", "mathangi", "MATHANGI"));

		Mockito.when(user.getAll()).thenReturn(usersList);

		assertTrue(user.isValidName("jess"));
		assertFalse(user.isValidName("SARAH"));

	}


}