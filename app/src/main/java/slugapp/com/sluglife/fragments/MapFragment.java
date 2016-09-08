package slugapp.com.sluglife.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import slugapp.com.sluglife.R;
import slugapp.com.sluglife.enums.FragmentEnum;
import slugapp.com.sluglife.enums.MarkerEnum;
import slugapp.com.sluglife.enums.MarkerTypeEnum;
import slugapp.com.sluglife.http.DiningHallHttpRequest;
import slugapp.com.sluglife.http.DiningListHttpRequest;
import slugapp.com.sluglife.interfaces.HttpCallback;
import slugapp.com.sluglife.models.DiningHallObject;
import slugapp.com.sluglife.models.FacilityObject;
import slugapp.com.sluglife.models.LoopObject;
import slugapp.com.sluglife.runnables.LoopRunnable;

/**
 * Created by Karol Josef Bustamante. <karoljosefb@gmail.com>
 * Edited by isaiah on 8/8/2015
 * <p/>
 * This file contains a google map fragment that displays a google map using the google map api.
 */
public class MapFragment extends BaseMapFragment {
    private static final FragmentEnum FRAGMENT = FragmentEnum.MAP;
    private static final MarkerEnum[] sMarkerEnums = MarkerEnum.values();
    private static final String EMPTY_STRING = "";

    private static final long MAP_DELAY = 0;
    private static final int MAP_PERIOD = 2000;
    private static final float SCHOOL_RADIUS = 1672.2233f;
    private static final float DEFAULT_ZOOM = 14.5f;
    private static final float LOCATION_ZOOM = 15.0f;

    private static final int LOOP_MASK = 0b000001;
    private static final int DINING_HALL_MASK = 0b000100;
    private static final int LIBRARY_MASK = 0b001000;
    public static final int DEFAULT_MASK = 0b001001;

    private LoopRunnable loopRunnable;
    private List<FacilityObject> staticMarkers;
    private List<LoopObject> dynamicMarkers;

    /**
     * Gets a new instance of fragment
     *
     * @return New instance of fragment
     */
    public static MapFragment newInstance() {
        return new MapFragment();
    }

    /**
     * Fragment's onCreateView method
     *
     * @param inflater           Layout inflater
     * @param container          Container of fragment
     * @param savedInstanceState Saved instance state
     * @return Inflated view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);

        this.setMapFragment(FRAGMENT);

        return view;
    }

    /**
     * Fragment's onResume method
     */
    @Override
    public void onResume() {
        super.onResume();
        if (this.loopRunnable != null) {
            this.mCallback.scheduleTimer(this.loopRunnable, MAP_DELAY, MAP_PERIOD);
            this.loopRunnable.start();
        }
    }

    /**
     * Sets fields
     */
    @Override
    protected void setFields() {
        this.staticMarkers = new ArrayList<>();
        this.dynamicMarkers = new ArrayList<>();
    }

    /**
     * Sets google map markers
     *
     * @param googleMap Google map
     */
    @Override
    protected void setMarkers(GoogleMap googleMap) {
        int bin = this.getSharedPrefInt(this.mContext.getString(R.string.bundle_markers), DEFAULT_MASK);

        if ((bin & LOOP_MASK) != 0) this.setLoopBusMarkers(googleMap);
        if ((bin & DINING_HALL_MASK) != 0) this.setDiningHallMarkers(googleMap);
        if ((bin & LIBRARY_MASK) != 0) this.setLibraryMarkers(googleMap);
    }

