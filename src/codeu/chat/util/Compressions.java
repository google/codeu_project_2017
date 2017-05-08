/**
 * @author Eric Zhuang, CodeU Project Group 6
 * @description A specific compression implementation for byte[]
*/ 

package codeu.chat.util;

import java.io.IOException;
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
}