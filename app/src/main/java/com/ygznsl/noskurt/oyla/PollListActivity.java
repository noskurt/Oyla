package com.ygznsl.noskurt.oyla;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.ygznsl.noskurt.oyla.entity.Category;
import com.ygznsl.noskurt.oyla.entity.City;
import com.ygznsl.noskurt.oyla.entity.Entity;
import com.ygznsl.noskurt.oyla.entity.Poll;
import com.ygznsl.noskurt.oyla.entity.User;
import com.ygznsl.noskurt.oyla.helper.FilterAndSortOptions;
import com.ygznsl.noskurt.oyla.helper.Function;
import com.ygznsl.noskurt.oyla.helper.Nullable;
import com.ygznsl.noskurt.oyla.helper.OylaDatabase;

import org.joda.time.LocalDate;
import org.joda.time.Years;

import java.text.Collator;
import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class PollListActivity extends AppCompatActivity {

    public static final int FILTER_SORT = 4445;

    private FilterAndSortOptions options = new FilterAndSortOptions();
    private boolean guiInitialized = false;
    private boolean anonymous;
    private List<Category> categories;
    private List<City> cities;
    private List<Poll> polls;
    private User user;

    private LinearLayout llMainLayoutPollList;
    private RecyclerView rvPollList;
    private PollViewAdapter adapter;
    private ProgressBar pbPollList;

    private void showProgressPar(){
        if (llMainLayoutPollList != null){
            llMainLayoutPollList.setVisibility(View.GONE);
        }
        if (pbPollList != null){
            pbPollList.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgressPar(){
        if (pbPollList != null){
            pbPollList.setVisibility(View.GONE);
        }
        if (llMainLayoutPollList != null){
            llMainLayoutPollList.setVisibility(View.VISIBLE);
        }
    }

    private void sort(final OylaDatabase oyla){
        final AsyncTask<FilterAndSortOptions, Integer, Boolean> task = new AsyncTask<FilterAndSortOptions, Integer, Boolean>() {
            @Override
            protected void onPreExecute() {
                showProgressPar();
            }

            protected Boolean doInBackground(FilterAndSortOptions... filterAndSortOptionses) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException ex) {
                    Log.e("sort.task", ex.getMessage());
                }
                final List<Poll> list = new LinkedList<>();
                for (Poll poll : polls){
                    final Category category = options.getPollCategory();
                    if (category.getId() != -1 && poll.getCategory() != category.getId()) continue;
                    final FilterAndSortOptions.PollGender pollGender = options.getPollGender();
                    if (pollGender != FilterAndSortOptions.PollGender.GENDER_BOTH){
                        if (pollGender == FilterAndSortOptions.PollGender.GENDER_FEMALE && poll.getGenders().equals("E")) continue;
                        if (pollGender == FilterAndSortOptions.PollGender.GENDER_MALE && poll.getGenders().equals("K")) continue;
                    }
                    final FilterAndSortOptions.PollMultiple pollMultiple = options.getPollMultiple();
                    if (pollMultiple != FilterAndSortOptions.PollMultiple.BOTH){
                        if (pollMultiple == FilterAndSortOptions.PollMultiple.YES && poll.getMult() == 0) continue;
                        if (pollMultiple == FilterAndSortOptions.PollMultiple.NO && poll.getMult() == 1) continue;
                    }
                    if (options.isPollUserSpecified()) {
                        if (options.getPollUser().getId() != poll.getUser()) continue;
                    } else {
                        final User user = oyla.getUserById(poll.getUser());
                        if (options.isPollUserCitySpecified()){
                            if (options.getPollUserCity().getId() != user.getCity()) continue;
                        }
                        if (options.isPollUserGenderSpecified()){
                            if (options.getPollUserGender() == FilterAndSortOptions.UserGender.FEMALE && user.getGender().equals("E")) continue;
                            if (options.getPollUserGender() == FilterAndSortOptions.UserGender.MALE && user.getGender().equals("K")) continue;
                        }
                        if (options.isPollUserAgeIntervalSpecified()){
                            try {
                                final FilterAndSortOptions.UserAgeInterval interval = options.getPollUserAgeInterval();
                                final LocalDate birthDate = LocalDate.fromDateFields(User.DATE_FORMAT.parse(user.getBdate()));
                                final int age = Years.yearsBetween(birthDate, LocalDate.now()).getYears();

                                if (interval == FilterAndSortOptions.UserAgeInterval.UNDER_18 && age >= 18) continue;
                                if (interval == FilterAndSortOptions.UserAgeInterval.BETWEEN_18_25 && (age < 18 || age > 25)) continue;
                                if (interval == FilterAndSortOptions.UserAgeInterval.BETWEEN_26_40 && (age < 26 || age > 40)) continue;
                                if (interval == FilterAndSortOptions.UserAgeInterval.BETWEEN_41_65 && (age < 41 || age > 65)) continue;
                                if (interval == FilterAndSortOptions.UserAgeInterval.ABOVE_65 && age <= 65) continue;
                            } catch (ParseException ex) {
                                Log.e("sort.task", ex.getMessage());
                            }
                        }
                    }
                    list.add(poll);
                    if (!list.isEmpty()){
                        Collections.sort(list, new Comparator<Poll>() {
                            @Override
                            public int compare(Poll p1, Poll p2) {
                                final FilterAndSortOptions.SortField field = options.getSortField();
                                final Collator collator = Collator.getInstance(new Locale("tr", "TR"));
                                int result = collator.compare(p1.getTitle(), p2.getTitle());
                                if (field == FilterAndSortOptions.SortField.BY_TITLE){
                                    result = collator.compare(p1.getTitle(), p2.getTitle());
                                } else if (field == FilterAndSortOptions.SortField.BY_CATEGORY_NAME) {
                                    final Category c1 = Entity.findById(categories, p1.getCategory()).get();
                                    final Category c2 = Entity.findById(categories, p2.getCategory()).get();
                                    result = collator.compare(c1.getName(), c2.getName());
                                } else if (field == FilterAndSortOptions.SortField.BY_PUBLISH_DATE) {
                                    try {
                                        final Date d1 = Poll.DATE_FORMAT.parse(p1.getPdate());
                                        final Date d2 = Poll.DATE_FORMAT.parse(p2.getPdate());
                                        result = d1.compareTo(d2);
                                    } catch (ParseException ex) {
                                        Log.e("sort.task.uithread", ex.getMessage());
                                    }
                                } else if (field == FilterAndSortOptions.SortField.BY_OPTION_COUNT){
                                    final int count1 = oyla.optionsOfPoll(p1).size();
                                    final int count2 = oyla.optionsOfPoll(p2).size();
                                    result = Integer.valueOf(count1).compareTo(count2);
                                } else if (field == FilterAndSortOptions.SortField.BY_VOTE_COUNT){
                                    final int count1 = oyla.votesOfPoll(p1).size();
                                    final int count2 = oyla.votesOfPoll(p2).size();
                                    result = Integer.valueOf(count1).compareTo(count2);
                                }
                                return options.getSortOrder() == FilterAndSortOptions.SortOrder.ASCENDING ? result : 0 - result;
                            }
                        });
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final PollViewAdapter pva = new PollViewAdapter(PollListActivity.this, list, oyla);
                            rvPollList.setAdapter(pva);
                            hideProgressPar();
                        }
                    });
                }
                return true;
            }
        };
        task.execute();
    }

    private void initializeGui(final OylaDatabase oyla){
        categories = Category.getCategories(this).get();
        cities = City.getCities(this).get();

        final Bundle extras = getIntent().getExtras();
        user = (User) extras.getSerializable("user");
        anonymous = extras.getBoolean("anonymous");
        final Nullable<String> scope = new Nullable<>(extras.getString("scope"));

        setTitle(scope.orElse(new Function<String, String>() {
            @Override
            public String apply(String in) {
                if (in.equals("polls")) return " Yayınladıklarım";
                if (in.equals("votes")) return " Oyladıklarım";
                return " Anketler";
            }
        }, "Oyla"));

        rvPollList = (RecyclerView) findViewById(R.id.rvPollList);
        rvPollList.setLayoutManager(new LinearLayoutManager(this));
        pbPollList = (ProgressBar) findViewById(R.id.pbPollList);
        llMainLayoutPollList = (LinearLayout) findViewById(R.id.llMainLayoutPollList);

        polls = new LinkedList<>(new HashSet<>(anonymous ? oyla.getPolls() :
                scope.orElse(new Function<String, List<Poll>>() {
                    @Override
                    public List<Poll> apply(String in) {
                        if (in.equals("polls")) return oyla.pollsUserCreated(user);
                        if (in.equals("votes")) return oyla.pollsUserVoted(user);
                        return oyla.getPolls();
                    }
                }, oyla.getPolls())
        ));

        rvPollList.addOnItemTouchListener(new RecyclerItemClick(this, rvPollList, new RecyclerItemClick.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                final Intent intent = new Intent(PollListActivity.this, PollActivity.class);
                final Poll poll = ((PollViewAdapter) rvPollList.getAdapter()).getPolls().get(position);

                intent.putExtra("user", user);
                intent.putExtra("anonymous", anonymous);
                intent.putExtra("poll", poll);
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {}
        }));

        pbPollList.setVisibility(View.GONE);
        llMainLayoutPollList.setVisibility(View.VISIBLE);

        sort(oyla);
        guiInitialized = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.filter_sort_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuFilterAndSort){
            final Intent intent = new Intent(this, FilterSortActivity.class);
            startActivityForResult(intent, FILTER_SORT);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == FILTER_SORT){
            options = (FilterAndSortOptions) data.getExtras().getSerializable("filterSort");
            final OylaDatabase oyla = ((MyApplication) getApplication()).oyla();
            sort(oyla);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_list);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        final OylaDatabase oyla = ((MyApplication) getApplication()).oyla();
        if (!guiInitialized) initializeGui(oyla);
    }

}