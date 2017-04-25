/**
 * @author Eric Zhuang, CodeU Project Group 6
 * @description Outlines the interface for the Compression engine
*/

package codeu.chat.util;

//Question: Do I need to throw IO exceptions like in the Serializer??
public interface Compression<T>{

    /**
     * @param data The object to be compressed
     * @return A byte array representation of the parameter oobject
     */
	byte[] compress(T data);

    /**
     * @pre data must be a compressed version of type T
     * @param data The entry to be decompressed
     * @return An object that represents the compressed packet passed in
     */
	T decompress(byte[] data);

}