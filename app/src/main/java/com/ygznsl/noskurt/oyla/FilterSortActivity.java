package com.ygznsl.noskurt.oyla;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.ygznsl.noskurt.oyla.entity.Category;
import com.ygznsl.noskurt.oyla.entity.City;
import com.ygznsl.noskurt.oyla.entity.User;
import com.ygznsl.noskurt.oyla.helper.FilterAndSortOptions;
import com.ygznsl.noskurt.oyla.helper.OylaDatabase;
import com.ygznsl.noskurt.oyla.helper.RadioButtonCollection;
import com.ygznsl.noskurt.oyla.helper.ValueChangedEvent;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class FilterSortActivity extends AppCompatActivity {

    private boolean guiInitialized = false;

    private RadioGroup radioGroupSortOrderFilterSort;
    private RadioGroup radioGroupPollUserGenderFilterSort;

    private LinearLayout llFilterOptionsFilterSort;
    private LinearLayout llAccordingToPollFilterSort;
    private LinearLayout llAccordingToUserFilterSort;
    private LinearLayout llSortOptionsFilterSort;
    private LinearLayout llButtonsFilterSort;

    private Spinner spinnerPollCategoryFilterSort;
    private Spinner spinnerPollUserFilterSort;
    private Spinner spinnerPollUserCityFilterSort;
    private Spinner spinnerPollUserAgeIntervalFilterSort;

    private CheckBox chkMultipleYesFilterSort;
    private CheckBox chkMultipleNoFilterSort;

    private Button btnCancelFilterSort;
    private Button btnApplyFilterSort;

    private RadioButton radioGenderFemaleFilterSort;
    private RadioButton radioGenderMaleFilterSort;
    private RadioButton radioGenderBothFilterSort;
    private RadioButton radioPollUserFilterSort;
    private RadioButton radioPollUserOtherFilterSort;
    private RadioButton radioPollUserFemaleFilterSort;
    private RadioButton radioPollUserMaleFilterSort;
    private RadioButton radioSortByPollTitleFilterSort;
    private RadioButton radioSortByCategoryNameFilterSort;
    private RadioButton radioSortByPublishDateFilterSort;
    private RadioButton radioSortByOptionCountFilterSort;
    private RadioButton radioSortByVoteCountFilterSort;
    private RadioButton radioAscendingFilterSort;
    private RadioButton radioDescendingFilterSort;

    private void initializeGui(OylaDatabase oyla){
        final List<Category> categories = Category.getCategories(this).get();
        categories.add(0, Category.all());

        final List<City> cities = City.getCities(this).get();
        cities.add(0, City.all());

        final List<String> ageIntervals = new LinkedList<>(Arrays.asList("18'den küçük", "18-25", "26-40", "41-65", "65'ten büyük"));

        radioGroupSortOrderFilterSort = (RadioGroup) findViewById(R.id.radioGroupSortOrderFilterSort);
        radioGroupPollUserGenderFilterSort = (RadioGroup) findViewById(R.id.radioGroupPollUserGenderFilterSort);

        llFilterOptionsFilterSort = (LinearLayout) findViewById(R.id.llFilterOptionsFilterSort);
        llAccordingToPollFilterSort = (LinearLayout) findViewById(R.id.llAccordingToPollFilterSort);
        llAccordingToUserFilterSort = (LinearLayout) findViewById(R.id.llAccordingToUserFilterSort);
        llSortOptionsFilterSort = (LinearLayout) findViewById(R.id.llSortOptionsFilterSort);
        llButtonsFilterSort = (LinearLayout) findViewById(R.id.llButtonsFilterSort);

        spinnerPollCategoryFilterSort = (Spinner) findViewById(R.id.spinnerPollCategoryFilterSort);
        spinnerPollUserFilterSort = (Spinner) findViewById(R.id.spinnerPollUserFilterSort);
        spinnerPollUserCityFilterSort = (Spinner) findViewById(R.id.spinnerPollUserCityFilterSort);
        spinnerPollUserAgeIntervalFilterSort = (Spinner) findViewById(R.id.spinnerPollUserAgeIntervalFilterSort);

        chkMultipleYesFilterSort = (CheckBox) findViewById(R.id.chkMultipleYesFilterSort);
        chkMultipleNoFilterSort = (CheckBox) findViewById(R.id.chkMultipleNoFilterSort);

        btnCancelFilterSort = (Button) findViewById(R.id.btnCancelFilterSort);
        btnApplyFilterSort = (Button) findViewById(R.id.btnApplyFilterSort);

        radioGenderFemaleFilterSort = (RadioButton) findViewById(R.id.radioGenderFemaleFilterSort);
        radioGenderMaleFilterSort = (RadioButton) findViewById(R.id.radioGenderMaleFilterSort);
        radioGenderBothFilterSort = (RadioButton) findViewById(R.id.radioGenderBothFilterSort);
        radioPollUserFilterSort = (RadioButton) findViewById(R.id.radioPollUserFilterSort);
        radioPollUserOtherFilterSort = (RadioButton) findViewById(R.id.radioPollUserOtherFilterSort);
        radioPollUserFemaleFilterSort = (RadioButton) findViewById(R.id.radioPollUserFemaleFilterSort);
        radioPollUserMaleFilterSort = (RadioButton) findViewById(R.id.radioPollUserMaleFilterSort);
        radioSortByPollTitleFilterSort = (RadioButton) findViewById(R.id.radioSortByPollTitleFilterSort);
        radioSortByCategoryNameFilterSort = (RadioButton) findViewById(R.id.radioSortByCategoryNameFilterSort);
        radioSortByPublishDateFilterSort = (RadioButton) findViewById(R.id.radioSortByPublishDateFilterSort);
        radioSortByOptionCountFilterSort = (RadioButton) findViewById(R.id.radioSortByOptionCountFilterSort);
        radioSortByVoteCountFilterSort = (RadioButton) findViewById(R.id.radioSortByVoteCountFilterSort);
        radioAscendingFilterSort = (RadioButton) findViewById(R.id.radioAscendingFilterSort);
        radioDescendingFilterSort = (RadioButton) findViewById(R.id.radioDescendingFilterSort);

        spinnerPollCategoryFilterSort.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories));
        spinnerPollUserCityFilterSort.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cities));
        spinnerPollUserAgeIntervalFilterSort.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ageIntervals));
        spinnerPollUserFilterSort.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, oyla.getUsers()));

        spinnerPollUserFilterSort.setEnabled(false);
        spinnerPollUserCityFilterSort.setEnabled(false);
        spinnerPollUserAgeIntervalFilterSort.setEnabled(false);
        radioPollUserFemaleFilterSort.setEnabled(false);
        radioPollUserMaleFilterSort.setEnabled(false);

        final RadioButtonCollection radioGroup = new RadioButtonCollection();
        radioGroup.add(radioPollUserFilterSort);
        radioGroup.add(radioPollUserOtherFilterSort);

        radioGroup.setOnSelectedItemChanged(new ValueChangedEvent<RadioButton>() {
            @Override
            public void valueChanged(RadioButton oldValue, RadioButton newValue) {
                if (newValue != null){
                    if (newValue.equals(radioPollUserFilterSort)) {
                        spinnerPollUserFilterSort.setEnabled(true);

                        spinnerPollUserCityFilterSort.setEnabled(false);
                        spinnerPollUserAgeIntervalFilterSort.setEnabled(false);
                        radioPollUserFemaleFilterSort.setEnabled(false);
                        radioPollUserMaleFilterSort.setEnabled(false);
                    } else if (newValue.equals(radioPollUserOtherFilterSort)) {
                        spinnerPollUserFilterSort.setEnabled(false);

                        spinnerPollUserCityFilterSort.setEnabled(true);
                        spinnerPollUserAgeIntervalFilterSort.setEnabled(true);
                        radioPollUserFemaleFilterSort.setEnabled(true);
                        radioPollUserMaleFilterSort.setEnabled(true);
                    }
                }
            }
        });

        btnCancelFilterSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent();
                FilterSortActivity.this.setResult(Activity.RESULT_CANCELED, intent);
                FilterSortActivity.this.finish();
            }
        });

        btnApplyFilterSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FilterAndSortOptions options = new FilterAndSortOptions();

                final Category category = (Category) spinnerPollCategoryFilterSort.getSelectedItem();
                if (category.getId() != -1) options.setPollCategory(category);

                if (radioGenderFemaleFilterSort.isChecked()) options.setPollGender(FilterAndSortOptions.PollGender.GENDER_FEMALE);
                else if (radioGenderMaleFilterSort.isChecked()) options.setPollGender(FilterAndSortOptions.PollGender.GENDER_MALE);
                else if (radioGenderBothFilterSort.isChecked()) options.setPollGender(FilterAndSortOptions.PollGender.GENDER_BOTH);

                if (chkMultipleYesFilterSort.isChecked() && chkMultipleNoFilterSort.isChecked()) options.setPollMultiple(FilterAndSortOptions.PollMultiple.BOTH);
                else if (chkMultipleYesFilterSort.isChecked()) options.setPollMultiple(FilterAndSortOptions.PollMultiple.YES);
                else if (chkMultipleNoFilterSort.isChecked()) options.setPollMultiple(FilterAndSortOptions.PollMultiple.NO);
                else options.setPollMultiple(FilterAndSortOptions.PollMultiple.BOTH);

                if (radioPollUserFilterSort.isChecked()) {
                    options.setPollUser((User) spinnerPollUserFilterSort.getSelectedItem());
                } else if (radioPollUserOtherFilterSort.isChecked()) {
                    final City city = (City) spinnerPollUserCityFilterSort.getSelectedItem();
                    if (city.getId() != -1) options.setPollUserCity(city);

                    if (radioPollUserFemaleFilterSort.isChecked()) options.setPollUserGender(FilterAndSortOptions.UserGender.FEMALE);
                    else if (radioPollUserMaleFilterSort.isChecked()) options.setPollUserGender(FilterAndSortOptions.UserGender.MALE);

                    final String ageInterval = (String) spinnerPollUserAgeIntervalFilterSort.getSelectedItem();
                    if (ageInterval.equals("18'den küçük")) options.setPollUserAgeInterval(FilterAndSortOptions.UserAgeInterval.UNDER_18);
                    else if (ageInterval.equals("18-25")) options.setPollUserAgeInterval(FilterAndSortOptions.UserAgeInterval.BETWEEN_18_25);
                    else if (ageInterval.equals("26-40")) options.setPollUserAgeInterval(FilterAndSortOptions.UserAgeInterval.BETWEEN_26_40);
                    else if (ageInterval.equals("41-65")) options.setPollUserAgeInterval(FilterAndSortOptions.UserAgeInterval.BETWEEN_41_65);
                    else options.setPollUserAgeInterval(FilterAndSortOptions.UserAgeInterval.ABOVE_65);
                }

                if (radioSortByPollTitleFilterSort.isChecked()) options.setSortField(FilterAndSortOptions.SortField.BY_TITLE);
                else if (radioSortByCategoryNameFilterSort.isChecked()) options.setSortField(FilterAndSortOptions.SortField.BY_CATEGORY_NAME);
                else if (radioSortByPublishDateFilterSort.isChecked()) options.setSortField(FilterAndSortOptions.SortField.BY_PUBLISH_DATE);
                else if (radioSortByOptionCountFilterSort.isChecked()) options.setSortField(FilterAndSortOptions.SortField.BY_OPTION_COUNT);
                else if (radioSortByVoteCountFilterSort.isChecked()) options.setSortField(FilterAndSortOptions.SortField.BY_VOTE_COUNT);

                if (radioAscendingFilterSort.isChecked()) options.setSortOrder(FilterAndSortOptions.SortOrder.ASCENDING);
                else if (radioDescendingFilterSort.isChecked()) options.setSortOrder(FilterAndSortOptions.SortOrder.DESCENDING);

                final Intent intent = new Intent();
                intent.putExtra("filterSort", options);
                FilterSortActivity.this.setResult(Activity.RESULT_OK, intent);
                FilterSortActivity.this.finish();
            }
        });

        guiInitialized = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_sort);
        final OylaDatabase oyla = ((MyApplication) getApplication()).oyla();
        if (!guiInitialized) initializeGui(oyla);
    }

}
