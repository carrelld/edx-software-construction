package twitter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

public class ExtractTest {

    /*
     * Testing strategy
     * 
     * Partition the inputs as follows:
     * tweets.size():   0, 1, >1
     * tweets.get(i).getTimestamp():    variation, no variation
     * 
     * tweets.get(i).getText(): number of distinct mentions: 0, 1, >1
     * tweets.get(i).getText(): number of duplicate mentions: 1, >1
     * tweets text containing invalid mentions
     * tweets containing confusing but valid and "to-spec" mentions
     * tweets testing [@-_A-Za-z0-9] as parts of mentions and non-mention text
     * 
     * Cover each part of partitions
     */
    
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet copyTweet1 = new Tweet(3, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);

    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    public void testGetTimespanIndexOutOfBoundsBug() {
        // covers the case of trying to access element of empty list
        //http://stackoverflow.com/questions/156503/how-do-you-assert-that-a-certain-exception-is-thrown-in-junit-4-tests
        boolean thrown = false;
        try {
            Extract.getTimespan(Arrays.asList());
        } catch (IndexOutOfBoundsException e) {
            thrown = true;
        }
        
        assertFalse("Implementation is trying to access elements in an empty list", thrown);
    }
    @Test
    public void testGetTimespanNoTweets() {
        // covers the case of 0 tweets
        
        Timespan timespan = Extract.getTimespan(Arrays.asList());
        
        assertEquals("expected 0-length timespan for empty list of tweets", timespan.getStart(), timespan.getEnd());
    }
    @Test
    public void testGetTimespanTwoTweets() {
        // covers the case of >1 tweet with variation
        
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2));
        
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d2, timespan.getEnd());
    }
    @Test
    public void testGetTimespanOneTweet() {
        // covers the case of 1 tweet, no variation
        
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1));
        
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d1, timespan.getEnd());
    }
    @Test
    public void testGetTimespanTwoTweetsSameDate() {
        // covers the case of 2 tweets, no variation
        
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, copyTweet1));
        
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d1, timespan.getEnd());
        assertEquals("tweet and calculated timestamps should agree", tweet1.getTimestamp(), timespan.getStart());
    }
    @Test
    public void testGetTimespanTwoTweetsReverseOrder() {
        // covers the case of 2 tweets, first tweet has timestamp after second tweet
        
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet2, tweet1));
        
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d2, timespan.getEnd());
    }
    
    @Test
    public void testGetMentionedUsersNoMention() {
        // covers case where tweet text contains no mentions
        
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet1));
        
        assertTrue("expected empty set", mentionedUsers.isEmpty());
    }
    @Test
    public void testGetMentionedUsersSingleMention() {
        // covers case of single mention and illegal username mentions
        Tweet t1 = new Tweet(1, "bbitdiddle", "@dj", d2);
        Tweet t2 = new Tweet(2, "bbitdiddle", "rivest@gmail.com@tim", d2);
        
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(t1, t2));
        
        for (String mention : mentionedUsers) {
            assertTrue("Unexpected mention", mention.equalsIgnoreCase("dj"));
        }
        assertTrue("expected only 1 mention", mentionedUsers.size() == 1);
    }
    @Test
    public void testGetMentionedUsersValidMentions() {
        // covers case of single mention and legality of mentions
        Tweet t1 = new Tweet(1, "bbitdiddle", "@dj++++@k9%%%%%%", d2);
        Tweet t2 = new Tweet(2, "bbitdiddle", "rivest@tim@@___________________________________@@@@.@txt.com", d2);
        
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(t1, t2));
        Set<String> expectedMentions = new HashSet<String>(Arrays.asList("dj", "k9", "___________________________________", "txt"));
        
        for (String mention : mentionedUsers) {
            assertTrue("Unexpected mention", expectedMentions.contains(mention.toLowerCase()));
        }
        assertTrue("expected 4 mentions", mentionedUsers.size() == 4);
    }  
    @Test
    public void testGetMentionedUsersDuplicateMentions() {
        // covers duplicate mentions (within and among tweets), and distinct mentions >1
        Tweet t1 = new Tweet(1, "bbitdiddle", "@dj_jazzy_jeff_14 @mi-9ke @mi-9ke", d2);
        Tweet t2 = new Tweet(2, "bbitdiddle", "rivest #hype @mi-9ke", d2);
        
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(t1, t2));
        Set<String> expectedMentions = new HashSet<String>(Arrays.asList("dj_jazzy_jeff_14", "mi-9ke"));
        
        for (String mention : mentionedUsers) {
            assertTrue("Unexpected mention", expectedMentions.contains(mention.toLowerCase()));
        }
        assertTrue("expected only two mentions", mentionedUsers.size() == 2);
    }
    @Test
    public void testGetMentionedUsersCaseInsensitive() {
        // covers where implementation may take any variation on case to be an element in the set
        Tweet t1 = new Tweet(1, "weirdcase", "@mIkE", d2);
        Tweet t2 = new Tweet(2, "othercase", "@mike", d2);
        
        // Case-insensitive set won't allow Strings that match via String::equalsIgnoreCase
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(t1, t2));
        
        // try to add the usernames should result in false boolean if name exists
        //assertFalse("expected shouldn't be able to add case-variation on already existing username", mentionedUsers.add("MiKe"));
        for (String user : mentionedUsers) {
            assertTrue("unexpected user in set", user.equalsIgnoreCase("mike"));
        }
        assertTrue("expected only one mentions", mentionedUsers.size() == 1);
    }

    /*
     * Warning: all the tests you write here must be runnable against any
     * Extract class that follows the spec. It will be run against several staff
     * implementations of Extract, which will be done by overwriting
     * (temporarily) your version of Extract with the staff's version.
     * DO NOT strengthen the spec of Extract or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Extract, because that means you're testing a
     * stronger spec than Extract says. If you need such helper methods, define
     * them in a different class. If you only need them in this test class, then
     * keep them in this test class.
     */


    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}
