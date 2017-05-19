package com.ygznsl.noskurt.oyla.helper;

import com.ygznsl.noskurt.oyla.entity.Category;
import com.ygznsl.noskurt.oyla.entity.City;
import com.ygznsl.noskurt.oyla.entity.User;

import java.io.Serializable;

public final class FilterAndSortOptions implements Serializable {

    private Category pollCategory = new Category();
    private PollGender pollGender = PollGender.GENDER_BOTH;
    private PollMultiple pollMultiple = PollMultiple.BOTH;
    private User pollUser = null;
    private City pollUserCity = new City();
    private UserGender pollUserGender = null;
    private UserAgeInterval pollUserAgeInterval = null;

    private SortOrder sortOrder = SortOrder.ASCENDING;
    private SortField sortField = SortField.BY_TITLE;

    public FilterAndSortOptions(){
        pollCategory.setName("Hepsi");
        pollCategory.setId(-1);
        pollUserCity.setName("Hepsi");
        pollUserCity.setId(-1);
    }

    public boolean isPollUserSpecified(){
        return pollUser != null;
    }

    public boolean isPollUserCitySpecified(){
        return pollUserCity.getId() != -1;
    }

    public boolean isPollUserGenderSpecified(){
        return pollUserGender != null;
    }

    public boolean isPollUserAgeIntervalSpecified(){
        return pollUserAgeInterval != null;
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

    public User getPollUser() {
        return pollUser;
    }

    public void setPollUser(User pollUser) {
        this.pollUser = pollUser;
    }

    public City getPollUserCity() {
        return pollUserCity;
    }

    public void setPollUserCity(City pollUserCity) {
        this.pollUserCity = pollUserCity;
    }

    public UserGender getPollUserGender() {
        return pollUserGender;
    }

    public void setPollUserGender(UserGender pollUserGender) {
        this.pollUserGender = pollUserGender;
    }

    public UserAgeInterval getPollUserAgeInterval() {
        return pollUserAgeInterval;
    }

    public void setPollUserAgeInterval(UserAgeInterval pollUserAgeInterval) {
        this.pollUserAgeInterval = pollUserAgeInterval;
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

    public enum UserGender {
        MALE,
        FEMALE
    }

    public enum UserAgeInterval {
        UNDER_18,
        BETWEEN_18_25,
        BETWEEN_26_40,
        BETWEEN_41_65,
        ABOVE_65
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
