/**
 * @author Eric Zhuang, CodeU Project Group 6
 * @description Testing suite for compression
*/

package codeu.chat.compression;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import codeu.chat.util.Time;
import codeu.chat.util.Uuid;
import codeu.chat.common.Message;
import codeu.chat.common.ConversationSummary;
import codeu.chat.common.Conversation;
import codeu.chat.common.User;
import codeu.chat.util.Compressions;

public final class CompressionTest{

	private Uuid author, id, next, prev;
	private final Time time = Time.now();
	private Message testMsg;
	private ConversationSummary testConvoSummary;
	private Conversation testConvo;
	private User testUser;

	//Note: since we are likely doing away with Uuids, many of these tests will need to be changed
	@Before
	public void setupUuids(){
		final String authString = "50";
		final String ids = "100.200.300.400.500.600.700.800";
		author = Uuid.fromString(authString);
		next = Uuid.fromString(ids);
		id = next.root();
		prev = id.root();
	}

	@Before
	public void setupTestMessage(){
		testMsg = new Message(id, next, prev, time, author, "I am a test message!\nPlease compress me!!!");
	}

	@Before
	public void setupTestConvoSummary(){
		testConvoSummary = new ConversationSummary(id, author, time, "This is the summary of a conversation between users foo and bar");
	}

	@Before
	public void setupTestConvo(){
		testConvo = new Conversation(id, author, time, "This is another conversation between users foo and bar. Additionally, users have been added to the hash map.");
		testConvo.users.add(author);
		testConvo.users.add(next);
	}

	//Question for ruiqi: what to do with tokens?
	@Before
	public void setupUser(){
		testUser = new User(author, "Mr. Foooooo", time);
	}

	@Test
	public void testMessageCompression(){
		Message copy = Compressions.MESSAGE.decompress(Compressions.MESSAGE.compress(testMsg));
		assertTrue(Message.equals(testMsg, copy));
	}

	@Test
	public void testMessageReadWrite(){
		try{
            ByteArrayOutputStream output = new ByteArrayOutputStream();
			Message.SERIALIZER.write(output, testMsg);
			ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
			assertTrue(Message.equals(testMsg, Message.SERIALIZER.read(input)));
        }catch (IOException e){
            e.printStackTrace();
        }
	}

	@Test
	public void testConvoSummaryCompression(){
		ConversationSummary copy = Compressions.CONVERSATION_SUMMARY.decompress(Compressions.CONVERSATION_SUMMARY.compress(testConvoSummary));
		assertTrue(ConversationSummary.equals(testConvoSummary, copy));
	}

	@Test
	public void testConvoSummaryReadWrite(){
		try{
            ByteArrayOutputStream output = new ByteArrayOutputStream();
			ConversationSummary.SERIALIZER.write(output, testConvoSummary);
			ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
			assertTrue(ConversationSummary.equals(testConvoSummary, ConversationSummary.SERIALIZER.read(input)));
        }catch (IOException e){
            e.printStackTrace();
        }
	}

	@Test
	public void testConvoCompression(){
		Conversation copy = Compressions.CONVERSATION.decompress(Compressions.CONVERSATION.compress(testConvo));
		assertTrue(Conversation.equals(testConvo, copy));
	}

	@Test
	public void testConvoReadWrite(){
		try{
            ByteArrayOutputStream output = new ByteArrayOutputStream();
			Conversation.SERIALIZER.write(output, testConvo);
			ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
			assertTrue(Conversation.equals(testConvo, Conversation.SERIALIZER.read(input)));
        }catch (IOException e){
            e.printStackTrace();
        }
	}

	@Test
	public void testUserCompression(){
		User copy = Compressions.USER.decompress(Compressions.USER.compress(testUser));
		assertTrue(User.equals(testUser, copy));
	}

	@Test
	public void testUserReadWrite(){
		try{
            ByteArrayOutputStream output = new ByteArrayOutputStream();
			User.SERIALIZER.write(output, testUser);
			ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
			assertTrue(User.equals(testUser, User.SERIALIZER.read(input)));
        }catch (IOException e){
            e.printStackTrace();
        }
	}
	
}