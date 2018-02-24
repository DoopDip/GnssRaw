package th.ac.kmutnb.cs.gnssraw;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.GnssMeasurement;
import android.location.GnssMeasurementsEvent;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private static final String TAG = "ListActivity";

    private RecyclerView recyclerView;
    private ListAdapter listAdapter;

    private TextView textViewTotalSatellite;

    private Handler handler;
    private List<GnssMeasurement> measurementList;
    private LocationManager locationManager;
    private GnssMeasurementsEvent.Callback measurementsEvent = new GnssMeasurementsEvent.Callback() {
        @Override
        public void onGnssMeasurementsReceived(GnssMeasurementsEvent eventArgs) {
            super.onGnssMeasurementsReceived(eventArgs);
            measurementList.clear();
            measurementList.addAll(eventArgs.getMeasurements());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    textViewTotalSatellite.setText(String.valueOf(measurementList.size()));
                    listAdapter.updateMeasurementList(measurementList);
                }
            });
            Log.i(TAG, "GnssMeasurementsEvent callback -> Satellite total : " + measurementList.size());
        }

        @Override
        public void onStatusChanged(int status) {
            super.onStatusChanged(status);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Log.i(TAG, "onCreate");

        measurementList = new ArrayList<>();
        handler = new Handler();

        listAdapter = new ListAdapter(measurementList);

        textViewTotalSatellite = (TextView) findViewById(R.id.list_totalSatellite);

        recyclerView = (RecyclerView) findViewById(R.id.list_recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(listAdapter);

        locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Check Permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;
        locationManager.registerGnssMeasurementsCallback(measurementsEvent);
        Log.i(TAG, "Register callback -> measurementsEvent");
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.unregisterGnssMeasurementsCallback(measurementsEvent);
        Log.i(TAG, "!! UnRegister callback -> measurementsEvent");
    }
}