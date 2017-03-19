package codeu.chat.common;

import org.junit.Test;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by ataylor on 3/12/17.
 */
public class BTreeTest {
    @Test
    public void testInsert() {
        BTree<Integer> test = new BTree<>(4, Integer::compareTo);
        int[] numsToTest = new int[]{
            5, 33, 29, 7, 18, 81, 44, 12, 93, 27, 63, 19, 31, -5, 99
        };
        for (int i = 0; i < numsToTest.length; i++) {
            test = test.add(numsToTest[i]);
        }
        assertEquals(test.toString(), "-5 5 7 12 18 19 27 29 31 33 44 63 81 93 99");
    }

    @Test
    public void testDuplicates() {
        BTree<Integer> test = new BTree<>(4, Integer::compareTo);
        int[] numsToTest = new int[]{
            5, 33, 29, 7, 18, 81, 44, 12, 93, 27, 63, 19, 31, -5, 99
        };
        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < numsToTest.length; i++) {
                test = test.add(numsToTest[i]);
            }
        }
        assertEquals("-5 5 7 12 18 19 27 29 31 33 44 63 81 93 99", test.toString());
    }

    @Test
    public void testContains() {
        BTree<String> test = new BTree<>(4, String::compareTo);
        String[] stringsToTest = "abc def ghi jklmn abe mno".split(" ");
        for (int i = 0; i < stringsToTest.length; i++) {
            test = test.add(stringsToTest[i]);
        }
        for (int i = 0; i < stringsToTest.length; i++) {
            assertNotNull(test.contains(stringsToTest[i]));
            assert(test.contains(stringsToTest[i]) == stringsToTest[i]);
        }
        assertNull(test.contains("abc def"));
        assertNull(test.contains(""));
    }
}
