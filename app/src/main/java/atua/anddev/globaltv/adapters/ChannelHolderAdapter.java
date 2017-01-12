package atua.anddev.globaltv.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import atua.anddev.globaltv.Global;
import atua.anddev.globaltv.R;
import atua.anddev.globaltv.entity.Channel;

import static atua.anddev.globaltv.GlobalServices.favoriteService;
import static atua.anddev.globaltv.GlobalServices.guideService;
import static atua.anddev.globaltv.GlobalServices.logoService;

public class ChannelHolderAdapter extends RecyclerView.Adapter<ChannelHolderAdapter.ViewHolder> {
    private Activity activity;
    private int resources;
    private List<Channel> items;
    private OnItemClickListener mOnItemClickListener;
    private Channel selectedItem;
    private boolean showProvName;

    public ChannelHolderAdapter(Activity activity, int resources, List<Channel> items, boolean showProvName) {
        this.activity = activity;
        this.items = items;
        this.resources = resources;
        this.showProvName = showProvName;
    }

    @Override
    public ChannelHolderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(resources, parent, false);

        return new ChannelHolderAdapter.ViewHolder(itemView, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(final ChannelHolderAdapter.ViewHolder holder, int position) {
        final Channel item = items.get(position);
        holder.setItem(item);
        holder.chNameView.setText(item.getName());
        holder.provNameView.setText(item.getProvider());
        holder.provNameView.setVisibility(showProvName ? View.VISIBLE : View.GONE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (Global.guideLoaded) {
                    String title = guideService.getProgramTitle(item.getName());
                    title = (title != null) ? title : "";
                    final String finalTitle = title;
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            holder.titleView.setText(Html.fromHtml(finalTitle));
                        }
                    });
                }
            }
        }).start();
        if (selectedItem == item) {
            holder.playView.setImageResource(R.drawable.play_active);
        } else {
            holder.playView.setImageResource(R.drawable.play_inactive);
        }
        if (isChannelFavorite(item)) {
            holder.favoriteView.setImageResource(R.drawable.favorite_active);
        } else {
            holder.favoriteView.setImageResource(R.drawable.favorite_inactive);
        }
        String icon = logoService.getLogoByName(item.getName());
        if ((icon != null) && !icon.isEmpty()) {
            Glide.with(activity).load(icon).into(holder.logoView);
        } else
            holder.logoView.setImageDrawable(null);
        holder.itemView.setTag(item);
    }

    public void setItems(List<Channel> items) {
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setSelected(Channel item) {
        this.selectedItem = item;
    }

    private boolean isChannelFavorite(Channel item) {
        Boolean result = false;
        for (Channel fav : favoriteService.getFavoriteList()) {
            if (item.getName().equals(fav.getName())
                    && item.getProvider().equals(fav.getProvider()))
                result = true;
        }
        return result;
    }

    public interface OnItemClickListener {
        void onItemClick(Channel item, int viewId);
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ChannelHolderAdapter.OnItemClickListener mOnItemClickListener;
        Channel item;
        TextView chNameView;
        TextView provNameView;
        TextView titleView;
        ImageView logoView;
        ImageView playView;
        ImageView favoriteView;

        ViewHolder(View view, ChannelHolderAdapter.OnItemClickListener onItemClickListener) {
            super(view);
            mOnItemClickListener = onItemClickListener;
            view.findViewById(R.id.clickItem).setOnClickListener(ViewHolder.this);
            chNameView = (TextView) view.findViewById(R.id.heading);
            provNameView = (TextView) view.findViewById(R.id.provName);
            titleView = (TextView) view.findViewById(R.id.title);
            logoView = (ImageView) view.findViewById(R.id.logoIcon);
            playView = (ImageView) view.findViewById(R.id.tickIcon);
            favoriteView = (ImageView) view.findViewById(R.id.favoriteIcon);
            playView.setOnClickListener(ViewHolder.this);
            favoriteView.setOnClickListener(ViewHolder.this);
            titleView.setOnClickListener(ViewHolder.this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(item, v.getId());
            }
        }

        public void setItem(Channel item) {
            this.item = item;
        }
    }
}
