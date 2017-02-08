package library;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Strategy pattern interface for library query
 * @author carredx
 *
 */
public interface IQueryStrategy {
    public List<Book> find(String query, Set<Book> books);
    default public void index(Book book) { return; }
}

/**
 * Basic implementation adhering to the minimum requirements of exact author,
 * exact title matches and ordered by desc year
 * 
 * @author CarreDX
 *
 */
class SimpleQuery implements IQueryStrategy {

    @Override
    public List<Book> find(String query, Set<Book> books) {
        //find
        Set<Book> foundBooks = new HashSet<Book>();
        for (Book book : books) {
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
        List<Book> booksList = new ArrayList<Book>(foundBooks);
        Comparator<Book> byYear = (Book o1, Book o2)->Integer.compare(o2.getYear(), o1.getYear()); //descending by year
        Collections.sort(booksList, byYear);
        return booksList;
    }
    
}

/**
 * Basic query qualities with index and sub-query matching. It will find every
 * book title and author which contain any of the space-separated query key
 * words
 * 
 * @author CarreDX
 *
 */
class IndexedQuery implements IQueryStrategy {
    
    private final Comparator<Book> order = new Comparator<Book>() {

        @Override
        public int compare(Book o1, Book o2) {
            int cmp = 0;
            if (o1.sameAs(o2)) {
                return Integer.compare(o2.getYear(), o1.getYear());
            } else {
                cmp += Double.compare(matchScore(query, o2), matchScore(query, o1));
            }
            
            if (cmp == 0) {
                cmp = o1.getTitle().compareTo(o2.getTitle());
            }
            return cmp;
        }
        
    };
    
    private final Map<String, Set<Book>> index = new HashMap<String, Set<Book>>();

    private String query;
    
    @Override
    public List<Book> find(String query, Set<Book> books) {
        // generate score Map for cache
        this.query = query;
        
        Set<Book> result = index.containsKey(query) ? index.get(query) : new HashSet<Book>();
        
        for (String keyword : query.split("\\s")) {
            if (index.containsKey(keyword)) {
                result.addAll(index.get(keyword)); 
            }
        }
        
        result.retainAll(books);
        List<Book> resultList = new ArrayList<Book>(result);
        Collections.sort(resultList, order);
        return resultList;
    }
    
    /**
     * Calculate a match score to help sort the query results according to spec
     * for {@link #find(String, Set)}
     * 
     * @param query
     * @param book
     * @return
     */
    private Double matchScore(String query, Book book) {
        if (query.equals(book.getTitle())) {
            return 4.0;
        }
        for (String author : book.getAuthors()) {
            if (query.equals(author)) {
                return 3.0;
            }
        }
        if (query.equals(Integer.toString(book.getYear()))) {
            return 2.0;
        }
        
        Set<String> bookWords = getKeywords(book);
        int bookChars = bookWords.stream().mapToInt(s -> s.length()).sum();
        Set<String> queryWords = new HashSet<String>(Arrays.asList(query.split("\\s")));
        bookWords.removeAll(queryWords);
        int unmatchedChars = bookWords.stream().mapToInt(s -> s.length()).sum();
        return 1.0 - ((double) unmatchedChars / bookChars);
    }
    
    
    /**
     * Add a book to the search index for ease of later querying
     */
    @Override
    public void index(Book book) {
        
        Set<String> words = getKeywords(book);
        
        for (String word : words) {
            Set<Book> booksValue = index.containsKey(word) ? index.get(word) : new HashSet<Book>();
            booksValue.add(book);
            index.put(word, booksValue);
        }
    }
    
    public Map<String, Set<Book>> getIndex() {
        return index;
    }
    
    /**
     * Get the set of keywords with which a book should be associated. This
     * includes entire title, space-separated words in title, entire author,
     * space-separated author name (i.e. first, last, prefix, post-fix), year
     * 
     * @param book
     * @return
     */
    public Set<String> getKeywords(Book book) {
        Set<String> words = new HashSet<String>();
        // Index full title
        words.addAll(Arrays.asList(book.getTitle()));
        // index each word in the title
        words.addAll(Arrays.asList(book.getTitle().split("\\s")));
        // index authors full names
        words.addAll(book.getAuthors());
        // index each word in authors names;
        for (String s : book.getAuthors()) {
            words.addAll(Arrays.asList(s.split("\\s")));
        }
        // index year
        words.add(Integer.toString(book.getYear()));
        return words;
    }
}
