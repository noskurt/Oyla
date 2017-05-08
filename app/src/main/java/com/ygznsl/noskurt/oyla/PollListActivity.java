package com.ygznsl.noskurt.oyla;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.ygznsl.noskurt.oyla.entity.Poll;

import java.util.List;

public class PollListActivity extends AppCompatActivity {

    private boolean guiInitialized = false;
    private List<Poll> pollsList;

    private RecyclerView mRecyclerView;
    private PollViewAdapter adapter;
    private ProgressBar progressBar;

    private void initializeGui(){
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        adapter = new PollViewAdapter(this, pollsList);
        mRecyclerView.setAdapter(adapter);

        /**
         * Herhangi bir ankete tıklandığı zaman
         * nereye yönlenecek o işlemler burada
         */
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClick(this, mRecyclerView, new RecyclerItemClick.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                /*Intent intent = new Intent(view.getContext(), PollActivity.class);
                Poll item = pollsList.get(position);
                intent.putExtra("DATA", item);
                startActivity(intent);*/
            }

            @Override
            public void onLongItemClick(View view, int position) {}
        }));

        guiInitialized = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_list);
        if (!guiInitialized) initializeGui();
    }

}