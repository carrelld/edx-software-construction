package library;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;

/**
 * Test suite for BigLibrary's stronger specs.
 */
public class BigLibraryTest {
    
    /* 
     * NOTE: use this file only for tests of BigLibrary.find()'s stronger spec.
     * Tests of all other Library operations should be in LibraryTest.java 
     */

    /*
     * Testing strategy
     * ==================
     * 
     * find()
     *   0 copies, 1 copy, >1 copy in lib
     *   0 matched book, 1 matched book, >1 matched book
     *   Book with same keywords in title: sorted, out of order
     *   empty query, query len > 0
     *   1 keyword, >1 keyword
     *   ordered exact match, out of order exact match
     *  ** many of these test cases are covered in LibraryTest.java
     */
    
    // TODO: put JUnit @Test methods here that you developed from your testing strategy
    @Test
    public void testExampleTest() {
        // this is just an example test, you should delete it
        Library library = new BigLibrary();
        assertEquals(Collections.emptyList(), library.find("This Test Is Just An Example"));
    }
    @Test
    public void testFindMatchKeywords() {
        Library library = new BigLibrary();
        Book book1 = new Book("This Is Home", Arrays.asList("Author"), 1900);
        Book book2 = new Book("Home Is This", Arrays.asList("Author"), 1900);
        Book book3 = new Book("Home Is", Arrays.asList("Author", "This"), 2000);
        
        
        library.buy(book2);
        library.buy(book2);
        library.buy(book3);
        library.buy(book3);
        library.buy(book1);
        
        // 1 keyword
        assertEquals(Collections.emptyList(), library.find("Test"));
        // >1 keyword
        assertEquals(Collections.emptyList(), library.find("Test Query"));
        // 1 matched book, >1 copy
        assertEquals(Arrays.asList(book3), library.find("2000"));
        // same keywords, different order; >1 matched book
        assertEquals(Arrays.asList(book1, book3, book2), library.find("This Is Home"));
        // Same keywords across book fields
        assertEquals(Arrays.asList(book3, book2, book1), library.find("Home Is"));

    }
    @Test
    public void testFindUnionMatchedKeywords() {
        Library library = new BigLibrary();
        Book book1 = new Book("This", Arrays.asList("Author"), 1900);
        Book book2 = new Book("Island", Arrays.asList("Author"), 1900);
        Book book3 = new Book("Home", Arrays.asList("Author"), 2000);
        
        // added out of order
        library.buy(book3);
        library.buy(book2);
        library.buy(book1);
        
        // multiple keywords no exact match
        assertEquals(Arrays.asList(book2, book3), library.find("Home Island"));
        
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
