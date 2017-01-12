package atua.anddev.globaltv;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import atua.anddev.globaltv.entity.Channel;

import static atua.anddev.globaltv.GlobalServices.channelService;

public class PlayerActivity extends Activity implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnInfoListener {
    private MediaController mediaControls;
    private VideoView mVideoView;
    private Integer position;
    private Channel channel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);

        if (mediaControls == null)
            mediaControls = new MediaController(this);

        getData();
        setupPlayer();
        openChannel();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("Position", mVideoView.getCurrentPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int position = savedInstanceState.getInt("Position");
        mVideoView.seekTo(position);
    }

    @Override
    public boolean onInfo(MediaPlayer p1, int p2, int p3) {
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer p1) {
        if (!p1.isPlaying()) {
            openChannel();
        }
    }

    private void getData() {
        Intent intent = getIntent();
        channel = (Channel) intent.getSerializableExtra("channel");
    }

    private void setupPlayer() {
        mVideoView = (VideoView) findViewById(R.id.video);
        mVideoView.setMediaController(mediaControls);
        mVideoView.setOnCompletionListener(this);
    }

    private void openChannel() {
        if (channel.getProvider().equals("Kineskop.tv"))
            openWithUpdatedUrl();
        else {
            mVideoView.setVideoPath(channel.getUrl());
            mVideoView.start();
        }
    }

    private void openWithUpdatedUrl() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String url = channelService.getUpdatedUrl(channel);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mVideoView.setVideoPath(url);
                        mVideoView.start();
                    }
                });
            }
        }).start();
    }
}
