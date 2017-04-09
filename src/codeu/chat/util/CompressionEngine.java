package codeu.chat.util;

import codeu.chat.common.Message;

import com.diogoduailibe.lzstring4j.LZString;

/**
 * @author Eric Zhuang, CodeU Project Group 6
 * @description Converts a message into a compressed string format in order
 * to minimize bandwidth when sending messages
*/
public final class CompressionEngine {

    /**
     * @param msg The message to be compressed
     * @return An unreadable String representation of the parameter message
     */
    public static String compressMessage(Message msg) {
        return LZString.compress(msg.toString());
    }

    /**
     * @pre packet must be a compressed Message
     * @param packet The entry to be decompressed
     * @return A message that represents the compressed packet passed in
     */
    public static Message decompressMessage(String packet) {
        return Message.fromString(LZString.decompress(packet));
    }
}
