package library;

/**
 * BookCopy is a mutable type representing a particular copy of a book that is held in a library's
 * collection.
 */
public class BookCopy {

    // TODO: rep
    private Condition condition;
    private final Book book;
    
    // TODO: rep invariant
    //   condition not null and book not null
    // TODO: abstraction function
    //   represents a single copy of a physical book as a reference to a Book object and it's physical condition as "GOOD" or "DAMAGED"
    // TODO: safety from rep exposure argument
    //   returned condition enum does not contain mutators, so returning reference to original object is safe
    //   return original reference to book is safe since book is immutable and final
    
    public static enum Condition {
        GOOD, DAMAGED
    };
    
    /**
     * Make a new BookCopy, initially in good condition.
     * @param book the Book of which this is a copy
     */
    public BookCopy(Book book) {
        this.book = book;
        this.condition = Condition.GOOD;
        
        this.checkRep();
    }
    
    // assert the rep invariant
    private void checkRep() {
        assert this.book != null;
    }
    
    /**
     * @return the Book of which this is a copy
     */
    public Book getBook() {
        return this.book;
    }
    
    /**
     * @return the condition of this book copy
     */
    public Condition getCondition() {
        return this.condition;
    }

    /**
     * Set the condition of a book copy.  This typically happens when a book copy is returned and a librarian inspects it.
     * @param condition the latest condition of the book copy
     */
    public void setCondition(Condition condition) {
        this.condition = condition;
    }
    
    /**
     * @return human-readable representation of this book that includes book.toString()
     *    and the words "good" or "damaged" depending on its condition
     */
    public String toString() {
        return this.book.toString() + "\nCondition: " + this.condition.toString().toLowerCase();
    }

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}
