package com.example.beaconreceriver.app;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.radiusnetworks.ibeacon.IBeacon;
import com.radiusnetworks.ibeacon.IBeaconConsumer;
import com.radiusnetworks.ibeacon.IBeaconManager;
import com.radiusnetworks.ibeacon.RangeNotifier;
import com.radiusnetworks.ibeacon.Region;

import java.util.Collection;

public class MainActivity extends Activity implements IBeaconConsumer {
    private IBeaconManager mIBeaconManager = IBeaconManager.getInstanceForApplication(this);
    private RelativeLayout mBackgroundView;
    private TextView mMessage;
    private int mPresentColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBackgroundView = (RelativeLayout)findViewById(R.id.myView);
        mBackgroundView.setBackgroundColor(Color.GRAY);
        mMessage = (TextView)findViewById(R.id.message);
        mIBeaconManager.bind(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIBeaconManager.unBind(this);
    }

    @Override
    public void onIBeaconServiceConnect() {
        mIBeaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<IBeacon> iBeacons, Region region) {
                for (IBeacon beacon : iBeacons) {
                    //Log.e("beacon", beacon.getProximityUuid());
                    String uuid = beacon.getProximityUuid();
                    if ("772bae40-c984-4d8a-b4c8-2bd2f3a3e6cb".equals(uuid)) {
                        updateView(beacon);
                    }
                }
            }
        });

        try {
            mIBeaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {   }
    }

    private void updateView(IBeacon beacon) {
        int bColor;
        switch (beacon.getProximity()) {
            case IBeacon.PROXIMITY_IMMEDIATE:
                bColor = Color.RED;
                Log.e("color", "red");
                break;
            case IBeacon.PROXIMITY_NEAR:
                bColor = Color.GREEN;
                Log.e("color", "green");
                break;
            case IBeacon.PROXIMITY_FAR:
                bColor = Color.BLUE;
                Log.e("color", "blue");
                break;
            default:
                bColor = Color.WHITE;
                Log.e("color", "white");
                break;
        }
        if (mPresentColor==bColor) return;

        mPresentColor = bColor;
        runOnUiThread(new Runnable(){
            @Override
            public void run() {
                mBackgroundView.setBackgroundColor(mPresentColor);
            }
        });
    }
}
