package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class FilterTest {

    /*
     * Testing Strategy
     * 
     * For all methods partition on:
     * tweets.size():   1, >1
     * 
     * For writtenBy, partition on:
     * number of distinct authors:  1, >1
     * number of tweets per author: 1, >1
     * order of tweet authors:  consecutive, non-consecutive
     * Test for case insensitivity of author names
     * 
     * For inTimespan, partition on:
     * filter timespan: zero, non-zero span;
     * tweet times: 0 results, 1+ results
     * edge cases: zero-span timespan matching a tweet time
     * 
     * For containing, partition on:
     * number of words found per tweet: 0, 1, >1
     * word found in the middle, beginning, and end
     * number of search terms: 1, >1
     * edge case: punctuation and other non-alpha and non-space characters surrounding the strings
     * 
     * tests verify post-conditions of List contents and order
     */
    
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-02-18T10:00:00Z");
    private static final Instant d4 = Instant.parse("2016-02-18T11:00:00Z");
    
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(3, "bbitdiddle", "to tweet or not to tweet, that is the question for the tweet", d3);
    private static final Tweet tweet4 = new Tweet(4, "alySSa", "this is the fourth tweet, second by me!!", d4);    
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    @Test
    public void testWrittenByEmptyList() {
        // covers the case of empty list of tweets
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(), "user");
        assertEquals("List should be empty", true, writtenBy.isEmpty());
    }
    @Test
    public void testWrittenByIndexOutOfBoundsBug() {
        // covers the case of trying to access element of empty list
        //http://stackoverflow.com/questions/156503/how-do-you-assert-that-a-certain-exception-is-thrown-in-junit-4-tests
        boolean thrown = false;
        try {
            Filter.writtenBy(Arrays.asList(), "user");
        } catch (IndexOutOfBoundsException e) {
            thrown = true;
        }
        
        assertFalse("Implementation is trying to access elements in an empty list", thrown);
    }
    @Test
    public void testWrittenBySingleTweetsSingleResult() {
        // covers single tweet
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1), "alyssa");
        
        assertEquals("expected singleton list", 1, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet1));
    }
    @Test
    public void testWrittenByMultipleTweetsSingleResult() {
        // covers multiple distinct author single tweets
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "alyssa");
        
        assertEquals("expected singleton list", 1, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet1));
    }
    @Test
    public void testWrittenByMultipleTweetsConsecutiveResult() {
        // covers multiple distinct authors, consecutive
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2, tweet3), "bbitdiddle");
        
        assertEquals("expected list size 2", 2, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.containsAll(Arrays.asList(tweet2, tweet3)));
        assertTrue("expected list to remain in original order", writtenBy.indexOf(tweet2) < writtenBy.indexOf(tweet3));
    }
    @Test
    public void testWrittenByMultipleTweetsNonConsecutiveAndDifferentCaseResult() {
        // covers multiple distinct authors, non-consecutive, case insensitivity of results
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2, tweet4), "alyssa");
        
        assertEquals("expected list size 2", 2, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.containsAll(Arrays.asList(tweet1, tweet4)));
        assertTrue("expected list to remain in original order", writtenBy.indexOf(tweet1) < writtenBy.indexOf(tweet4));
    }
    
    @Test
    public void testInTimespanMultipleTweetsMultipleResults() {
        // covers multiple tweets with multiple results in the non-zero span
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T12:00:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));
        
        assertFalse("expected non-empty list", inTimespan.isEmpty());
        assertTrue("expected list to contain tweets", inTimespan.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, inTimespan.indexOf(tweet1));
    }
    @Test
    public void testInTimespanSingleTweetSingleResultWithEdgeCaseMatchingTimespan() {
        // covers single tweet with single result whose span is zero, but matches the tweet
        Instant testStart = Instant.parse("2016-02-17T10:00:00Z");
        Instant testEnd = testStart;
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1), new Timespan(testStart, testEnd));
        
        assertFalse("expected non-empty list", inTimespan.isEmpty());
        assertTrue("expected list to contain tweets", inTimespan.containsAll(Arrays.asList(tweet1)));
        assertEquals("expected same order", 0, inTimespan.indexOf(tweet1));
    }
    @Test
    public void testInTimespanNoResult() {
        // covers multiple tweets with no results
        Instant testStart = Instant.parse("2016-02-16T10:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T09:00:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2, tweet3, tweet4), new Timespan(testStart, testEnd));
        
        assertTrue("expected empty list", inTimespan.isEmpty());
    }
    @Test
    public void testContainingStringContainsSubstringBug() {
        // covers the improper use of "contains" bug
        Tweet t1 = new Tweet(1, "user1", "abcdefghijklmnop", d1);
        Tweet t2 = new Tweet(2, "user2", "efghijklmnopqrst", d2);
        List<Tweet> containing = Filter.containing(Arrays.asList(t1, t2), Arrays.asList("efghi"));
        
        assertTrue("abcde".contains("bcd")); //assumption of how String#contains works
        assertTrue("expected empty list", containing.isEmpty());
    }
    @Test
    public void testContainingMultipleTweets() {
        // covers multiple tweets return multiple results, with word found in middle, single result
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("talk"));
        
        assertFalse("expected non-empty list", containing.isEmpty());
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, containing.indexOf(tweet1));
    }
    @Test
    public void testContainingMultipleTweetsWordsAtBeginning() {
        // covers multiple tweets return multiple results, word found at beginning
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2, tweet3), Arrays.asList("is"));
        
        assertTrue("expected list size 2", containing.size() == 2);
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet1, tweet3)));
        assertEquals("expected same order", 0, containing.indexOf(tweet1));
    }
    @Test
    public void testContainingMultipleTweetsCaseInsensitivity() {
        // covers Case insensitivity of search term
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2, tweet3), Arrays.asList("IS"));
        
        assertTrue("expected list size 2", containing.size() == 2);
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet1, tweet3)));
        assertEquals("expected same order", 0, containing.indexOf(tweet1));
    }
    @Test
    public void testContainingMultipleTweetsMultipleSearchTerms() {
        // covers multiple search terms
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2, tweet3), Arrays.asList("IS", "talk"));
        
        assertTrue("expected list size 3", containing.size() == 3);
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet1, tweet2, tweet3)));
        assertEquals("expected same order", 0, containing.indexOf(tweet1));
        assertTrue("expected same order of remaining elements", containing.indexOf(tweet2) == 1 && containing.indexOf(tweet3) == 2);
    }
    @Test
    public void testContainingPunctuation() {
        // covers the case of ONLY matching on white-space separated substrings, with word found at end
        List<Tweet> containing;
        // without punctuation
        containing = Filter.containing(Arrays.asList(tweet3, tweet4), Arrays.asList("tweet"));
        assertTrue("expected single tweet", containing.contains(tweet3));
        
        // with punctuation
        containing = Filter.containing(Arrays.asList(tweet3, tweet4), Arrays.asList("tweet,"));
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet3, tweet4)));
        assertEquals("expected same order", 0, containing.indexOf(tweet3));
    }

    /*
     * Warning: all the tests you write here must be runnable against any Filter
     * class that follows the spec. It will be run against several staff
     * implementations of Filter, which will be done by overwriting
     * (temporarily) your version of Filter with the staff's version.
     * DO NOT strengthen the spec of Filter or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Filter, because that means you're testing a stronger
     * spec than Filter says. If you need such helper methods, define them in a
     * different class. If you only need them in this test class, then keep them
     * in this test class.
     */


    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
