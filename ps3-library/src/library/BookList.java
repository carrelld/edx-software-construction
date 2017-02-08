package library;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * BookList is a mutable class to track available and checked-out copies of a particular book
 * @author carredx
 *
 */
public class BookList {
    private Set<BookCopy> available = new HashSet<BookCopy>();
    private Set<BookCopy> checkedOut = new HashSet<BookCopy>();
    
    // rep invariant:
    //     all bookCopy.getBook are equal in the set available and checkedout
    //     checkedOut and available contain no overlapping BookCopys
    // abstraction function:
    //     the Set of BookCopy available, checkedOut are all the available, checked-out book copies for a specific book
    // safety from rep exposure argument
    //     All returned Set are defensively copied in their particular classes

    private void checkRep() {
        // No overlap between checkedOut and available
        Set<BookCopy> intersection = new HashSet<BookCopy>(available);
        intersection.retainAll(checkedOut);
        assert intersection.isEmpty();
        
        //All #getBook return the same book for every BookCopy in instance
        Set<BookCopy> allBookCopy = new HashSet<BookCopy>(available);
        allBookCopy.addAll(checkedOut);
        
        // empty set
        if (allBookCopy.isEmpty()) {
            assert true;
            return;
        }
        
        // non-empty set
        Iterator<BookCopy> it = allBookCopy.iterator();
        Book expectedBook = it.next().getBook();            
        
        for (BookCopy bc : allBookCopy) {
            assert bc.getBook().equals(expectedBook);
        }
    }
    
    public void checkin(BookCopy copy) {
        checkedOut.remove(copy);
        available.add(copy);
        checkRep();
    }

    public void checkout(BookCopy copy) {
        available.remove(copy);
        checkedOut.add(copy);
        checkRep();
    }

    public Set<BookCopy> getCheckedOut() {
        return new HashSet<BookCopy>(checkedOut);
    }

    public Set<BookCopy> getAvailable() {
        return new HashSet<BookCopy>(available);
    }
    
    public void lose(BookCopy copy) {
        available.remove(copy);
        checkedOut.remove(copy);
        checkRep();
    }
    
    
    
    
}
