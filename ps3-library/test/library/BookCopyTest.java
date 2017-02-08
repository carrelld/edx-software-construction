package library;

import org.junit.Test;

import library.BookCopy.Condition;

import static org.junit.Assert.*;

import java.util.Arrays;

/**
 * Test suite for BookCopy ADT.
 */
public class BookCopyTest {

    /*
     * Testing strategy
     * ==================
     * 
     * Methods: BookCopy(), getBook(), getCondition(), setCondition(), toString()
     * 
     * condition partition on: GOOD, DAMAGED
     */
    
    // TODO: put JUnit @Test methods here that you developed from your testing strategy
    @Test
    public void testNormalBook() {
        // tests getBook(), getCondition(), and toString()
        Book book = new Book("title", Arrays.asList("author"), 2000);
        BookCopy copy = new BookCopy(book);
        
        assertEquals(book, copy.getBook());
        assertEquals(Condition.GOOD, copy.getCondition());
        assertTrue(copy.toString().toLowerCase().contains(book.toString().toLowerCase()));
        assertTrue(copy.toString().toLowerCase().contains("good"));
    }
    
    @Test
    public void testMutableCondition() {
        Book book = new Book("title", Arrays.asList("author"), 2000);
        BookCopy copy = new BookCopy(book);
        
        assertEquals(Condition.GOOD, copy.getCondition());
        
        copy.setCondition(Condition.DAMAGED);
        
        assertEquals(Condition.DAMAGED, copy.getCondition());
        
        copy.setCondition(Condition.GOOD);
        
        assertEquals(Condition.GOOD, copy.getCondition());
    }
    
    @Test
    public void testEqualsHashCode() {
        // tests equals and hashCode behavior
        Book book = new Book("title", Arrays.asList("author"), 2000);

        BookCopy copy1 = new BookCopy(book);
        BookCopy copy2 = new BookCopy(book);
        
        assertNotEquals(copy1, copy2);
        assertEquals(copy1.getBook(), copy2.getBook());
    }
    
    @Test(expected=AssertionError.class)
    public void testNullBook() {
        new BookCopy(null);
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
