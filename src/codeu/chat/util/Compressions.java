/**
 * @author Eric Zhuang, CodeU Project Group 6
 * @description Converts various objects into a compressed byte[] in order
 * to minimize bandwidth when sending messages. Also allows messages to be decompressed.
*/ 

package codeu.chat.util;

import codeu.chat.common.Message;
import codeu.chat.common.Conversation;
import codeu.chat.common.ConversationSummary;
import codeu.chat.common.User;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.util.zip.DataFormatException;  
import java.util.zip.Deflater;  
import java.util.zip.Inflater;

public final class Compressions{

	public static final Compression<byte[]> BYTES = new Compression<byte[]>(){

		/**
     	* @param data The bytes that are to be compressed
     	* @return A smaller byte array that can be decompressed back into itself
     	*/
		@Override
		public byte[] compress (byte[] data){

	        Deflater deflater = new Deflater();
	        deflater.setInput(data);

	        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);

	        //Is this needed?
	        deflater.finish();

	        byte[] buffer = new byte[100];
	        while (!deflater.finished()) {  
	            int count = deflater.deflate(buffer);
	            outputStream.write(buffer, 0, count);   
	        }
	        byte[] output = outputStream.toByteArray();

	        deflater.end();

	        return output;  

		}
    	/**
     	* @param data The bytes that are to be decompressed
     	* @return A larger byte array that represents the original data
    	 */
		@Override
		public byte[] decompress(byte[] data){

	        Inflater inflater = new Inflater();
	        inflater.setInput(data);

	        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);  
	        byte[] buffer = new byte[1024];  
	        while (!inflater.finished()) {
	            int count = 0;
	            try{
	                count = inflater.inflate(buffer);
	            }
	            catch (DataFormatException e){
	                e.printStackTrace();
	            } 
	            outputStream.write(buffer, 0, count);  
	        }
	        byte[] output = outputStream.toByteArray();

	        inflater.end();

        	return output;
        }
    };

    public static final Compression<Message> MESSAGE = new Compression<Message>(){

	    @Override
	    public byte[] compress(Message data){
	        ByteArrayOutputStream msgStream = new ByteArrayOutputStream();
	        try{
	            Message.toStream(msgStream, data);
	        }catch (IOException e){
	            e.printStackTrace();
	        }
	        byte[] byteMsg = msgStream.toByteArray();
	        return BYTES.compress(byteMsg);
	    }

	    @Override
	    public Message decompress(byte[] data) {

	        data = BYTES.decompress(data);

	        ByteArrayInputStream byteMsg = new ByteArrayInputStream(data);
	        //Must create a filler message in order to satisfy compiler
	        Message msg = new Message(Uuid.NULL, Uuid.NULL, Uuid.NULL, Time.now(), Uuid.NULL, "");
	        try {
	            msg = Message.fromStream(byteMsg);
	        }catch (IOException e){
	            e.printStackTrace();
	        }
	        return msg;
	    }
  };

  public static final Compression<Conversation> CONVERSATION = new Compression<Conversation>(){

    @Override
    public byte[] compress(Conversation data){
        ByteArrayOutputStream convoStream = new ByteArrayOutputStream();
        try{
          Conversation.toStream(convoStream, data);
        }catch (IOException e){
            e.printStackTrace();
        }
        byte[] byteConvo = convoStream.toByteArray();

        return BYTES.compress(byteConvo);
    }

    @Override
    public Conversation decompress(byte[] data){

      data = BYTES.decompress(data);

      ByteArrayInputStream byteConvo = new ByteArrayInputStream(data);

      Conversation convo = new Conversation(Uuid.NULL, Uuid.NULL, Time.now(), "");
      try {
        convo = Conversation.fromStream(byteConvo);
      }catch (IOException e){
          e.printStackTrace();
      }
      return convo;
    }

  };

  public static final Compression<ConversationSummary> CONVERSATION_SUMMARY = new Compression<ConversationSummary>(){

    @Override
    public byte[] compress(ConversationSummary data){

        ByteArrayOutputStream convoSummaryStream = new ByteArrayOutputStream();
        try{
          ConversationSummary.toStream(convoSummaryStream, data);
        }catch (IOException e){
            e.printStackTrace();
        }
        byte[] byteConvoSummary = convoSummaryStream.toByteArray();

        return Compressions.BYTES.compress(byteConvoSummary);

    }

    @Override
    public ConversationSummary decompress(byte[] data){

      data = Compressions.BYTES.decompress(data);

      ByteArrayInputStream byteConvoSummary = new ByteArrayInputStream(data);

      ConversationSummary convoSummary = new ConversationSummary(Uuid.NULL, Uuid.NULL, Time.now(), "");
      try {
        convoSummary = ConversationSummary.fromStream(byteConvoSummary);
      }catch (IOException e){
          e.printStackTrace();
      }
      return convoSummary;
    }
  };

  public static final Compression<User> USER = new Compression<User>(){

    @Override
    public byte[] compress(User data){

        ByteArrayOutputStream userStream = new ByteArrayOutputStream();
        try{
          User.toStream(userStream, data);
        }catch (IOException e){
            e.printStackTrace();
        }
        byte[] byteUser = userStream.toByteArray();

        return Compressions.BYTES.compress(byteUser);

    }

    @Override
    public User decompress(byte[] data){

      data = Compressions.BYTES.decompress(data);

      ByteArrayInputStream byteUser = new ByteArrayInputStream(data);

      User userSummary = new User(Uuid.NULL, "", Time.now());
      try {
        userSummary = User.fromStream(byteUser);
      }catch (IOException e){
          e.printStackTrace();
      }
      return userSummary;
    }
  };

}