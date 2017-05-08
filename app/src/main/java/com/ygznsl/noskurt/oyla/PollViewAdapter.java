package com.ygznsl.noskurt.oyla;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ygznsl.noskurt.oyla.entity.Poll;

import java.util.List;

public class PollViewAdapter extends RecyclerView.Adapter<PollViewAdapter.CustomViewHolder> {

    private List<Poll> pollItemList;
    private Context context;

    public PollViewAdapter(Context context, List<Poll> pollItemList) {
        this.pollItemList = pollItemList;
        this.context = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_poll_list, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    /**
     *  Item den veri çekip veriyi görsel
     *  elemanlara ekleme işemi de burada oluyor
     */

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        final Poll pollItem = pollItemList.get(position);

        holder.title.setText(pollItem.getDate() + " - " + pollItem.getTitle());
        holder.content.setText(pollItem.getPreview());
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
        protected ImageView thumbnail;
        protected TextView title;
        protected TextView content;

        public CustomViewHolder(View itemView) {
            super(itemView);
            this.thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            this.title = (TextView) itemView.findViewById(R.id.title);
            this.content = (TextView) itemView.findViewById(R.id.content);
        }
    }
}
