package com.ygznsl.noskurt.oyla.helper;

import android.util.Log;

import com.ygznsl.noskurt.oyla.entity.Category;
import com.ygznsl.noskurt.oyla.entity.Entity;
import com.ygznsl.noskurt.oyla.entity.Poll;

import java.io.Serializable;
import java.text.Collator;
import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public final class FilterAndSortOptions implements Serializable {

    private Category pollCategory = new Category();
    private PollGender pollGender = PollGender.GENDER_BOTH;
    private PollMultiple pollMultiple = PollMultiple.BOTH;

    private SortOrder sortOrder = SortOrder.ASCENDING;
    private SortField sortField = SortField.BY_TITLE;

    public FilterAndSortOptions(){
        pollCategory.setName("Hepsi");
        pollCategory.setId(-1);
    }

    public Category getPollCategory() {
        return pollCategory;
    }

    public void setPollCategory(Category pollCategory) {
        this.pollCategory = pollCategory;
    }

    public PollGender getPollGender() {
        return pollGender;
    }

    public void setPollGender(PollGender pollGender) {
        this.pollGender = pollGender;
    }

    public PollMultiple getPollMultiple() {
        return pollMultiple;
    }

    public void setPollMultiple(PollMultiple pollMultiple) {
        this.pollMultiple = pollMultiple;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    public SortField getSortField() {
        return sortField;
    }

    public void setSortField(SortField sortField) {
        this.sortField = sortField;
    }

    private List<Poll> filter(List<Poll> polls, OylaDatabase oyla){
        final List<Poll> list = new LinkedList<>(polls);

        int index = 0;
        while (index < list.size()){
            final Poll poll = list.get(index);

            if ((pollGender == PollGender.GENDER_MALE && (poll.getGenders().equals("B") || poll.getGenders().equals("K")))
                    || (pollGender == PollGender.GENDER_FEMALE && (poll.getGenders().equals("B") || poll.getGenders().equals("E")))){
                list.remove(index);
                continue;
            }

            if (pollCategory.getId() != -1){
                if (poll.getCategory() != pollCategory.getId()){
                    list.remove(index);
                    continue;
                }
            }

            if (pollMultiple != PollMultiple.BOTH){
                if ((pollMultiple == PollMultiple.YES && poll.getMult() == 0)
                        || (pollMultiple == PollMultiple.NO && poll.getMult() == 1)){
                    list.remove(index);
                    continue;
                }
            }

            index++;
        }

        return list;
    }

    private List<Poll> sort(List<Poll> list, final OylaDatabase oyla, final List<Category> categories){
        if (!list.isEmpty()){
            Collections.sort(list, new Comparator<Poll>() {
                @Override
                public int compare(Poll p1, Poll p2) {
                    final Collator collator = Collator.getInstance(new Locale("tr", "TR"));
                    int result = collator.compare(p1.getTitle(), p2.getTitle());
                    if (sortField == SortField.BY_TITLE){
                        result = collator.compare(p1.getTitle(), p2.getTitle());
                    } else if (sortField == SortField.BY_CATEGORY_NAME) {
                        final Category c1 = Entity.findById(categories, p1.getCategory()).get();
                        final Category c2 = Entity.findById(categories, p2.getCategory()).get();
                        result = collator.compare(c1.getName(), c2.getName());
                    } else if (sortField == SortField.BY_PUBLISH_DATE) {
                        try {
                            final Date d1 = Poll.DATE_FORMAT.parse(p1.getPdate());
                            final Date d2 = Poll.DATE_FORMAT.parse(p2.getPdate());
                            result = d1.compareTo(d2);
                        } catch (ParseException ex) {
                            Log.e("sort.task.uithread", ex.getMessage());
                        }
                    } else if (sortField == SortField.BY_OPTION_COUNT){
                        final int count1 = oyla.optionsOfPoll(p1).size();
                        final int count2 = oyla.optionsOfPoll(p2).size();
                        result = Integer.valueOf(count1).compareTo(count2);
                    } else if (sortField == SortField.BY_VOTE_COUNT){
                        final int count1 = oyla.votesOfPoll(p1).size();
                        final int count2 = oyla.votesOfPoll(p2).size();
                        result = Integer.valueOf(count1).compareTo(count2);
                    }
                    return sortOrder == SortOrder.ASCENDING ? result : 0 - result;
                }
            });
        }
        return list;
    }

    public List<Poll> filterAndSort(List<Poll> list, OylaDatabase oyla, List<Category> categories){
        return sort(filter(list, oyla), oyla, categories);
    }

    @Override
    public String toString() {
        return "FilterAndSortOptions{" +
                "pollCategory=" + pollCategory +
                ", pollGender=" + pollGender +
                ", pollMultiple=" + pollMultiple +
                ", sortOrder=" + sortOrder +
                ", sortField=" + sortField +
                '}';
    }

    public enum PollGender {
        GENDER_MALE,
        GENDER_FEMALE,
        GENDER_BOTH
    }

    public enum PollMultiple {
        YES,
        NO,
        BOTH
    }

    public enum SortOrder {
        ASCENDING,
        DESCENDING
    }

    public enum SortField {
        BY_TITLE,
        BY_CATEGORY_NAME,
        BY_PUBLISH_DATE,
        BY_OPTION_COUNT,
        BY_VOTE_COUNT
    }

}
