package zerodev.p1261_mediaplayer;

import android.content.ContentUris;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener {

    final String LOG_TAG = "myLogs";

    final String DATA_HTTP = "http://dl.dropboxusercontent.com/u/6197740/explosion.mp3";
    final String DATA_STREAM = "http://online.radiorecord.ru:8101/rr_128";
    final String DATA_SD = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
            + "/music.mp3";
    final Uri DATA_URI = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, 13359);

    MediaPlayer mediaPlayer;
    AudioManager audioManager;
    CheckBox chbLoop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        chbLoop = (CheckBox)findViewById(R.id.chbLoop);
    }

    public void onClickStart(View v){
        releaseMP();
        try{
            switch (v.getId()){
                case R.id.btnStartHttp:
                    Log.d(LOG_TAG,"start HTTP");
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(DATA_HTTP);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    Log.d(LOG_TAG, "prepare Async");
                    mediaPlayer.setOnPreparedListener(this);
                    mediaPlayer.prepareAsync();
                    break;
                case R.id.btnStartStream:
                    Log.d(LOG_TAG,"start stream");
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(DATA_STREAM);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    Log.d(LOG_TAG,"prepare Async");
                    mediaPlayer.setOnPreparedListener(this);
                    mediaPlayer.prepareAsync();
                    break;
                case R.id.btnStartSD:
                    Log.d(LOG_TAG,"start SD: " + DATA_SD);
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(DATA_SD);
                    Toast.makeText(this,"set data source ok", Toast.LENGTH_LONG).show();
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.prepare();
                    Toast.makeText(this,"prepare ok", Toast.LENGTH_LONG).show();
                    mediaPlayer.start();
                    Toast.makeText(this,"start ok", Toast.LENGTH_LONG).show();
                    break;
                case R.id.btnStartUri:
                    Log.d(LOG_TAG,"start uri");
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(this, DATA_URI);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    break;
                case R.id.btnStartRaw:
                    Log.d(LOG_TAG,"start Raw");
                    mediaPlayer = MediaPlayer.create(this, R.raw.explosion);
                    mediaPlayer.start();
                    break;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        if (mediaPlayer == null) {
            return;
        }
        mediaPlayer.setLooping(chbLoop.isChecked());
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
            @Override
            public void onCompletion(MediaPlayer mediaPlayer){
                Log.d(LOG_TAG,"onCompletion");
            }
        });
    }

    private void releaseMP(){
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onClick(View v){
        if (mediaPlayer == null) {
            return;
        }
        switch (v.getId()){
            case R.id.btnPause:
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                }
                break;
            case R.id.btnResume:
                if(!mediaPlayer.isPlaying()){
                    mediaPlayer.release();
                }
                break;
            case R.id.btnStop:
                mediaPlayer.stop();
                break;
            case R.id.btnBackward:
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 3000);
                break;
            case R.id.btnForward:
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 3000);
                break;
            case R.id.btnInfo:
                Log.d(LOG_TAG,"Playing " + mediaPlayer.isPlaying());
                Log.d(LOG_TAG,"Time + " + mediaPlayer.getCurrentPosition() + " / " +
                    + mediaPlayer.getDuration());
                Log.d(LOG_TAG,"Looping " + mediaPlayer.isLooping());
                Log.d(LOG_TAG,"Volume " + audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
                break;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.d(LOG_TAG,"onPrepared");
        mediaPlayer.start();
    }

    public void onCompletion(MediaPlayer mp){
        Log.d(LOG_TAG,"onCompletion");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseMP();
    }
}
