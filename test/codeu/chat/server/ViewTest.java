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


public final class ViewTest {
  final Model model = new Model();
  View view;

  @Test
  public void testSearchMessages() {

    List<Message> returnValuesHello = new ArrayList<Message>();
    List<Message> returnValuesSarah = new ArrayList<Message>();
	List<Message> returnValuesJess = new ArrayList<Message>();
	List<Message> returnValuesNone = new ArrayList<Message>();

	Uuid Sarah = new Uuid(100);
	Uuid Mathangi = new Uuid(200);
	Uuid Jess = new Uuid(300);

	Uuid one = new Uuid(1);
	Uuid two = new Uuid(2);
	Uuid three = new Uuid(3);
	Uuid four = new Uuid(4);
	Uuid five = new Uuid(5);
		
	Uuid conversation = new Uuid(1000); 
	Uuid conversation2 = new Uuid(2000); 
		
	Conversation conversationConv = new Conversation(conversation, Sarah, new Time(450), "conversation"); 
	Conversation conversation2Conv = new Conversation(conversation2, Sarah, new Time(850), "conversation2"); 
		
	//ensure all users are part of the conversations 
	conversationConv.users.add(Sarah); 
	conversationConv.users.add(Jess); 
	conversationConv.users.add(Mathangi); 
		
	conversation2Conv.users.add(Sarah); 
		
	//create and add the conversations to the model
	model.add(conversationConv); 
	model.add(conversation2Conv);  

	Message mOne = new Message(one, two, null, new Time(500), Sarah, "Hello World", conversation);
	Message mTwo = new Message(two, three, one, new Time(600), Jess, "What's up, Sarah?", conversation);
	Message mThree = new Message(three, four, two, new Time(700), Sarah, "Nothing much, Jess!", conversation);
	Message mFour = new Message(four, five, three, new Time(800), Mathangi, "Hey Sarah and Jess! How are we testing our code?", conversation);
	Message mFive = new Message(five, null, four, new Time(900), Sarah, "Okay, I have to go! Talk to you all soon!", conversation2);

    //add the messages to the model
	model.add(mOne);
	model.add(mTwo);
	model.add(mThree);
	model.add(mFour);
	model.add(mFive);
		 
    //create the view
	view = new View(model);
		
	//set up the test actual results to compare
	returnValuesHello.add(mOne);
	returnValuesSarah.add(mFour);
	returnValuesSarah.add(mTwo);
	returnValuesJess.add(mFour);
	returnValuesJess.add(mThree); 

	assertEquals(returnValuesHello, view.searchMessages(conversation, Sarah, "Hello"));
	assertEquals(returnValuesJess, view.searchMessages(conversation, Jess, "Jess"));
	assertEquals(returnValuesSarah, view.searchMessages(conversation, Jess,  "Sarah"));
	assertEquals(returnValuesNone, view.searchMessages(conversation2, Sarah, "Purple Duck"));
	assertEquals(returnValuesNone, view.searchMessages(conversation2, Mathangi, "Talk"));
	assertEquals(returnValuesNone, view.searchMessages(conversation, Sarah, "Purple Duck"));
  }
}