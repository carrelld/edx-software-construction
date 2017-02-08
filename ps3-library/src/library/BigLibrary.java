package library;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * BigLibrary represents a large collection of books that might be held by a city or
 * university library system -- millions of books.
 * 
 * In particular, every operation needs to run faster than linear time (as a function of the number of books
 * in the library).
 */
public class BigLibrary implements Library {

    // rep:
    private final Map<Book, BookList> collection = new HashMap<Book, BookList>();
    private final IQueryStrategy queryStrategy = new IndexedQuery();
    
    // rep invariant:
    //     collection contains no keys whose values lists both contain no elements
    // abstraction function:
    //     collection contains all the Book and their associated lists of available and checked out copies
    // safety from rep exposure argument
    //     Book is immutable in constructor
    //     All returned Set are defensively copied in their particular classes
    
    
    public BigLibrary() { }
    
    // assert the rep invariant
    private void checkRep() {
        // collection contains no keys whose values lists both contain no elements
        for (Book book : collection.keySet()) {
            BookList bl = collection.get(book);
            assert !bl.getAvailable().isEmpty() || !bl.getCheckedOut().isEmpty();
        }
    }

    @Override
    public BookCopy buy(Book book) {
        BookCopy copy = new BookCopy(book);
        BookList bl;
        if (collection.containsKey(book)) {
            bl = collection.get(book);
        } else {
            bl = new BookList();
            queryStrategy.index(book);
        }
        bl.checkin(copy);            
        collection.put(book, bl);
        checkRep();
        return copy;
    }
    
    @Override
    public void checkout(BookCopy copy) {
        Book book = copy.getBook();
        collection.get(book).checkout(copy);
    }

    
    @Override
    public void checkin(BookCopy copy) {
        Book book = copy.getBook();
        collection.get(book).checkin(copy);
    }
    
    @Override
    public Set<BookCopy> allCopies(Book book) {
        BookList bl = collection.containsKey(book) ? collection.get(book) : new BookList();
        Set<BookCopy> union = bl.getAvailable();
        union.addAll(bl.getCheckedOut());
        return union;
    }

    @Override
    public Set<BookCopy> availableCopies(Book book) {
        BookList bl = collection.containsKey(book) ? collection.get(book) : new BookList();
        return bl.getAvailable();
    }
    
    @Override
    public boolean isAvailable(BookCopy copy) {
        Book book = copy.getBook();
        BookList bl = collection.containsKey(book) ? collection.get(book) : new BookList();
        return bl.getAvailable().contains(copy);
    }
    
    /**
     * In addition to exact title and exact author matches, this will query on
     * space-separated keywords.
     * 
     * @param query
     *            Set of space-separated keywords or exact match string
     * @return The result is a union of all books containing one or more exact
     *         matching keyword. It should be ordered by exact match (keywords
     *         same order as query), then by percentage of characters in the
     *         title+author+year string that match the query keywords
     *         descending, then by title alphabetically, then by author
     *         alphabetically, then by Year descending.
     */
    @Override
    public List<Book> find(String query) {
        return queryStrategy.find(query, collection.keySet());
    }
    
    @Override
    public void lose(BookCopy copy) {
        Book book = copy.getBook();
        BookList bl = collection.containsKey(book) ? collection.get(book) : new BookList();
        bl.lose(copy);
        
        //remove book if it was last copy
        if (collection.containsKey(book) && allCopies(book).isEmpty()) {
            collection.remove(book);
        }
        checkRep();
    }

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}