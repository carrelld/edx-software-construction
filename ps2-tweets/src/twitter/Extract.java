package twitter;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extract consists of methods that extract information from a list of tweets.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class Extract {

    /**
     * Get the time period spanned by tweets.
     * 
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * @return a minimum-length time interval that contains the timestamp of
     *         every tweet in the list.
     */
    public static Timespan getTimespan(List<Tweet> tweets) {
        if (tweets == null || tweets.isEmpty()) {
            return new Timespan(Instant.EPOCH, Instant.EPOCH);
        }
        assert tweets.size() >= 1;
        Iterator<Tweet> it = tweets.iterator();
        Instant start = it.next().getTimestamp();
        Instant end = start;
        while (it.hasNext()) {
            Instant time = it.next().getTimestamp();
            if (time.isBefore(start)) {
                start = time;
            } else if (time.isAfter(end)) {
                end = time;
            }
        }
        assert !start.isAfter(end);
        return new Timespan(start, end);
    }

    /**
     * Get usernames mentioned in a list of tweets.
     * 
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * @return the set of usernames who are mentioned in the text of the tweets.
     *         A username-mention is "@" followed by a Twitter username (as
     *         defined by Tweet.getAuthor()'s spec).
     *         The username-mention cannot be immediately preceded or followed by any
     *         character valid in a Twitter username.
     *         For this reason, an email address like bitdiddle@mit.edu does NOT 
     *         contain a mention of the username mit.
     *         Twitter usernames are case-insensitive, and the returned set may
     *         include a username at most once.
     */
    public static Set<String> getMentionedUsers(List<Tweet> tweets) {
        Set<String> mentions = new HashSet<String>();
        // pattern is a negative lookbehind that only captures mentions not preceded by a valid username character
        Pattern p = Pattern.compile("(?<![a-zA-z0-9-_])@([a-zA-z0-9-_]+)");
        
        for (Tweet tweet : tweets) {
            Matcher m = p.matcher(tweet.getText());
            while (m.find()) {
                // Since set isn't case-insensitive by default, force toLowerCase before adding to set
                String username = m.group(1).toLowerCase();
                mentions.add(username);
            }
        }
        return mentions;
    }
    
    /**
     * Get hashtags and a list of user who have used them from a list of tweets
     * 
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * @return hashtags used with the associated author. hashtags, are of the form "#" followed by any legal username characters of non-zero length. Terminated by twitter-valid terminators 
     * Hastags are case-insensitive. The returned map key should be a distinct hashtag and the value should be a set of distinct authors who have used it.
     */
    public static Map<String, Set<String>> getHashtagUsers(List<Tweet> tweets) {
        Map<String, Set<String>> hashtags = new HashMap<String, Set<String>>();
        // regex pattern is any # followed by letters, numbers, dashes or underscores greater than length 0 terminated by twitter-valid terminators
        Pattern p = Pattern.compile("#([a-zA-z0-9-_]+)[\\.\\,\\!\\)$ ]?");
        
        for (Tweet tweet : tweets) {
            Matcher m = p.matcher(tweet.getText());
            // while hashtags are being found
            while (m.find()) {
                // Since Map isn't case-insensitive by default, force toLowerCase before adding to map
                String hashtag = m.group(1).toLowerCase();
                String author = tweet.getAuthor().toLowerCase();
                
                if (!hashtags.containsKey(hashtag)) {
                    hashtags.put(hashtag, new HashSet<String>());
                }
                hashtags.get(hashtag).add(author);
            }
        }
        return hashtags;
    }

    /* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
