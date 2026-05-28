package com.example.heleneapp;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class VideoActivity extends AppCompatActivity {

    private VideoView videoView;
    private MediaController mediaController;
    private ProgressBar progressBar;
    private int currentPosition = 0;
    private  ImageButton btnVolver;
    private  Uri videoUri;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        videoView   = findViewById(R.id.videoView);
        progressBar = findViewById(R.id.progressBar);
        btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(v -> finish());

        mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.helene_video);
        videoView.setVideoURI(videoUri);

        progressBar.setVisibility(View.VISIBLE);

        videoView.setOnPreparedListener(mediaPlayer -> {
            progressBar.setVisibility(View.GONE);

            if (currentPosition > 0) {
                videoView.seekTo(currentPosition);
            }
            videoView.start();
        });

        videoView.setOnErrorListener((mp, what, extra) -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Error al cargar el vídeo", Toast.LENGTH_SHORT).show();
            return true;
        });

        videoView.setOnInfoListener((mp, what, extra) -> {
            if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                progressBar.setVisibility(View.VISIBLE);
            } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                progressBar.setVisibility(View.GONE);
            }
            return true;
        });

        videoView.setOnCompletionListener(mp ->{videoView.start();});
    }

    @Override
    protected void onPause() {
        super.onPause();
        currentPosition = videoView.getCurrentPosition();
        if (videoView.isPlaying()) {
            videoView.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        videoView.stopPlayback();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentPosition > 0) {
            videoView.seekTo(currentPosition);
            videoView.start();
        }
    }
}