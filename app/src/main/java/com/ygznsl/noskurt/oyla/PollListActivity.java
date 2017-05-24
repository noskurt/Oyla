package com.ygznsl.noskurt.oyla;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ygznsl.noskurt.oyla.entity.Category;
import com.ygznsl.noskurt.oyla.entity.Poll;
import com.ygznsl.noskurt.oyla.entity.User;
import com.ygznsl.noskurt.oyla.helper.FilterAndSortOptions;
import com.ygznsl.noskurt.oyla.helper.Function;
import com.ygznsl.noskurt.oyla.helper.Nullable;
import com.ygznsl.noskurt.oyla.helper.OylaDatabase;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class PollListActivity extends AppCompatActivity {

    public static final int FILTER_SORT = 4445;

    private FilterAndSortOptions options = new FilterAndSortOptions();
    private boolean guiInitialized = false;
    private boolean anonymous;
    private List<Category> categories;
    private List<Poll> polls;
    private User user;

    private LinearLayout llMainLayoutPollList;
    private TextView txtNoPollsFoundPollList;
    private CustomRecyclerView rvPollList;
    private ProgressBar pbPollList;

    private void showProgressPar() {
        if (llMainLayoutPollList != null) {
            llMainLayoutPollList.setVisibility(View.GONE);
        }
        if (pbPollList != null) {
            pbPollList.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgressPar() {
        if (pbPollList != null) {
            pbPollList.setVisibility(View.GONE);
        }
        if (llMainLayoutPollList != null) {
            llMainLayoutPollList.setVisibility(View.VISIBLE);
        }
    }

    private void sort(final OylaDatabase oyla) {
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final List<Poll> filteredAndSorted = options.filterAndSort(polls, oyla, categories);
                        final PollViewAdapter pva = new PollViewAdapter(PollListActivity.this, filteredAndSorted, oyla);
                        rvPollList.setAdapter(pva);
                        txtNoPollsFoundPollList.setVisibility(filteredAndSorted.isEmpty() ? View.VISIBLE : View.GONE);
                        hideProgressPar();
                    }
                });
                return true;
            }
        };
        task.execute();
    }

    private void initializeGui(final OylaDatabase oyla) {
        categories = Category.getCategories(this).get();

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

        llMainLayoutPollList = (LinearLayout) findViewById(R.id.llMainLayoutPollList);
        txtNoPollsFoundPollList = (TextView) findViewById(R.id.txtNoPollsFoundPollList);
        rvPollList = (CustomRecyclerView) findViewById(R.id.rvPollList);
        pbPollList = (ProgressBar) findViewById(R.id.pbPollList);

        rvPollList.setLayoutManager(new LinearLayoutManager(this));

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
            public void onLongItemClick(View view, int position) {
            }
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
        if (item.getItemId() == R.id.menuFilterAndSort) {
            final Intent intent = new Intent(this, FilterSortActivity.class);
            intent.putExtra("filterSort", options);
            startActivityForResult(intent, FILTER_SORT);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == FILTER_SORT) {
            options = (FilterAndSortOptions) data.getExtras().getSerializable("filterSort");
            final OylaDatabase oyla = ((MyApplication) getApplication()).oyla();
            sort(oyla);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_list);
        MyApplication.setIconBar(this);
        final OylaDatabase oyla = ((MyApplication) getApplication()).oyla();
        if (!guiInitialized) initializeGui(oyla);
    }

}