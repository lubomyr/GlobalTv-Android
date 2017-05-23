package atua.anddev.globaltv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import atua.anddev.globaltv.adapters.GuideExpListAdapter;
import atua.anddev.globaltv.entity.Channel;
import atua.anddev.globaltv.entity.Programme;

public class GuideActivity extends Activity implements GlobalServices, View.OnClickListener {
    private Channel channel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set sub.xml as user interface layout
        setContentView(R.layout.guidelist);

        channel = getData();
        showInfo(channel);
        showGuide(channel);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.favoriteIcon:
                changeFavorite(channel);
                break;
            case R.id.playIcon:
                setActivePlayIcon();
                openChannel(channel);
                break;
        }
    }

    private Channel getData() {
        Intent intent = this.getIntent();
        return (Channel) intent.getSerializableExtra("channel");
    }

    private void showGuide(Channel channel) {
        List<Programme> guideList = guideService.getChannelGuide(channel.getName());

        ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.guideList);
        GuideExpListAdapter adapter = new GuideExpListAdapter(this, guideList, false);
        expandableListView.setAdapter(adapter);
        int pos = guideService.getProgramPos(channel.getName());
        int total = guideList.size();
        int showpos = pos - (total / pos);
        expandableListView.setSelection(showpos);
    }

    private void showInfo(Channel item) {
        String icon;

        TextView channelNameView = (TextView) findViewById(R.id.channelName);
        channelNameView.setText(item.getName());

        ImageView logoView = (ImageView) findViewById(R.id.logoIcon);
        if ((item.getIcon() != null) && (!item.getIcon().isEmpty()))
            icon = item.getIcon();
        else
            icon = logoService.getLogoByName(item.getName());
        if ((icon != null) && !icon.isEmpty()) {
            Glide.with(this).load(icon).into(logoView);
        } else
            logoView.setImageDrawable(null);

        ImageView favoriteView = (ImageView) findViewById(R.id.favoriteIcon);
        if (favoriteService.isChannelFavorite(item)) {
            favoriteView.setImageResource(R.drawable.favorite_active);
        } else {
            favoriteView.setImageResource(R.drawable.favorite_inactive);
        }
        favoriteView.setOnClickListener(this);

        ImageView playView = (ImageView) findViewById(R.id.playIcon);
        playView.setOnClickListener(this);
    }

    private void changeFavorite(Channel item) {
        if (!favoriteService.isChannelFavorite(item))
            favoriteService.addToFavoriteList(item);
        else
            favoriteService.deleteFromFavorites(item);
        showInfo(item);
    }

    private void setActivePlayIcon() {
        ImageView playView = (ImageView) findViewById(R.id.playIcon);
        playView.setImageResource(R.drawable.play_active);
    }

    private void openChannel(Channel channel) {
        if (Global.useInternalPlayer) {
            Intent intent = new Intent(GuideActivity.this, PlayerActivity.class);
            intent.putExtra("channel", channel);
            startActivity(intent);
        } else
            channelService.openChannel(GuideActivity.this, channel);
    }
}
