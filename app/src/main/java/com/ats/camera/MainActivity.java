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
    private capturestill cp;

    private Button btnCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        s = new SimpleDateFormat("ddMMyyyyhhmmss");
        cp = new capturestill();
        btnCapture = findViewById(R.id.btnCapture);
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String format = s.format(new Date());
                int result = cp.capture_still(Environment.getExternalStorageDirectory() + "/capture" ,"image_" + format + ".jpg" );
                Log.d("hahieuit",""+result);
            }
        });
    }
}

class capturestill {

    static {
        System.loadLibrary("capov7740");
    }
    /* make sure that the path where the file is to be captured is writtable
    also the filename must not be the filename already present
    the api returns 1 value which means file already present.
    make sure filename ends with .jpeg example /data/capture/1.jpeg
    */
    public static native int capture_still(String path, String filename);

    public capturestill()
    {
        Log.d("capturestill", "capturestill instance created");
    }
}
