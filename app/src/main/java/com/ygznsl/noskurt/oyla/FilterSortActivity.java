package com.ygznsl.noskurt.oyla;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.ygznsl.noskurt.oyla.entity.Category;
import com.ygznsl.noskurt.oyla.helper.Consumer;
import com.ygznsl.noskurt.oyla.helper.FilterAndSortOptions;
import com.ygznsl.noskurt.oyla.helper.Nullable;

import java.util.List;

public class FilterSortActivity extends AppCompatActivity {

    private boolean guiInitialized = false;

    private Spinner spinnerPollCategoryFilterSort;

    private CheckBox chkMultipleYesFilterSort;
    private CheckBox chkMultipleNoFilterSort;

    private RadioButton radioGenderFemaleFilterSort;
    private RadioButton radioGenderMaleFilterSort;
    private RadioButton radioGenderBothFilterSort;
    private RadioButton radioSortByPollTitleFilterSort;
    private RadioButton radioSortByCategoryNameFilterSort;
    private RadioButton radioSortByPublishDateFilterSort;
    private RadioButton radioSortByOptionCountFilterSort;
    private RadioButton radioSortByVoteCountFilterSort;
    private RadioButton radioAscendingFilterSort;
    private RadioButton radioDescendingFilterSort;

    private void initializeGui(){
        final List<Category> categories = Category.getCategories(this).get();
        categories.add(0, Category.all());

        spinnerPollCategoryFilterSort = (Spinner) findViewById(R.id.spinnerPollCategoryFilterSort);
        chkMultipleYesFilterSort = (CheckBox) findViewById(R.id.chkMultipleYesFilterSort);
        chkMultipleNoFilterSort = (CheckBox) findViewById(R.id.chkMultipleNoFilterSort);
        radioGenderFemaleFilterSort = (RadioButton) findViewById(R.id.radioGenderFemaleFilterSort);
        radioGenderMaleFilterSort = (RadioButton) findViewById(R.id.radioGenderMaleFilterSort);
        radioGenderBothFilterSort = (RadioButton) findViewById(R.id.radioGenderBothFilterSort);
        radioSortByPollTitleFilterSort = (RadioButton) findViewById(R.id.radioSortByPollTitleFilterSort);
        radioSortByCategoryNameFilterSort = (RadioButton) findViewById(R.id.radioSortByCategoryNameFilterSort);
        radioSortByPublishDateFilterSort = (RadioButton) findViewById(R.id.radioSortByPublishDateFilterSort);
        radioSortByOptionCountFilterSort = (RadioButton) findViewById(R.id.radioSortByOptionCountFilterSort);
        radioSortByVoteCountFilterSort = (RadioButton) findViewById(R.id.radioSortByVoteCountFilterSort);
        radioAscendingFilterSort = (RadioButton) findViewById(R.id.radioAscendingFilterSort);
        radioDescendingFilterSort = (RadioButton) findViewById(R.id.radioDescendingFilterSort);

        final Button btnCancelFilterSort = (Button) findViewById(R.id.btnCancelFilterSort);
        final Button btnApplyFilterSort = (Button) findViewById(R.id.btnApplyFilterSort);

        spinnerPollCategoryFilterSort.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories));

        final CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!chkMultipleYesFilterSort.isChecked() && !chkMultipleNoFilterSort.isChecked()){
                    chkMultipleYesFilterSort.setChecked(true);
                    chkMultipleNoFilterSort.setChecked(true);
                }
            }
        };

        chkMultipleYesFilterSort.setOnCheckedChangeListener(listener);
        chkMultipleNoFilterSort.setOnCheckedChangeListener(listener);

        final Nullable<FilterAndSortOptions> options = new Nullable<>((FilterAndSortOptions) getIntent().getExtras().getSerializable("filterSort"));
        options.operate(new Consumer<FilterAndSortOptions>() {
            @Override
            public void accept(FilterAndSortOptions in) {
                spinnerPollCategoryFilterSort.setSelection(categories.indexOf(in.getPollCategory()));

                final FilterAndSortOptions.PollGender pollGender = in.getPollGender();
                final FilterAndSortOptions.PollMultiple pollMultiple = in.getPollMultiple();
                final FilterAndSortOptions.SortField sortField = in.getSortField();
                final FilterAndSortOptions.SortOrder sortOrder = in.getSortOrder();

                if (pollGender == FilterAndSortOptions.PollGender.GENDER_BOTH) radioGenderBothFilterSort.setChecked(true);
                else if (pollGender == FilterAndSortOptions.PollGender.GENDER_FEMALE) radioGenderFemaleFilterSort.setChecked(true);
                else if (pollGender == FilterAndSortOptions.PollGender.GENDER_MALE) radioGenderMaleFilterSort.setChecked(true);

                if (pollMultiple != FilterAndSortOptions.PollMultiple.BOTH){
                    if (pollMultiple == FilterAndSortOptions.PollMultiple.YES) chkMultipleNoFilterSort.setChecked(false);
                    else if (pollMultiple == FilterAndSortOptions.PollMultiple.NO) chkMultipleYesFilterSort.setChecked(false);
                }

                if (sortField == FilterAndSortOptions.SortField.BY_TITLE) radioSortByPollTitleFilterSort.setChecked(true);
                else if (sortField == FilterAndSortOptions.SortField.BY_CATEGORY_NAME) radioSortByCategoryNameFilterSort.setChecked(true);
                else if (sortField == FilterAndSortOptions.SortField.BY_PUBLISH_DATE) radioSortByPublishDateFilterSort.setChecked(true);
                else if (sortField == FilterAndSortOptions.SortField.BY_OPTION_COUNT) radioSortByOptionCountFilterSort.setChecked(true);
                else if (sortField == FilterAndSortOptions.SortField.BY_VOTE_COUNT) radioSortByVoteCountFilterSort.setChecked(true);

                if (sortOrder == FilterAndSortOptions.SortOrder.ASCENDING) radioAscendingFilterSort.setChecked(true);
                else if (sortOrder == FilterAndSortOptions.SortOrder.DESCENDING) radioDescendingFilterSort.setChecked(true);
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
        MyApplication.setIconBar(this);
        setTitle(" Filtrele ve Sırala");
        if (!guiInitialized) initializeGui();
    }

}
