package com.ns3.Attendance;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class Help extends AppCompatActivity {
    VideoView mVideoView;
    MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        mediaController = new MediaController(this);
        mVideoView = (VideoView) findViewById(R.id.VideoView);

        mediaController.setAnchorView(mVideoView);
        Uri localUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video);

        mVideoView.setVideoURI(localUri);
        mVideoView.setMediaController(mediaController);

        mVideoView.start();
    }
}
