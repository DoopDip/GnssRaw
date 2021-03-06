package th.ac.kmutnb.cs.gnssrecord.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.location.GnssMeasurement;
import android.location.GnssStatus;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import th.ac.kmutnb.cs.gnssrecord.DetailActivity;
import th.ac.kmutnb.cs.gnssrecord.R;
import th.ac.kmutnb.cs.gnssrecord.config.Constants;

/**
 * Created by DoopDip on 18/2/2018 AD.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListHolder> {

    private static final String TAG = ListAdapter.class.getSimpleName();

    private List<GnssMeasurement> measurementList;

    public ListAdapter(List<GnssMeasurement> measurementList) {
        this.measurementList = measurementList;
        Log.i(TAG, "Set List -> GnssMeasurement");
    }

    @Override
    public ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ListHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_list, parent, false));
    }

    @Override
    public void onBindViewHolder(final ListHolder holder, int position) {
        GnssMeasurement measurement = measurementList.get(position);
        int logoSatellite = logoSatelliteImageResource(measurement.getConstellationType());
        String nameSatellite = satelliteName(measurement.getConstellationType());

        holder.imageViewLogo.setImageResource(logoSatellite);
        holder.textViewSvId.setText(String.valueOf(measurement.getSvid()));
        holder.textViewSatelliteName.setText(nameSatellite);
        measurement.hasSnrInDb();
        holder.linearLayout.setBackgroundResource(borderColor(measurement.getCn0DbHz()));
        holder.linearLayout.setOnClickListener(v -> {
            Pair[] pairs = new Pair[]{
                    new Pair<View, String>(holder.imageViewLogo, "list_logoTransition"),
                    new Pair<View, String>(holder.textViewSvId, "list_SvidTransition"),
                    new Pair<View, String>(holder.textViewSatelliteName, "list_SatelliteNameTransition")
            };

            ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(
                    (Activity) v.getContext(), pairs
            );

            Intent intent = new Intent(v.getContext(), DetailActivity.class);
            intent.putExtra("measurement", measurement);
            intent.putExtra("logoSatellite", logoSatellite);
            intent.putExtra("nameSatellite", nameSatellite);
            v.getContext().startActivity(intent, activityOptions.toBundle());
            Log.i(TAG, "Click -> DetailActivity by Svid = " + measurement.getSvid());
        });
    }

    @Override
    public int getItemCount() {
        return measurementList.size();
    }

    static class ListHolder extends RecyclerView.ViewHolder {

        LinearLayout linearLayout;
        ImageView imageViewLogo;
        TextView textViewSvId;
        TextView textViewSatelliteName;

        ListHolder(View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.adapter_list);
            imageViewLogo = itemView.findViewById(R.id.adapter_list_logo);
            textViewSvId = itemView.findViewById(R.id.adapter_list_svId);
            textViewSatelliteName = itemView.findViewById(R.id.adapter_list_satelliteName);
        }
    }

    public void updateGnssMeasurementList(List<GnssMeasurement> measurementListOld, List<GnssMeasurement> measurementListNew) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new SatelliteDiffCallback(measurementListOld, measurementListNew));
        diffResult.dispatchUpdatesTo(this);
    }

    private String satelliteName(int svId) {
        if (svId == GnssStatus.CONSTELLATION_GPS)
            return "GPS";
        else if (svId == GnssStatus.CONSTELLATION_SBAS)
            return "SBAS";
        else if (svId == GnssStatus.CONSTELLATION_GLONASS)
            return "GLONASS";
        else if (svId == GnssStatus.CONSTELLATION_QZSS)
            return "QZSS";
        else if (svId == GnssStatus.CONSTELLATION_BEIDOU)
            return "BEIDOU";
        else if (svId == GnssStatus.CONSTELLATION_GALILEO)
            return "GALILEO";
        else
            return "UNKNOW";
    }

    private int logoSatelliteImageResource(int constellationType) {
        int logoImageResource = R.drawable.logo_unknow;
        if (constellationType == GnssStatus.CONSTELLATION_GPS)
            logoImageResource = R.drawable.logo_gps;
        else if (constellationType == GnssStatus.CONSTELLATION_SBAS)
            logoImageResource = R.drawable.logo_sbas;
        else if (constellationType == GnssStatus.CONSTELLATION_GLONASS)
            logoImageResource = R.drawable.logo_glonass;
        else if (constellationType == GnssStatus.CONSTELLATION_QZSS)
            logoImageResource = R.drawable.logo_qzss;
        else if (constellationType == GnssStatus.CONSTELLATION_BEIDOU)
            logoImageResource = R.drawable.logo_beidou;
        else if (constellationType == GnssStatus.CONSTELLATION_GALILEO)
            logoImageResource = R.drawable.logo_galileo;
        return logoImageResource;
    }

    private int borderColor(double signal) {
        if (signal > Constants.STATUS_SATELLITE_GREEN)
            return R.drawable.bg_list_satellite_green;
        else if (signal > Constants.STATUS_SATELLITE_YELLOW)
            return R.drawable.bg_list_satellite_yellow;
        else
            return R.drawable.bg_list_satellite_red;
    }
}
