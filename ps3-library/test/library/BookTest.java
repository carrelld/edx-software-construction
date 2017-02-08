package library;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test suite for Book ADT.
 */
public class BookTest {

    /*
     * Testing strategy
     * ==================
     * 
     * Methods:
     *   Book(), getTitle(), getAuthors(), getYear(), toString();
     *   
     * Partition on title length: 1, length > 1
     * Title spec validation: Case-sensitive, immutable
     * 
     * Partition on authors: 1, length > 1
     * Authors spec validation: order maintained, case-sensitive, immutable
     * 
     * Partition on year: 0, 1, 1899, 1900, 1999, 2000, 2016, Integer.MAX
     * Year spec validation: 
     * 
     * Other tests:
     * Verify class is immutable
     */
    
    @Test
    public void testMinimalBook() {
        // covers title and authors length 1, and year 0
        Book book = new Book("t", Arrays.asList("a"), 0);
        assertEquals("t", book.getTitle());
        assertEquals(0, book.getYear());
        assertEquals(1, book.getAuthors().size());
        assertTrue(book.getAuthors().contains("a"));
        
        assertTrue(book.toString().contains("t"));
        assertTrue(book.toString().contains("a"));
        assertTrue(book.toString().contains("0"));
    }
    @Test
    public void testStandardBook() {
        // covers title and authors greater than length 1, case-sensitive authors, 1999 year
        Book book = new Book("This is a test title", Arrays.asList("z", "first last", "FIRST LaSt", "a"), 1999);
        assertEquals("This is a test title", book.getTitle());
        assertEquals(1999, book.getYear());
        assertEquals(4, book.getAuthors().size());
        assertEquals("z", book.getAuthors().get(0));
        assertEquals("first last", book.getAuthors().get(1));
        assertEquals("FIRST LaSt", book.getAuthors().get(2));
        assertEquals("a", book.getAuthors().get(3));
    }
    @Test
    public void testPublishedYearBook() {
        // covers books in years 1, and Integer.MAX
        Book book1 = new Book("title", Arrays.asList("author"), 1);
        Book book2 = new Book("title", Arrays.asList("author"), Integer.MAX_VALUE);
        Book book3 = new Book("title", Arrays.asList("author"), 2000);
        Book book4 = new Book("title", Arrays.asList("author"), 2016);
        
        assertEquals(1, book1.getYear());
        assertEquals(Integer.MAX_VALUE, book2.getYear());
        assertEquals(2000, book3.getYear());
        assertEquals(2016, book4.getYear());

    }
    @Test
    public void testImmutableBook() {
        // covers class is immutable
        Book book = new Book("t", Arrays.asList("a"), 0);
        
        List<String> bAuthors = book.getAuthors();
        
        bAuthors.add("error");
        
        assertEquals("a", book.getAuthors().get(0));
        assertFalse(book.getAuthors().contains("error"));
        
    }
    
    @Test
    public void testEqualsHashCode() {
        // tests equals and hashCode behavior
        
        // title case matters
        Book b1 = new Book("title", Arrays.asList("author"), 2000);
        Book b2 = new Book("Title", Arrays.asList("author"), 2000);
        Book b3 = new Book("title", Arrays.asList("author"), 2000);
        
        assertEquals(b1, b3);
        assertNotEquals(b1, b2);
        assertEquals(b1.hashCode(), b3.hashCode());
        assertNotEquals(b1.hashCode(), b2.hashCode());
        
        // author case and order matters
        Book a1 = new Book("title", Arrays.asList("author1", "author2"), 2000);
        Book a2 = new Book("title", Arrays.asList("author1", "author2"), 2000);
        Book a3 = new Book("title", Arrays.asList("author2", "author1"), 2000);
        
        assertEquals(a1, a2);
        assertNotEquals(a1, a3);
        assertEquals(a1.hashCode(), a2.hashCode());
        assertNotEquals(a1.hashCode(), a3.hashCode());
        
        // year matters
        Book y1 = new Book("title", Arrays.asList("author"), 2000);
        Book y2 = new Book("title", Arrays.asList("author"), 2000);
        Book y3 = new Book("title", Arrays.asList("author"), 2015);
        
        assertEquals(y1, y2);
        assertNotEquals(y1, y3);
        assertEquals(y1.hashCode(), y2.hashCode());
        assertNotEquals(y1.hashCode(), y3.hashCode());
    }
    
    @Test(expected=AssertionError.class)
    public void testBadTitle() {
        new Book("    ", Arrays.asList("author"), 1);
    }
    @Test(expected=AssertionError.class)
    public void testBadAuthor() {
        new Book("title", new ArrayList<String>(), 1);
    }
    @Test(expected=AssertionError.class)
    public void testBadYear() {
        new Book("title", Arrays.asList("author"), -1);
    }

    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}