    /**
     * Sets google map listeners
     *
     * @param googleMap Google map
     */
    @Override
    protected void setMapListeners(final GoogleMap googleMap) {
        // OnMarkerClickListener
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                return false;
            }
        });

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                onClickStaticInfoWindow(marker);
            }
        });
    }

    /**
     * Sets initial google map zoom
     *
     * @param googleMap Google map
     */
    @Override
    @SuppressWarnings({"MissingPermission"})
    protected void setInitialZoom(GoogleMap googleMap) {
        float lat = Float.valueOf(this.mContext.getString(R.string.map_init_lat));
        float lng = Float.valueOf(this.mContext.getString(R.string.map_init_lng));

        LatLng initLatLng = new LatLng(lat, lng);

        googleMap.getUiSettings().setZoomControlsEnabled(true);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initLatLng, DEFAULT_ZOOM));

        if (!this.isGPSEnabled()) return;

        if (this.isLocationPermitted()) googleMap.setMyLocationEnabled(true);
        else this.requestLocationPermissions();
    }

    /**
     * Clears google map data
     */
    @Override
    protected void clearMapData() {
        for (LoopObject loop : this.dynamicMarkers) {
            if (loop.marker != null) {
                loop.marker.remove();
                loop.marker = null;
            }
        }
        this.mCallback.cancelTimer();
        if (this.loopRunnable != null) this.loopRunnable.stop();
    }

    /**
     * Sets loop bus markers on google map
     *
     * @param googleMap Google map
     */
    private void setLoopBusMarkers(final GoogleMap googleMap) {
        this.dynamicMarkers = new ArrayList<>();

        this.loopRunnable = new LoopRunnable(this.mContext, googleMap,
                this.dynamicMarkers);

        this.mCallback.scheduleTimer(this.loopRunnable, MAP_DELAY, MAP_PERIOD);
    }

    /**
     * Sets dining hall markers on google map
     *
     * @param googleMap Google map
     */
    private void setDiningHallMarkers(final GoogleMap googleMap) {
        new DiningListHttpRequest(this.mContext).execute(new HttpCallback<List<String>>() {

            /**
             * On request success
             *
             * @param values List of values from request
             */
            @Override
            public void onSuccess(List<String> values) {
                for (String diningHallName : values) {
                    new DiningHallHttpRequest(mContext, diningHallName).execute(
                            new HttpCallback<DiningHallObject>() {

                                /**
                                 * On request success
                                 *
                                 * @param val Dining hall object from request
                                 */
                                @Override
                                public void onSuccess(DiningHallObject val) {
                                    val.marker = googleMap.addMarker(new MarkerOptions()
                                            .title(val.name + mContext.getString(
                                                    R.string.detail_map_dining_ending))
                                            .snippet(mContext.getString(R.string.map_dining_snippet))
                                            .position(val.latLng)
                                            .icon(BitmapDescriptorFactory.fromResource(
                                                    DiningHallObject.diningImage)));
                                    staticMarkers.add(val);
                                }

                                /**
                                 * On request error
                                 *
                                 * @param e Exception
                                 */
                                @Override
                                public void onError(Exception e) {
                                    e.printStackTrace();
                                }
                            });
                }
            }

            /**
             * On request error
             *
             * @param e Exception
             */
            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Sets library markers on google map
     *
     * @param googleMap Google map
     */
    private void setLibraryMarkers(final GoogleMap googleMap) {
        for (MarkerEnum currEnum : sMarkerEnums) {
            double lat = Double.valueOf(this.mContext.getString(currEnum.lat));
            double lng = Double.valueOf(this.mContext.getString(currEnum.lng));

            String title = this.mContext.getString(currEnum.title);
            String snippet = this.mContext.getString(currEnum.snippet);
            LatLng latLng = new LatLng(lat, lng);
            if (currEnum.type != MarkerTypeEnum.LIBRARY) continue;
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(currEnum.icon);

            this.staticMarkers.add(new FacilityObject(MarkerTypeEnum.LIBRARY,
                    googleMap.addMarker(new MarkerOptions()
                            .title(title)
                            .snippet(snippet)
                            .position(latLng)
                            .icon(bitmap))));
        }
    }

    /**
     * Does action on google map marker info window click
     *
     * @param marker Google map marker
     */
    private void onClickStaticInfoWindow(Marker marker) {
        for (FacilityObject facility : staticMarkers) {
            if (!(facility.marker).getTitle().equals(marker.getTitle())) continue;
            if (facility.isType(MarkerTypeEnum.DINING_HALL)) {
                this.mCallback.setFragment(DiningViewPagerFragment.newInstance(this.mContext,
                        marker.getTitle().replace(this.mContext.getString(R.string.detail_map_dining_ending), EMPTY_STRING)));
            } else if (facility.isType(MarkerTypeEnum.LIBRARY)) {
                this.mCallback.setFragment(MapFacilityViewFragment.newInstance(this.mContext,
                        marker.getTitle()));
            }
        }
    }
}