package com.ygznsl.noskurt.oyla.helper;

import com.ygznsl.noskurt.oyla.entity.Entity;
import com.ygznsl.noskurt.oyla.entity.Option;
import com.ygznsl.noskurt.oyla.entity.Poll;
import com.ygznsl.noskurt.oyla.entity.User;
import com.ygznsl.noskurt.oyla.entity.Vote;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public final class OylaDatabase implements Serializable {

    private final List<Vote> votes = Collections.synchronizedList(new LinkedList<Vote>());
    private final Map<Integer, User> users = Collections.synchronizedMap(new TreeMap<Integer, User>());
    private final Map<Integer, Poll> polls = Collections.synchronizedMap(new TreeMap<Integer, Poll>());
    private final Map<Integer, Option> options = Collections.synchronizedMap(new TreeMap<Integer, Option>());

    public synchronized void addUser(User user){
        users.put(user.getId(), user);
    }

    public synchronized void addPoll(Poll poll){
        polls.put(poll.getId(), poll);
    }

    public synchronized void addOption(Option option){
        options.put(option.getId(), option);
    }

    public synchronized void addVote(Vote vote){
        if (!votes.contains(vote)){
            votes.add(vote);
        }
    }

    public synchronized void removeUser(User user){
        users.remove(user.getId());
    }

    public synchronized void removePoll(Poll poll){
        polls.remove(poll.getId());
    }

    public synchronized void removeOption(Option option){
        options.remove(option.getId());
    }

    public synchronized void removeVote(Vote vote){
        votes.remove(vote);
    }

    public synchronized List<User> getUsers() {
        return new LinkedList<>(users.values());
    }

    public synchronized List<Poll> getPolls() {
        return new LinkedList<>(polls.values());
    }

    public synchronized List<Option> getOptions() {
        return new LinkedList<>(options.values());
    }

    public synchronized List<Vote> getVotes() {
        return votes;
    }

    public User getUserById(int id){
        return users.get(id);
    }

    public Poll getPollById(int id){
        return polls.get(id);
    }

    public Option getOptionById(int id){
        return options.get(id);
    }

    public boolean hasUserCreatedPoll(User user, Poll poll){
        return poll.getUser() == user.getId();
    }

    public boolean hasUserVotedPoll(User user, Poll poll){
        return pollsUserVoted(user).contains(poll);
    }

    public List<Poll> pollsUserCreated(final User user){
        return Entity.findAllMatches(getPolls(), new Predicate<Poll>() {
            @Override
            public boolean test(Poll in) {
                return hasUserCreatedPoll(user, in);
            }
        });
    }

    public List<Option> optionsUserVoted(User user){
        return optionsUserVoted(user, null);
    }

    public List<Option> optionsUserVoted(final User user, final Poll poll){
        final List<Integer> optionIds = Entity.findAllMatches(getVotes(), new Predicate<Vote>() {
            @Override
            public boolean test(Vote in) {
                return in.getU() == user.getId();
            }
        }, new Function<Vote, Integer>() {
            @Override
            public Integer apply(Vote in) {
                return in.getO();
            }
        });

        return Entity.findAllMatches(getOptions(), new Predicate<Option>() {
            @Override
            public boolean test(Option in) {
                return optionIds.contains(in.getId()) && (poll == null || in.getPoll() == poll.getId());
            }
        });
    }

    public List<Poll> pollsUserVoted(final User user){
        final Set<Integer> pollIds = new TreeSet<>(Entity.map(optionsUserVoted(user), new Function<Option, Integer>() {
            @Override
            public Integer apply(Option in) {
                return in.getPoll();
            }
        }));

        return Entity.findAllMatches(getPolls(), new Predicate<Poll>() {
            @Override
            public boolean test(Poll in) {
                return pollIds.contains(in.getId());
            }
        });
    }

    public List<Option> optionsOfPoll(final Poll poll){
        return Entity.findAllMatches(getOptions(), new Predicate<Option>() {
            @Override
            public boolean test(Option in) {
                return in.getPoll() == poll.getId();
            }
        });
    }

    public List<Vote> votesOfPoll(final Poll poll){
        final List<Integer> optionIds = Entity.map(optionsOfPoll(poll), new Function<Option, Integer>() {
            @Override
            public Integer apply(Option in) {
                return in.getId();
            }
        });
        return Entity.findAllMatches(getVotes(), new Predicate<Vote>() {
            @Override
            public boolean test(Vote in) {
                return optionIds.contains(in.getO());
            }
        });
    }

    public Poll randomPollForUser(User user){
        final Random random = new Random();
        Poll randomPoll;
        do {
            randomPoll = polls.get(random.nextInt(polls.size()));
        } while (!randomPoll.getGenders().equals("B") && (user == null || !randomPoll.getGenders().equals(user.getGender())));
        return randomPoll;
    }

    public String randomPollUrl(){
        final List<Character> chars = new LinkedList<>();
        for (char i = 'a'; i <= 'z'; i++) chars.add(i);
        for (char i = 'A'; i <= 'Z'; i++) chars.add(i);
        for (char i = '0'; i <= '9'; i++) chars.add(i);

        final List<String> urls = Entity.map(getPolls(), new Function<Poll, String>() {
            @Override
            public String apply(Poll in) {
                return in.getUrl();
            }
        });

        final Random random = new Random();
        final StringBuilder str = new StringBuilder();
        do {
            str.delete(0, str.length());
            str
                    .append(chars.get(random.nextInt(chars.size())))
                    .append(chars.get(random.nextInt(chars.size())));
        } while (urls.contains(str.toString().trim()));

        return str.toString().trim();
    }

}
