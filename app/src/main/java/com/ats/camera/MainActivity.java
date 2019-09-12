package com.ats.camera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private SimpleDateFormat s;
    private ov7740 cp;

    private Button btnCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        s = new SimpleDateFormat("ddMMyyyyhhmmss");
        cp = new ov7740();
        btnCapture = findViewById(R.id.btnCapture);
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnCapture.setEnabled(false);
                String format = s.format(new Date());
                int result = cp.captureImage(Environment.getExternalStorageDirectory() + "/capture" ,"image_" + format + ".jpg" );
                Log.d("hahieuit",""+result);
                btnCapture.setEnabled(true);
            }
        });
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
    public static native int captureImage(String path, String filename);

    public ov7740()
    {
        Log.d("capov7740", "capov7740 instance created");
    }
}
