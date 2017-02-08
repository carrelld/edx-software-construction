package library;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import library.BookCopy.Condition;

/**
 * Test suite for Library ADT.
 */
@RunWith(Parameterized.class)
public class LibraryTest {

    /*
     * Note: all the tests you write here must be runnable against any
     * Library class that follows the spec.  JUnit will automatically
     * run these tests against both SmallLibrary and BigLibrary.
     */

    /**
     * Implementation classes for the Library ADT.
     * JUnit runs this test suite once for each class name in the returned array.
     * @return array of Java class names, including their full package prefix
     */
    @Parameters(name="{0}")
    public static Object[] allImplementationClassNames() {
        return new Object[] { 
            "library.SmallLibrary", 
            "library.BigLibrary"
        }; 
    }

    /**
     * Implementation class being tested on this run of the test suite.
     * JUnit sets this variable automatically as it iterates through the array returned
     * by allImplementationClassNames.
     */
    @Parameter
    public String implementationClassName;    

    /**
     * @return a fresh instance of a Library, constructed from the implementation class specified
     * by implementationClassName.
     */
    public Library makeLibrary() {
        try {
            Class<?> cls = Class.forName(implementationClassName);
            return (Library) cls.newInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    /*
     * Testing strategy
     * ==================
     * 
     * TODO: your testing strategy for this ADT should go here.
     * Make sure you have partitions.
     * 
     * Methods:
     *   buy; lib has copy, doesn't have copy
     *   checkout; lib has 1, lib has >1
     *   checkin; lib has 1, lib has >1
     *   isAvailable; lib has no copies, lib has 1 available, lib has 1 not available
     *   allCopies; lib has no copies, lib has 1 available, lib has 1 checked-out, lib has many available and checked out
     *   availableCopies; lib has no copies, lib has 1 available, lib has 1 checked-out, lib has many available and checked out
     *   find;
     *     0 copies, 1 copy, >1 copy
     *     matches 0 books, 1 book, >1 book
     *     author match, title match, no match
     *     same titles, authors; different years sorted correctly
     *   lose; lib has copy available, lib has copy checked out, lib has many copies
     *   
     * 
     */
    
    // TODO: put JUnit @Test methods here that you developed from your testing strategy
    @Test
    public void testExampleTest() {
        Library library = makeLibrary();
        Book book = new Book("This Test Is Just An Example", Arrays.asList("You Should", "Replace It", "With Your Own Tests"), 1990);
        assertEquals(Collections.emptySet(), library.availableCopies(book));
    }
    @Test
    public void testBuy() {
        // Covers lib has and doesn't have copy of a book
        Library library = makeLibrary();
        Book book = new Book("title", Arrays.asList("author"), 1900);
        
        BookCopy newCopy = library.buy(book);
        assertTrue(library.isAvailable(newCopy));
        assertEquals(Condition.GOOD, newCopy.getCondition());
        
        // see what copies exist in the library
        Set<BookCopy> copies = library.allCopies(book);
        assertEquals(1, copies.size());
        assertEquals(book, copies.iterator().next().getBook());
        
        BookCopy duplicateCopy = library.buy(book);
        assertEquals(Condition.GOOD, duplicateCopy.getCondition());
        
        // make sure quantity was updated
        copies = library.allCopies(book);
        assertEquals(2, copies.size());
        for (BookCopy bc : copies) {
            assertEquals(book, bc.getBook());
            assertEquals(Condition.GOOD, bc.getCondition());
        }
    }
    @Test
    public void testCheckoutReturnOneCopy() {
        Library library = makeLibrary();
        Book book = new Book("title", Arrays.asList("author"), 1900);
        BookCopy copy = library.buy(book);
        
        //library has 1 copy
        assertTrue(library.isAvailable(copy));
        library.checkout(copy);
        assertFalse(library.isAvailable(copy));
        
        //checkin
        library.checkin(copy);
        assertTrue(library.isAvailable(copy));
    }
    @Test
    public void testCheckoutReturnMoreThanOneCopy() {
        Library library = makeLibrary();
        Book book = new Book("title", Arrays.asList("author"), 1900);
        BookCopy copy = library.buy(book);
        BookCopy duplicate = library.buy(book);
        
        //library has more than 1 copy
        assertTrue(library.isAvailable(copy));
        assertTrue(library.isAvailable(duplicate));
        library.checkout(copy);
        assertFalse(library.isAvailable(copy));
        assertTrue(library.isAvailable(duplicate));
        library.checkout(duplicate);
        assertFalse(library.isAvailable(copy));
        assertFalse(library.isAvailable(duplicate));
        
        //checkin
        library.checkin(copy);
        assertTrue(library.isAvailable(copy));
        assertFalse(library.isAvailable(duplicate));
        library.checkin(duplicate);
        assertTrue(library.isAvailable(copy));
        assertTrue(library.isAvailable(duplicate));
    }
    @Test
    public void testIsAvailable() {
        Library library = makeLibrary();
        Book book = new Book("title", Arrays.asList("author"), 1900);
        
        //no copies
        assertFalse(library.isAvailable(new BookCopy(book)));
        
        //one copy
        BookCopy copy = library.buy(book);
        assertTrue(library.isAvailable(copy));
        
        //one copy not available
        library.checkout(copy);
        assertFalse(library.isAvailable(copy));
    }
    @Test
    public void testAllCopiesAvailableCopies() {
        Library library = makeLibrary();
        Book book = new Book("title", Arrays.asList("author"), 1900);
        
        //lib has no copies
        Set<BookCopy> libNoCopy = Collections.emptySet();
        assertEquals(libNoCopy, library.allCopies(book));
        assertEquals(libNoCopy, library.availableCopies(book));
        
        //lib has 1 available
        BookCopy copy = library.buy(book);
        Set<BookCopy> libOneAvailable = new HashSet<BookCopy>(Arrays.asList(copy));
        assertEquals(libOneAvailable, library.allCopies(book));
        assertEquals(libOneAvailable, library.availableCopies(book));
        
        //lib has 1 checked-out
        library.checkout(copy);
        assertEquals(libOneAvailable, library.allCopies(book));
        assertEquals(libNoCopy, library.availableCopies(book));
        
        //lib has many available and checked out
        BookCopy duplicate = library.buy(book);
        Set<BookCopy> libTwoAvailable = new HashSet<BookCopy>(Arrays.asList(copy, duplicate));
        Set<BookCopy> libDupAvailable = new HashSet<BookCopy>(Arrays.asList(duplicate));
        assertEquals(libTwoAvailable, library.allCopies(book));
        assertEquals(libDupAvailable, library.availableCopies(book));
        
        //>one all checked out
        library.checkout(duplicate);
        assertEquals(libTwoAvailable, library.allCopies(book));
        assertEquals(libNoCopy, library.availableCopies(book));
        

    }
    @Test
    public void testFind() {
        Library library = makeLibrary();
        Book book1 = new Book("holes", Arrays.asList("stephen"), 2000);
        Book book2 = new Book("sphere", Arrays.asList("stephen"), 2000);
        Book book3 = new Book("sphere: the sequel", Arrays.asList("stephen"), 2016);
        
        //empty lib, 0 copies, 0 books, no match
        List<Book> libEmpty = library.find("holes");
        assertEquals(Collections.emptyList(), libEmpty);
        
        //title match, 1 book, 1 copy
        library.buy(book1);
        assertEquals(Arrays.asList(book1), library.find("holes"));
        
        //author match, >1 book, >1 copy
        library.buy(book1);
        library.buy(book2);
        library.buy(book3);
        List<Book> authorMatches = library.find("stephen");
        List<Book> expectedMatches = Arrays.asList(book1, book2, book3);
        assertTrue(expectedMatches.containsAll(authorMatches));
        assertEquals(expectedMatches.size(), authorMatches.size());
        
        // same titles, authors; different years sorted
        Book dup1 = new Book("Bible", Arrays.asList("Jeebus"), 900);
        Book dup2 = new Book("Bible", Arrays.asList("Jeebus"), 1);
        Book dup3 = new Book("Bible", Arrays.asList("Jeebus"), 2015);
        
        library.buy(dup1);
        library.buy(dup2);
        library.buy(dup3);
        
        List<Book> expectedOrder = Arrays.asList(dup3, dup1, dup2); //descending by date
        
        assertEquals(expectedOrder, library.find("Bible"));

    }
    @Test
    public void testLose() {
        Library library = makeLibrary();
        Book book = new Book("title", Arrays.asList("author"), 1900);
        
        //lib has copy available
        BookCopy order1 = library.buy(book);
        assertTrue(library.isAvailable(order1));
        library.lose(order1);
        assertFalse(library.isAvailable(order1));
        assertEquals(Collections.emptySet(), library.allCopies(book));
        
        //lib has copy checked out
        BookCopy order2 = library.buy(book);
        library.checkout(order2);
        library.lose(order2);
        assertFalse(library.isAvailable(order2));
        assertEquals(Collections.emptySet(), library.allCopies(book));
        
        //lib has many copies
        BookCopy order3 = library.buy(book);
        BookCopy order4 = library.buy(book);
        library.lose(order3);
        assertFalse(library.isAvailable(order3));
        assertTrue(library.isAvailable(order4));
        assertEquals(new HashSet<BookCopy>(Arrays.asList(order4)), library.allCopies(book));
    }
    @Test
    public void testEqualsHashCode() {
        Library lib1 = makeLibrary();
        Library lib2 = makeLibrary();
        Book book = new Book("title", Arrays.asList("author"), 1900);
        
        // reference equality (since library is mutable)
        assertTrue(lib1.equals(lib1));
        assertTrue(lib1.hashCode() == lib1.hashCode());
        
        // different states
        lib1.buy(book);
        assertFalse(lib1.equals(lib2));
        
        // same, non-empty state
        lib2.buy(book);
        assertFalse(lib1.equals(lib2));
        
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
