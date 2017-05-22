package com.ygznsl.noskurt.oyla;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ygznsl.noskurt.oyla.entity.Category;
import com.ygznsl.noskurt.oyla.entity.Entity;
import com.ygznsl.noskurt.oyla.entity.Poll;
import com.ygznsl.noskurt.oyla.entity.User;
import com.ygznsl.noskurt.oyla.helper.Function;
import com.ygznsl.noskurt.oyla.helper.OylaDatabase;

import java.text.ParseException;
import java.util.List;

public class PollViewAdapter extends RecyclerView.Adapter<PollViewAdapter.CustomViewHolder> {

    private final List<Category> categories;
    private final List<Poll> polls;
    private final OylaDatabase oyla;
    private final Context context;

    public PollViewAdapter(Context context, List<Poll> polls, OylaDatabase oyla) {
        this.polls = polls;
        this.oyla = oyla;
        this.context = context;
        categories = Category.getCategories(context).get();
    }

    public List<Poll> getPolls() {
        return polls;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.card_view_poll_list, null));
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, int position) {
        final Poll poll = polls.get(position);

        holder.txtPollTitlePollView.setText(poll.getTitle());
        try {
            holder.txtPollPublishDatePollView.setText(User.DATE_FORMAT.format(Poll.DATE_FORMAT.parse(poll.getPdate())));
        } catch (ParseException ex) {
            holder.txtPollPublishDatePollView.setText(poll.getPdate());
        }

        holder.txtPollCategoryPollView.setText(Entity.findById(categories, poll.getCategory()).orElse(new Function<Category, String>() {
            @Override
            public String apply(Category in) {
                return in.getName();
            }
        }, ""));

        holder.imgPollGenderPollView.setImageResource(
                poll.getGenders().equals("B") ?
                        R.drawable.gender_both :
                        (poll.getGenders().equals("E") ?
                                R.drawable.gender_male :
                                R.drawable.gender_female
                        )
        );
    }

    @Override
    public int getItemCount() {
        return (polls != null ? polls.size() : 0);
    }

    public void add(Poll poll) {
        polls.add(poll);
        notifyDataSetChanged();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        final TextView txtPollTitlePollView;
        final TextView txtPollPublishDatePollView;
        final TextView txtPollCategoryPollView;
        final ImageView imgPollGenderPollView;

        public CustomViewHolder(View view) {
            super(view);
            txtPollTitlePollView = (TextView) view.findViewById(R.id.txtPollTitlePollView);
            txtPollPublishDatePollView = (TextView) view.findViewById(R.id.txtPollPublishDatePollView);
            txtPollCategoryPollView = (TextView) view.findViewById(R.id.txtPollCategoryPollView);
            imgPollGenderPollView = (ImageView) view.findViewById(R.id.imgPollGenderPollView);
        }

    }

}
