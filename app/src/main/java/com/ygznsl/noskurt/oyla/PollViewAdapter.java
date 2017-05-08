package com.ygznsl.noskurt.oyla;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ygznsl.noskurt.oyla.entity.Option;
import com.ygznsl.noskurt.oyla.entity.Poll;
import com.ygznsl.noskurt.oyla.entity.User;

import java.text.ParseException;
import java.util.List;

public class PollViewAdapter extends RecyclerView.Adapter<PollViewAdapter.CustomViewHolder> {

    private List<Poll> pollItemList;
    private Context context;

    public PollViewAdapter(Context context, List<Poll> pollItemList) {
        this.pollItemList = pollItemList;
        this.context = context;
    }

    public List<Poll> getPolls() {
        return pollItemList;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_poll_list, null));
    }

    /**
     *  Item den veri çekip veriyi görsel
     *  elemanlara ekleme işemi de burada oluyor
     */
    @Override
    public void onBindViewHolder(final CustomViewHolder holder, int position) {
        final Poll poll = pollItemList.get(position);

        holder.txtPollTitlePollView.setText(poll.getTitle());
        holder.txtPollUserPollView.setText(poll.getUser());
        try {
            holder.txtPollPublishDatePollView.setText(User.DATE_FORMAT.format(Poll.DATE_FORMAT.parse(poll.getPdate())));
        } catch (ParseException ex) {
            holder.txtPollPublishDatePollView.setText(poll.getPdate());
        }

        holder.imgPollGenderPollView.setImageResource(
                poll.getGenders().equals("B") ?
                        R.drawable.gender_both :
                        (poll.getGenders().equals("E") ?
                                R.drawable.gender_male :
                                R.drawable.gender_female
                        )
        );

        FirebaseDatabase.getInstance().getReference().child("option").orderByChild("poll").equalTo(poll.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final StringBuilder str = new StringBuilder();
                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                            str.append("\"").append(ds.getValue(Option.class).getTitle()).append("\", ");
                        }
                        final String options = "[" + str.toString().trim().substring(0, str.toString().trim().length() - 1) + "]";
                        holder.txtPollOptionsPollView.setText(options.length() <= 50 ? options : options.substring(0, 48) + "...");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
    }

    @Override
    public int getItemCount() {
        return (null != pollItemList ? pollItemList.size() : 0);
    }

    public void clear() {
        pollItemList.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Poll> list) {
        pollItemList.addAll(list);
        notifyDataSetChanged();
    }

    /**
     * findView işlemleri burda oluyor gerekli olanlar
     * burda eklenmesi lazım
     */
    class CustomViewHolder extends RecyclerView.ViewHolder {

        final RelativeLayout rlMainPollView;
        final TextView txtPollTitlePollView;
        final TextView txtPollPublishDatePollView;
        final TextView txtPollUserPollView;
        final TextView txtPollOptionsPollView;
        final ImageView imgPollGenderPollView;

        public CustomViewHolder(View view) {
            super(view);
            rlMainPollView = (RelativeLayout) view.findViewById(R.id.rlMainPollView);
            txtPollTitlePollView = (TextView) view.findViewById(R.id.txtPollTitlePollView);
            txtPollPublishDatePollView = (TextView) view.findViewById(R.id.txtPollPublishDatePollView);
            txtPollUserPollView = (TextView) view.findViewById(R.id.txtPollUserPollView);
            txtPollOptionsPollView = (TextView) view.findViewById(R.id.txtPollOptionsPollView);
            imgPollGenderPollView = (ImageView) view.findViewById(R.id.imgPollGenderPollView);
        }

    }

}
