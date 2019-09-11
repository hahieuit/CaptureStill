package com.example.capturestill;

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
    private static final int REQUEST_CODE = 1234;
    //vars
    private boolean mPermissions;

    private Button btnCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

    }

    private void init(){
        SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
        if(!mPermissions){
            verifyPermissions();
        }else{
            // Example of a call to a native method
            btnCapture = findViewById(R.id.btnCapture);
            btnCapture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String format = s.format(new Date());
                    int result = capture_still(Environment.getExternalStorageDirectory() + "/hatest" ,"image_" + format + ".jpg" );
                    Log.d("hahieuit",""+result);
                }
            });

        }
    }

    public void verifyPermissions(){
        Log.d(TAG, "verifyPermissions: asking user for permissions.");
        String[] permissions = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
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

    static {
        // The runtime will add "lib" on the front and ".o" on the end of
        // the name supplied to loadLibrary.
        System.loadLibrary("capture_still");
    }
    static native int add(int a, int b);
    static native int capture_still(String path,String filename);

}
