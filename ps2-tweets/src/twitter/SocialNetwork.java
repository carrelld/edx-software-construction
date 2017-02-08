package twitter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * SocialNetwork provides methods that operate on a social network.
 * 
 * A social network is represented by a Map<String, Set<String>> where map[A] is
 * the set of people that person A follows on Twitter, and all people are
 * represented by their Twitter usernames. Users can't follow themselves. If A
 * doesn't follow anybody, then map[A] may be the empty set, or A may not even exist
 * as a key in the map; this is true even if A is followed by other people in the network.
 * Twitter usernames are not case sensitive, so "ernie" is the same as "ERNie".
 * A username should appear at most once as a key in the map or in any given
 * map[A] set.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class SocialNetwork {

    /**
     * Guess who might follow whom, from evidence found in tweets.
     * 
     * @param tweets
     *            a list of tweets providing the evidence, not modified by this
     *            method.
     * @return a social network (as defined above) in which Ernie follows Bert
     *         if and only if there is evidence for it in the given list of
     *         tweets.
     *         One kind of evidence that Ernie follows Bert is if Ernie
     *         @-mentions Bert in a tweet. This must be implemented. Other kinds
     *         of evidence may be used at the implementor's discretion.
     *         All the Twitter usernames in the returned social network must be
     *         either authors or @-mentions in the list of tweets.
     */
    public static Map<String, Set<String>> guessFollowsGraph(List<Tweet> tweets) {
        Map<String, Set<String>> network = new HashMap<String, Set<String>>();
        Map<String, Set<String>> hashtags = Extract.getHashtagUsers(tweets);
        
        
        for (Tweet tweet : tweets) {
            String username = tweet.getAuthor().toLowerCase();
            // store all predicted follows for this tweet's author
            Set<String> follows = new HashSet<String>();     
            
            // mentions imply follows!
            // convert all mentions to lowercase before putting to network
            Set<String> uncleanMentions = Extract.getMentionedUsers(Filter.writtenBy(tweets, username));
            for (String mention : uncleanMentions) {
                follows.add(mention.toLowerCase());
            }
            
            // add co-hashtaggers to predicted follows
            for (String hashtag : hashtags.keySet()) {
                Set<String> hashtagUsers = hashtags.get(hashtag);
                if (hashtags.get(hashtag).contains(username)) {
                    follows.addAll(hashtagUsers);
                }
            }
            
            // user can't follow self
            follows.remove(username);
            network.put(username, follows);
        }

        return network;
    }

    /**
     * Find the people in a social network who have the greatest influence, in
     * the sense that they have the most followers.
     * 
     * @param followsGraph
     *            a social network (as defined above)
     * @return a list of all distinct Twitter usernames in followsGraph, in
     *         descending order of follower count.
     */
    public static List<String> influencers(Map<String, Set<String>> followsGraph) {       
        // Map for influence of users
        Map<String, Integer> influenceMap = new HashMap();

        // Add users in network and map Counts follows for each user in a case-insensitive manner
        for (String key : followsGraph.keySet()) {
            // make case-insensitive right away
            String user = key.toLowerCase();
            // add network key user
            if (!influenceMap.containsKey(user)) {
                influenceMap.put(user, 0);
            }
            // add follows and increment influence count
            for (String dirtyUserName : followsGraph.get(key)) {
                String followedUser = dirtyUserName.toLowerCase();
                int nextInfluence = influenceMap.containsKey(followedUser) ? influenceMap.get(followedUser) + 1 : 1;
                influenceMap.put(followedUser, nextInfluence);
            }
        }
        
        // Return sorted List<String>
        return influenceMap.entrySet().stream().sorted(new InfluenceSort()).map(Map.Entry::getKey).collect(Collectors.toList());

    }

    /* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}

class InfluenceSort implements Comparator<Map.Entry<String, Integer>> {
    /**
     * Order elements first descending by influence, then alphabetically by username
     */
    @Override
    public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
        int comp = o2.getValue().compareTo(o1.getValue());
        if (comp == 0) {
            return o1.getKey().compareTo(o2.getKey());
        } else {
            return comp;
        }
    }
}