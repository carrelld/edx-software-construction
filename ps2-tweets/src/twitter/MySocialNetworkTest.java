package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class MySocialNetworkTest {

    /*
     * Testing strategy:
     * Since this class is largely tested by the specifications in 
     * SocialNetworkTest, this testing will focus on the narrower specs 
     * of the implementation; namely the ability to add predicted 
     * follows based on common hash tags.
     * 
     * Partition on:
     * number of tweets: 1, >1
     * number distinct authors: 1, >1
     * number of common hashtags: 1, >1
     * 
     * Cover each category
     */
    
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    public void testGuessFollowsOneAuthorOneTag() {
        // covers the case of one author, one hashtag
        Tweet t = new Tweet(1, "user", "#tag", d1);
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<Tweet>(Arrays.asList(t)));
        
        assertTrue("expected empty set of follows", followsGraph.get("user").isEmpty());
    }
    
    @Test
    public void testGuessFollowsTwoAuthorOneTag() {
        // covers the case of >1 author, single hashtag
        Tweet t1 = new Tweet(1, "user", "#tag", d1);
        Tweet t2 = new Tweet(2, "otheruser", "#tag", d1);
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<Tweet>(Arrays.asList(t1, t2)));
        
        assertTrue("expected 1 user in follow set", followsGraph.get("user").size() == 1 && followsGraph.get("user").contains("otheruser"));
        assertTrue("expected 1 user in follow set", followsGraph.get("otheruser").size() == 1 && followsGraph.get("otheruser").contains("user"));
    }
    @Test
    public void testGuessFollowsTwoAuthorTwoTag() {
        // covers the case of >1 author, single hashtag
        Tweet t1 = new Tweet(1, "user", "#tag #othertag", d1);
        Tweet t2 = new Tweet(2, "otheruser", "#tag #othertag", d1);
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<Tweet>(Arrays.asList(t1, t2)));
        
        assertTrue("expected 1 user in follow set", followsGraph.get("user").size() == 1 && followsGraph.get("user").contains("otheruser"));
        assertTrue("expected 1 user in follow set", followsGraph.get("otheruser").size() == 1 && followsGraph.get("otheruser").contains("user"));
    }

}
