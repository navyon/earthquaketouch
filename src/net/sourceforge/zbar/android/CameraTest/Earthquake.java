package net.sourceforge.zbar.android.CameraTest;


import android.app.Activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.immersion.uhl.Launcher;


public class Earthquake extends FragmentActivity {
    /** Called when the activity is first created. */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake);

    }
    @Override
    public void onPause() {
        super.onPause();

    }

}