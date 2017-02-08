package library;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Book is an immutable type representing an edition of a book -- not the physical object, 
 * but the combination of words and pictures that make up a book.  Each book is uniquely
 * identified by its title, author list, and publication year.  Alphabetic case and author 
 * order are significant, so a book written by "Fred" is different than a book written by "FRED".
 */
public class Book {

    // TODO: rep
    private final String title;
    private final List<String> authors;
    private final int year;
    
    // TODO: rep invariant
    //   year always greater than 0
    //   author list always has at least 1 author
    //   title contains at least 1 non-space character
    // TODO: abstraction function
    // TODO: safety from rep exposure argument
    //   constructor copies List of authors rather than assign
    //   title is immutable String
    //   authors List is copied before returned
    //   year is primative int, so already copied when returned
    
    /**
     * Make a Book.
     * @param title Title of the book. Must contain at least one non-space character.
     * @param authors Names of the authors of the book.  Must have at least one name, and each name must contain 
     * at least one non-space character.
     * @param year Year when this edition was published in the conventional (Common Era) calendar.  Must be nonnegative. 
     */
    public Book(String title, List<String> authors, int year) {
        this.title = title;
        this.authors = new ArrayList<String>(authors);
        this.year = year;
        
        this.checkRep();
    }
    
    // assert the rep invariant
    private void checkRep() {
        assert year >= 0;
        assert authors != null;
        assert authors.size() >= 1;
        assert title != null;
        assert title.matches(".*[^ ]+.*");
    }
    
    /**
     * @return the title of this book
     */
    public String getTitle() {
        return this.title;
    }
    
    /**
     * @return the authors of this book
     */
    public List<String> getAuthors() {
        return new ArrayList<String>(authors);
    }

    /**
     * @return the year that this book was published
     */
    public int getYear() {
        return this.year;
    }

    /**
     * @return human-readable representation of this book that includes its title,
     *    authors, and publication year
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%1s (%2s) | ", this.title, this.year));
        Iterator<String> authorIterator = authors.iterator();
        if (authorIterator.hasNext()) {
            sb.append(authorIterator.next());
            while (authorIterator.hasNext()) {
                sb.append("; ");                
                sb.append(authorIterator.next());
            }
        }
        return sb.toString();
        
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + authors.hashCode();
        result = prime * result + title.hashCode();
        result = prime * result + year;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Book)) {
            return false;
        }
        Book other = (Book) obj;
        if (!authors.equals(other.authors)
                | !title.equals(other.title)
                | year != other.year) {
            return false;
        }
        return true;
    }

    public boolean sameAs(Book o2) {
        return this.getTitle().equals(o2.getTitle()) && this.getAuthors().equals(o2.getAuthors());
    }

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}
