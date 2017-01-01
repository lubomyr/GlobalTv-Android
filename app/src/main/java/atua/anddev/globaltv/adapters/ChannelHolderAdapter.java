package atua.anddev.globaltv.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import atua.anddev.globaltv.Global;
import atua.anddev.globaltv.R;
import atua.anddev.globaltv.entity.Channel;

import static atua.anddev.globaltv.GlobalServices.favoriteService;
import static atua.anddev.globaltv.GlobalServices.guideService;

public class ChannelHolderAdapter extends RecyclerView.Adapter<ChannelHolderAdapter.ViewHolder> {
    private Context context;
    private int resources;
    private List<Channel> items;
    private OnItemClickListener  mOnItemClickListener;
    private int selectedProvider;
    private String selectedChannelId="";

    public ChannelHolderAdapter(Context context, int resources, List<Channel> items, int provider) {
        this.context = context;
        this.items = items;
        this.resources = resources;
        this.selectedProvider = provider;
    }

    @Override
    public ChannelHolderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(resources, parent, false);

        return new ChannelHolderAdapter.ViewHolder(itemView, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(ChannelHolderAdapter.ViewHolder holder, int position) {
        Channel item = items.get(position);
        holder.setItem(item);
        holder.chNameView.setText(item.getName());
        if (Global.guideLoaded) {
            String title = guideService.getProgramTitle(item.getName());
            title = (title != null) ? title : "";
            holder.titleView.setText(Html.fromHtml(title));
        }
        if (selectedChannelId.equals(item.getUrl())) {
            holder.playView.setImageResource(R.drawable.play_active);
        } else {
            holder.playView.setImageResource(R.drawable.play_inactive);
        }
        if (isChannelFavorite(item)) {
            holder.favoriteView.setImageResource(R.drawable.favorite_active);
        } else {
            holder.favoriteView.setImageResource(R.drawable.favorite_inactive);
        }
        holder.itemView.setTag(item);
    }

    public void setItems(List<Channel> items) {
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        mOnItemClickListener = onItemClickListener;
    }

    public void setSelected(String str) {
        this.selectedChannelId = str;
    }

    private boolean isChannelFavorite(Channel item) {
        boolean result;
        int index = favoriteService.getFavoriteListForProv(selectedProvider).indexOf(item.getName());
        result = index != -1;
        return result;
    }

    public interface OnItemClickListener{
        void onItemClick(Channel item, int viewId);
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ChannelHolderAdapter.OnItemClickListener mOnItemClickListener;
        Channel item;
        TextView chNameView;
        TextView titleView;
        ImageView playView;
        ImageView favoriteView;

        ViewHolder(View view, ChannelHolderAdapter.OnItemClickListener onItemClickListener) {
            super(view);
            mOnItemClickListener = onItemClickListener;
            view.findViewById(R.id.clickItem).setOnClickListener(ViewHolder.this);
            chNameView = (TextView) view.findViewById(R.id.heading);
            titleView  = (TextView) view.findViewById(R.id.title);
            playView = (ImageView) view.findViewById(R.id.tickIcon);
            favoriteView = (ImageView) view.findViewById(R.id.favoriteIcon);
            playView.setOnClickListener(ViewHolder.this);
            favoriteView.setOnClickListener(ViewHolder.this);
            titleView.setOnClickListener(ViewHolder.this);
        }

        @Override
        public void onClick(View v) {
            if(mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(item, v.getId());
            }
        }

        public void setItem(Channel item){
            this.item = item;
        }
    }
}
