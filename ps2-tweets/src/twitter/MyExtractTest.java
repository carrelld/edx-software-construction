package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class MyExtractTest {

    /*
     * Testing Strategy:
     * 
     * Partition on:
     * valid and non-valid hashtag format
     * number of hashtags: 0, 1, >1
     * Number of Users: 1, >1
     * Number of tweets: 1, >1
     * 
     * check case-insensitivity is maintained
     * 
     * Cover each part
     */
    
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    
    @Test
    public void testGetHashtagUsersNoMention() {
        // covers case where tweet text contains no hashtags
        Map<String, Set<String>> hashtags = Extract.getHashtagUsers(Arrays.asList(tweet1));
        
        assertTrue("expected empty map", hashtags.isEmpty());
    }
    @Test
    public void testGetHashtagUsersSingleTag() {
        // covers case of one hashtag and illegal hashtag
        Tweet t1 = new Tweet(1, "bbitdiddle", "#bro", d2);
        Tweet t2 = new Tweet(2, "bbitdiddle", "###", d2);
        
        Map<String, Set<String>> hashtags = Extract.getHashtagUsers(Arrays.asList(t1, t2));
        
        assertTrue("expected hashtag in map", hashtags.containsKey("bro"));
        assertTrue("expected one hashtag in map", hashtags.size() == 1);
        assertTrue("expected user set in map not null", hashtags.get("bro") != null);
        assertTrue("expected only 1 user in hashtag set value", hashtags.get("bro").size() == 1);
    } 
    @Test
    public void testGetHashtagUsersDuplicateTags() {
        // covers duplicate mentions (within and among tweets), and distinct hashtags >1
        Tweet t1 = new Tweet(1, "user1", "#todo #todo", d2);
        Tweet t2 = new Tweet(2, "user2", "rivest #hype #todo", d2);
        
        Map<String, Set<String>> hashtags = Extract.getHashtagUsers(Arrays.asList(t1, t2));
        
        assertTrue("expected 2 hashtags in map", hashtags.size() == 2);
        
        assertTrue("expected hashtag in map", hashtags.containsKey("todo"));
        assertTrue("expected user set in map not null", hashtags.get("todo") != null);
        assertTrue("expected 2 users in hashtag set value", hashtags.get("todo").size() == 2);
        
        assertTrue("expected hashtag in map", hashtags.containsKey("hype"));
        assertTrue("expected user set in map not null", hashtags.get("hype") != null);
        assertTrue("expected only 1 user in hashtag set value", hashtags.get("hype").size() == 1);
    }
    @Test
    public void testGetHashtagUsersValidTags() {
        // covers case of valid hashtags getting captured and an author NOT getting accidently put into another hashtag set
        Tweet t1 = new Tweet(1, "author", "#hype,xxx#chortle.xxx#fortune#by-pass #f1_ux!#baseplate)#endofline", d2);
        Tweet t2 = new Tweet(2, "emptyauthor", "hype,xxxchortle.xxxfortuneby-passf1_ux!baseplate)endofline", d2);
        
        // hype, chortle, fortune, bypass, f1_ux, baseplate
        Map<String, Set<String>> hashtags = Extract.getHashtagUsers(Arrays.asList(t1, t2));
        
        assertTrue("expected 6 hashtags in map", hashtags.size() == 7);
        
        assertTrue("expected hashtag in map with only 1 user", hashtags.containsKey("hype") && hashtags.get("hype").size() == 1);
        assertTrue("expected hashtag in map with only 1 user", hashtags.containsKey("chortle") && hashtags.get("chortle").size() == 1);
        assertTrue("expected hashtag in map with only 1 user", hashtags.containsKey("fortune") && hashtags.get("fortune").size() == 1);
        assertTrue("expected hashtag in map with only 1 user", hashtags.containsKey("by-pass") && hashtags.get("by-pass").size() == 1);
        assertTrue("expected hashtag in map with only 1 user", hashtags.containsKey("f1_ux") && hashtags.get("f1_ux").size() == 1);
        assertTrue("expected hashtag in map with only 1 user", hashtags.containsKey("baseplate") && hashtags.get("baseplate").size() == 1);
        assertTrue("expected hashtag in map with only 1 user", hashtags.containsKey("endofline") && hashtags.get("endofline").size() == 1);
        
        assertTrue("expected hashtag's only user is author", hashtags.get("hype").contains("author"));
        assertTrue("expected hashtag's only user is author", hashtags.get("chortle").contains("author"));
        assertTrue("expected hashtag's only user is author", hashtags.get("fortune").contains("author"));
        assertTrue("expected hashtag's only user is author", hashtags.get("by-pass").contains("author"));
        assertTrue("expected hashtag's only user is author", hashtags.get("f1_ux").contains("author"));
        assertTrue("expected hashtag's only user is author", hashtags.get("baseplate").contains("author"));
        assertTrue("expected hashtag's only user is author", hashtags.get("endofline").contains("author"));
        
    }  
    @Test
    public void testGetHashtagUsersCaseInsensitive() {
        // covers where implementation may take any variation on case to be an element in the set
        Tweet t1 = new Tweet(1, "upper", "#POUND-IT", d2);
        Tweet t2 = new Tweet(2, "lower", "#pound-IT", d2);
        
        Map<String, Set<String>> hashtags = Extract.getHashtagUsers(Arrays.asList(t1, t2));
        Set<String> keys = hashtags.keySet();
        
        assertTrue("expected one hashtag in map", keys.size() == 1);
        Iterator<String> iterator = keys.iterator();
        assertTrue("expecting a non-null entry", iterator.hasNext());
        assertTrue("expected hashtag in map to be case-insensitive", iterator.next().equalsIgnoreCase("POUND-it"));
    }

}
