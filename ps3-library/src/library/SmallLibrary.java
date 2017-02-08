package library;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** 
 * SmallLibrary represents a small collection of books, like a single person's home collection.
 */
public class SmallLibrary implements Library {

    // This rep is required! 
    // Do not change the types of inLibrary or checkedOut, 
    // and don't add or remove any other fields.
    // (BigLibrary is where you can create your own rep for
    // a Library implementation.)

    // rep
    private Set<BookCopy> inLibrary;
    private Set<BookCopy> checkedOut;
    
    // rep invariant:
    //    the intersection of inLibrary and checkedOut is the empty set
    //
    // abstraction function:
    //    represents the collection of books inLibrary union checkedOut,
    //      where if a book copy is in inLibrary then it is available,
    //      and if a copy is in checkedOut then it is checked out

    // safety from rep exposure argument:
    //    inLibrary and checkedOut are private. Constructor copies are made before returning from public methods
    //    TODO The BookCopy objects returned as part of the Observer functions are reference equal to the library's rep. This is a problem since they are mutable
    
    public SmallLibrary() {
        inLibrary = new HashSet<BookCopy>();
        checkedOut = new HashSet<BookCopy>();
        checkRep();
    }
    
    // assert the rep invariant
    // run at every creator, mutator, producer
    private void checkRep() {
        Set<BookCopy> intersection = new HashSet<BookCopy>(inLibrary);
        intersection.retainAll(checkedOut);
        assert intersection.isEmpty();
    }

    @Override
    public BookCopy buy(Book book) {
        BookCopy copy = new BookCopy(book);
        inLibrary.add(copy);
        checkRep();
        return copy;
    }
    
    @Override
    public void checkout(BookCopy copy) {
        if (inLibrary.remove(copy)) {
            checkedOut.add(copy);
        } else {
            throw new InvalidParameterException("This copy is not available for checkout");
        }
        checkRep();
    }
    
    @Override
    public void checkin(BookCopy copy) {
        if (checkedOut.remove(copy)) {
            inLibrary.add(copy);
        } else {            
            throw new InvalidParameterException("This copy is not available for checkin");
        }
        checkRep();
    }
    
    @Override
    public boolean isAvailable(BookCopy copy) {
        return inLibrary.contains(copy);
    }
    
    @Override
    public Set<BookCopy> allCopies(Book book) {
        Set<BookCopy> copies = availableCopies(book);
        copies.addAll(getCopies(book, checkedOut));
        return copies;
    }
    
    @Override
    public Set<BookCopy> availableCopies(Book book) {
        return getCopies(book, inLibrary);
    }

    @Override
    public List<Book> find(String query) {
        // all book copies set
        Set<BookCopy> union = new HashSet<BookCopy>(inLibrary);
        union.addAll(checkedOut);
        
        //find
        Set<Book> foundBooks = new HashSet<Book>();
        for (BookCopy copy : union) {
            Book book = copy.getBook();
            // title match
            if (book.getTitle().equals(query)) {
                foundBooks.add(book);
            }
            // author match
            if (book.getAuthors().contains(query)) {
                foundBooks.add(book);
            }
        }
        
        //sort and convert
        List<Book> books = new ArrayList<Book>(foundBooks);
        Comparator<Book> byYear = (Book o1, Book o2)->Integer.compare(o2.getYear(), o1.getYear()); //descending by year
        Collections.sort(books, byYear);
        return books;
    }
    
    @Override
    public void lose(BookCopy copy) {
        if (!inLibrary.remove(copy)) {
            if (!checkedOut.remove(copy)) {
                throw new InvalidParameterException("This copy does not exist in library");
            }
        }
        checkRep();
    }
    
    private Set<BookCopy> getCopies(Book book, Collection<BookCopy> c) {
        Set<BookCopy> copies = new HashSet<BookCopy>();
        for (BookCopy b : c) {
            if (b.getBook().equals(book)) { copies.add(b); } 
        }
        return copies;
    }

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
