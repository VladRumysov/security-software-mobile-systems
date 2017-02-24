package com.example.vlad.lab_camera;


import java.io.File;
import java.lang.System;
import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings.*;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;

import static java.security.AccessController.getContext;

public class MainActivity extends Activity {

    File directory;
    final int TYPE_VIDEO = 2;
    final int REQUEST_CODE_VIDEO = 2;
    final String TAG = "myLogs";
    final int DURATION_LIMIT = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createDirectory();

    }


    public void onClickVideo(View view) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, generateFileUri(TYPE_VIDEO));
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, DURATION_LIMIT);
        startActivityForResult(intent, REQUEST_CODE_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {

        if (requestCode == REQUEST_CODE_VIDEO) {
            if (resultCode == RESULT_OK) {
                if (intent == null) {
                    Log.d(TAG, "Intent is null");
                } else {
                    Log.d(TAG, "Video uri: " + intent.getData());
                }
            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "Canceled");
            }
        }
    }

    private Uri generateFileUri(int type) {
        File file = null;

        if(type == TYPE_VIDEO) {
            file = new File(directory.getPath() + "/" + "RVS"
                    + "9a96576a"
                    + Calendar.YEAR + Calendar.MONTH + Calendar.DAY_OF_MONTH
                    + Calendar.HOUR + Calendar.MINUTE
                    + System.currentTimeMillis() + ".mp4");
        }

        Log.d(TAG, "fileName = " + file);
        return Uri.fromFile(file);
    }

    private void createDirectory() {
        directory = new File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyFolder");

        if (!directory.exists())
            directory.mkdirs();
    }

}