package library;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class IQueryStrategyTest {

    @Test
    public void testIndexedQueryGetKeywords() {
        IndexedQuery query = new IndexedQuery();
        Book book = new Book("Title Text", Arrays.asList("First Author", "Second Author"), 1900);
        
        Set<String> expectedKeywords = new HashSet<String>(Arrays.asList("1900", "Title Text", "Title", "Text", "First Author", "Second Author", "First", "Second", "Author"));
        Set<String> observedKeywords = query.getKeywords(book);
        
        assertEquals(expectedKeywords, observedKeywords);
    }
    @Test
    public void testIndexedQueryIndex() {
        IndexedQuery query = new IndexedQuery();
        Book book = new Book("Title", Arrays.asList("Author"), 1900);
        
        query.index(book);
        
        Map<String, Set<Book>> index = query.getIndex();
        
        assertEquals(3, index.size());
        assertTrue(index.containsKey("Title"));
        assertTrue(index.containsKey("Author"));
        assertTrue(index.containsKey("1900"));
        
        Set<Book> expected = new HashSet<Book>(Arrays.asList(book));
        
        assertEquals(expected, index.get("Title"));
        assertEquals(expected, index.get("Author"));
        assertEquals(expected, index.get("1900"));
        
    }

    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
}
