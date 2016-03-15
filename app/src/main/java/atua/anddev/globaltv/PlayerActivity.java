package atua.anddev.globaltv;

import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

public class PlayerActivity extends MainActivity {
    VideoView videoView;
    String selectedUrl;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set sub.xml as user interface layout
        setContentView(R.layout.player);
        videoView = (VideoView) findViewById(R.id.videoView1);
        Uri uri = Uri.parse(selectedUrl);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();

    }
}
