//Backend Test of ClientUser method(s)

package codeu.chat.server;

import static org.junit.Assert.*;
import org.mockito.Mockito;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.plugins.MockMaker;
import org.mockito.InjectMocks;

import org.junit.Test;
import org.junit.Before;
import org.junit.runners.Suite.SuiteClasses;
import org.junit.runners.Suite;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import codeu.chat.common.BasicView;
import codeu.chat.common.Conversation;
import codeu.chat.common.ConversationSummary;
import codeu.chat.common.LogicalView;
import codeu.chat.common.Message;
import codeu.chat.common.SinglesView;
import codeu.chat.common.User;
import codeu.chat.util.Logger;
import codeu.chat.util.Time;
import codeu.chat.util.Uuid;
import codeu.chat.util.store.StoreAccessor;

@RunWith(MockitoJUnitRunner.class)
public final class ViewTest {

	Model model = Mockito.mock(Model.class);;

    View view; 
    
	@Before
	public void doBefore() {
		view = Mockito.spy(new View(model));
	}

	@Test
	public void testClientUserTest() {

		List<Message> messageList = new ArrayList<Message>();
		List<Message> returnValuesHello = new ArrayList<Message>(); 
		List<Message> returnValuesSarah = new ArrayList<Message>();
		List<Message> returnValuesNone = new ArrayList<Message>();
		
		Uuid Sarah = new Uuid(100); 
		Uuid Mathangi = new Uuid(200); 
		Uuid Jess = new Uuid(300); 
		
		Uuid one = new Uuid(1); 
		Uuid two = new Uuid(2); 
		Uuid three = new Uuid(3); 
		Uuid four = new Uuid(4); 
		Uuid five = new Uuid(5); 
		
		messageList.add(new Message(one, two, null, new Time(500), Sarah, "Hello World"));
		messageList.add(new Message(two, three, one, new Time(600), Jess, "What's up, Sarah?"));
		messageList.add(new Message(three, four, two, new Time(700), Sarah, "Nothing much, Jess!"));
		messageList.add(new Message(four, five, three, new Time(800), Mathangi, "Hey Sarah and Jess! How are we testing our code?"));
		messageList.add(new Message(five, null, four, new Time(900), Sarah, "Okay, I have to go! Talk to you all soon!"));
		
		returnValuesHello.add(messageList.get(0)); 
		Mockito.doReturn(returnValuesHello).when(view).searchMessages("Hello");
		
		returnValuesSarah.add(messageList.get(1)); 
		returnValuesSarah.add(messageList.get(3)); 
		Mockito.doReturn(returnValuesSarah).when(view).searchMessages("Sarah");
		
		Mockito.doReturn(returnValuesNone).when(view).searchMessages("Purple Duck");

		assertEquals(returnValuesHello, view.searchMessages("Hello"));
		assertEquals(returnValuesSarah, view.searchMessages("Sarah"));
		assertEquals(returnValuesNone, view.searchMessages("Purple Duck"));
	}


}