package twitter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

public class SocialNetworkTest {

    /*
     * Testing Strategy:
     * 
     * guessFollowsGraph partition list of input tweets on:
     * size of list of tweets: 0, 1, >1
     * number of authors in list: 1, >1
     * edge-cases: 
     *  self-mentions
     *  case-variable author names
     * 
     * Postconditions to verify:
     * expected size of network (upper limit)
     * expected users in network (legal elements)
     * 
     * 
     * influencers partition on:
     * size of SocialNetwork: 0, 1, >1
     * initial order of users reversed by count
     * 
     */
    
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    public void testGuessFollowsGraphEmpty() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>());
        
        assertTrue("expected empty graph", followsGraph.isEmpty());
    }
    @Test
    public void testGuessFollowsSelfMentions() {
        // covers the case of self-mentions not adding to the map
        Tweet t = new Tweet(1, "user", "@user", d1);
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<Tweet>(Arrays.asList(t)));
        
        for (String user : followsGraph.keySet()) {
            assertTrue("unexpected user", user.equalsIgnoreCase("user"));
            assertTrue("expected empty set of follows", followsGraph.get(user).isEmpty());
        }
    }
    @Test
    public void testGuessFollowsOneMention() {
        // covers the case of adding single mention to follows
        Tweet t = new Tweet(1, "user", "@friend", d1);
        Set<String> expectedKeys = new HashSet<String>(Arrays.asList("user"));
        Set<String> expectedFollows = new HashSet<String>(Arrays.asList("friend"));
        
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<Tweet>(Arrays.asList(t)));
        
        // get set for user
        // necessary for case-insensitive matching
        Set<String> follows = null;
        for (String user : followsGraph.keySet()) {
            if (expectedKeys.contains(user.toLowerCase())) {
                follows = followsGraph.get(user);
                break;
            }
        }
        assertTrue("expected a non-null set of followers for user", follows != null);
        assertTrue("unexpected follow set size", follows.size() == expectedFollows.size());
        
        // check follows for expected users
        for (String follow : follows) {
            assertTrue("unexpected follower in set", expectedFollows.contains(follow.toLowerCase()));
        }
    }
    @Test
    public void testGuessFollowsCapturesMentions() {
        // covers the case of multiple mentions
        Tweet t = new Tweet(1, "user", "@FOLLOW1 @follow2", d1);
        Set<String> expectedKeys = new HashSet<String>(Arrays.asList("user"));
        Set<String> expectedFollows = new HashSet<String>(Arrays.asList("follow1", "follow2"));
        
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<Tweet>(Arrays.asList(t)));
        
        // get set for user
        // necessary for case-insensitive matching
        Set<String> follows = null;
        for (String user : followsGraph.keySet()) {
            if (expectedKeys.contains(user.toLowerCase())) {
                follows = followsGraph.get(user);
                break;
            }
        }
        assertTrue("expected a non-null set of followers for user", follows != null);
        assertTrue("unexpected follow set size", follows.size() == expectedFollows.size());
        
        // check follows for expected users
        for (String follow : follows) {
            assertTrue("unexpected follower in set", expectedFollows.contains(follow.toLowerCase()));
        }
    }
    @Test
    public void testGuessFollowsMultipleAuthors() {
        // covers the case of authors being added to eachothers networks
        Tweet t1 = new Tweet(1, "stan", "now, I'm drunk again", d1);
        Tweet t2 = new Tweet(2, "kyle", "a means to my end", d1);
        
        Map<String, Set<String>> allowedNetwork = new HashMap<String, Set<String>>();
        allowedNetwork.put("stan", new HashSet<String>(Arrays.asList("kyle")));
        allowedNetwork.put("kyle", new HashSet<String>(Arrays.asList("stan")));

        Map<String, Set<String>> network = SocialNetwork.guessFollowsGraph(new ArrayList<Tweet>(Arrays.asList(t1, t2)));
        
        // check network's follows are in allowed follows
        Set<String> follows = null;
        Set<String> allowedFollows = null;
        
        for (String user : network.keySet()) {
            assertTrue("unexpected user network", allowedNetwork.containsKey(user.toLowerCase()));
            follows = network.get(user);
            allowedFollows = allowedNetwork.get(user.toLowerCase());
            assertTrue("expected a non-null set of followers for user", follows != null);
            for (String follow : follows) {
                assertTrue("Follow is not in allowed follow set", allowedFollows.contains(follow.toLowerCase()));
            }
            assertTrue("unexpected follow set size", follows.size() <= allowedFollows.size());
            break;
        }

    }
    @Test
    public void testGuessFollowsSameAuthorDifferentCase() {
        // covers the case of authors being added to their own network because of case insensitivity
        Tweet t1 = new Tweet(1, "StaN", "now, I'm drunk again", d1);
        Tweet t2 = new Tweet(2, "sTAn", "a means to my end @stAn", d1);
        
        Map<String, Set<String>> allowedNetwork = new HashMap<String, Set<String>>();
        allowedNetwork.put("stan", new HashSet<String>());
        
        Map<String, Set<String>> network = SocialNetwork.guessFollowsGraph(new ArrayList<Tweet>(Arrays.asList(t1, t2)));
        
        // check network's follows are in allowed follows
        Set<String> follows = null;
        
        for (String user : network.keySet()) {
            assertTrue("unexpected user network", allowedNetwork.containsKey(user.toLowerCase()));
            follows = network.get(user);
            assertTrue("expected a non-null set of followers for user", follows != null);
            assertTrue("unexpected follow set size", follows.size() == 0);
            break;
        }
    }
    @Test
    public void testGuessFollowsMentionCaseSensitive() {
        // covers the case of mentions being added in a case-insensitive way
        Tweet t1 = new Tweet(1, "ABE", "now, I'm drunk again @CHUCK", d1);
        Tweet t2 = new Tweet(2, "chuck", "a means to my end @abe", d1);
        
        Map<String, Set<String>> allowedNetwork = new HashMap<String, Set<String>>();
        allowedNetwork.put("abe", new HashSet<String>(Arrays.asList("chuck")));
        allowedNetwork.put("chuck", new HashSet<String>(Arrays.asList("abe")));
        
        Map<String, Set<String>> network = SocialNetwork.guessFollowsGraph(new ArrayList<Tweet>(Arrays.asList(t1, t2)));
        
        for (String user : network.keySet()) {
            assertTrue("unexpected user network", allowedNetwork.containsKey(user.toLowerCase()));
            Set<String> follows = network.get(user);
            Set<String> allowedFollows = allowedNetwork.get(user.toLowerCase());
            assertTrue("expected a non-null set of followers for user", follows != null);
            assertTrue("required users missing from follow set", follows.containsAll(allowedFollows));
            assertTrue("follow set size must be 1", follows.size() == allowedFollows.size());
        }
    }
    
    @Test
    public void testInfluencersEmpty() {
        Map<String, Set<String>> followsGraph = new HashMap<String, Set<String>>();
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue("expected List same size as input", influencers.size() == 0);
        assertTrue("expected empty list", influencers.isEmpty());
    }
    @Test
    public void testInfluencersOneUserTwoUnrelatedFollows() {
        // covers the case of a map with a single username following two users
        Map<String, Set<String>> followsGraph = new HashMap<String, Set<String>>();
        Set<String> followers = new HashSet<String>(Arrays.asList("test1", "test2"));
        followsGraph.put("username1", followers);
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        List<String> expectedInfluencers = new ArrayList<String>(Arrays.asList("test1", "test2", "username1"));
        
        assertTrue("expected List same size as input", influencers.size() == 3);
        for (String user : influencers) {
            assertTrue("unexpected user in list", expectedInfluencers.contains(user.toLowerCase()));
        }
    }
    @Test
    public void testInfluencersTwoUsersOneFollows() {
        // covers the case of two users with different influence
        Map<String, Set<String>> followsGraph = new HashMap<String, Set<String>>();
        followsGraph.put("username1", new HashSet<String>(Arrays.asList("username2")));
        followsGraph.put("username2", new HashSet<String>());
        
        List<String> expectedInfluencers = new ArrayList<String>(Arrays.asList("username2", "username1"));
        
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue("expected List same size as input", influencers.size() == 2);
        assertTrue("expected order of users sorted by descending influence", influencers.get(0).equalsIgnoreCase("username2"));
        for (String user : influencers) {
            assertTrue("unexpected user in list", expectedInfluencers.contains(user.toLowerCase()));
        }
    }
    @Test
    public void testInfluencersNonKeyUserWithInfluence() {
        // covers the case of two users in map and third user only existing in follows with most influence
        Map<String, Set<String>> followsGraph = new HashMap<String, Set<String>>();
        followsGraph.put("username1", new HashSet<String>(Arrays.asList("username2", "username3")));
        followsGraph.put("username2", new HashSet<String>(Arrays.asList("username1", "username3")));
        
        List<String> expectedInfluencers = new ArrayList<String>(Arrays.asList("username3", "username2", "username1"));
        
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue("expected List same size as input", influencers.size() == 3);
        assertTrue("expected order of users sorted by descending influence", influencers.get(0).equalsIgnoreCase("username3"));
        for (String user : influencers) {
            assertTrue("unexpected user in list", expectedInfluencers.contains(user.toLowerCase()));
        }
    }
    @Test
    public void testInfluencersNonKeyUserWithNoInfluence() {
        // covers the case of two users in map and third user only existing in follows with least influence
        Map<String, Set<String>> followsGraph = new HashMap<String, Set<String>>();
        followsGraph.put("username1", new HashSet<String>(Arrays.asList("username2", "username3")));
        followsGraph.put("username2", new HashSet<String>(Arrays.asList("username1")));
        followsGraph.put("username4", new HashSet<String>(Arrays.asList("username1")));
        
        List<String> expectedInfluencers = new ArrayList<String>(Arrays.asList("username4", "username3", "username2", "username1"));
        
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue("expected List same size as input", influencers.size() == 4);
        assertTrue("expected order of users sorted by descending influence", influencers.get(0).equalsIgnoreCase("username1"));
        assertTrue("expected order of users sorted by descending influence", influencers.get(3).equalsIgnoreCase("username4"));
        for (String user : influencers) {
            assertTrue("unexpected user in list", expectedInfluencers.contains(user.toLowerCase()));
        }
    }
    @Test
    public void testInfluencersCaseSensitivity() {
        // covers the case of two users with different influence
        Map<String, Set<String>> followsGraph = new HashMap<String, Set<String>>();
        followsGraph.put("username1", new HashSet<String>(Arrays.asList("userNAME2")));
        followsGraph.put("username2", new HashSet<String>(Arrays.asList("USERname1")));
        
        List<String> expectedInfluencers = new ArrayList<String>(Arrays.asList("username2", "username1"));
        
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue("expected List same size as input", influencers.size() == 2);
        for (String user : influencers) {
            assertTrue("unexpected user in list", expectedInfluencers.contains(user.toLowerCase()));
        }
    }

    /*
     * Warning: all the tests you write here must be runnable against any
     * SocialNetwork class that follows the spec. It will be run against several
     * staff implementations of SocialNetwork, which will be done by overwriting
     * (temporarily) your version of SocialNetwork with the staff's version.
     * DO NOT strengthen the spec of SocialNetwork or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in SocialNetwork, because that means you're testing a
     * stronger spec than SocialNetwork says. If you need such helper methods,
     * define them in a different class. If you only need them in this test
     * class, then keep them in this test class.
     */


    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
