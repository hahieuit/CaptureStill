package com.ats.camera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE = 1234;
    private boolean mPermissions;

    private SimpleDateFormat s;
    private ov7740 cp;

    private Button btnCapture;
    private TextView txtNotify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init(){
        try {
            Runtime.getRuntime().exec(new String[]{"su", "-c", "/system/bin/sh -c \"chmod 666 /dev/video0\""});
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(!mPermissions){
            verifyPermissions();
        }else{
            s = new SimpleDateFormat("ddMMyyyyhhmmss");
            cp = new ov7740();
            btnCapture = findViewById(R.id.btnCapture);
            txtNotify = findViewById(R.id.txtNotify);
            btnCapture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnCapture.setEnabled(false);
                    String format = s.format(new Date());
                    File f = new File(Environment.getExternalStorageDirectory() + "/capture");
                    if (!f.exists()) {
                        f.mkdirs();
                    }
                    int result = cp.captureImage(Environment.getExternalStorageDirectory() + "/capture" ,"image_" + format + ".jpg" );
                    Log.d("hahieuit",""+Environment.getExternalStorageDirectory() + "/capture");
                    if(result == 0){
                        ringtone();
                        txtNotify.setText(getString(R.string.capture_success));
                        txtNotify.setVisibility(View.VISIBLE);

                    }else if(result == 1){
                        txtNotify.setText(getString(R.string.exited));
                        txtNotify.setVisibility(View.VISIBLE);
                    }else{
                        txtNotify.setText(getString(R.string.capture_fail));
                        txtNotify.setVisibility(View.VISIBLE);
                    }
                    btnCapture.setEnabled(true);
                }
            });
        }
    }

    public void ringtone(){
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void verifyPermissions(){
        Log.d(TAG, "verifyPermissions: asking user for permissions.");
        String[] permissions = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[0] ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[1] ) == PackageManager.PERMISSION_GRANTED) {
            mPermissions = true;
            init();
        } else {
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    permissions,
                    REQUEST_CODE
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                init();
            }else{
                finish();
            }
        }
    }
}

class ov7740{

    static {
        System.loadLibrary("capov7740");
    }
    /* make sure that the path where the file is to be captured is writtable
    also the filename must not be the filename already present
    the api returns 1 value which means file already present.
    make sure filename ends with .jpeg example /data/capture/1.jpeg
    */
    public native int captureImage(String path, String filename);

    public ov7740()
    {
        Log.d("capov7740", "capov7740 instance created");
    }
}
