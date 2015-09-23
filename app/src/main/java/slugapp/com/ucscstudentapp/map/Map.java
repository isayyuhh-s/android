package slugapp.com.ucscstudentapp.map;

//
//  Map.java
//  SlugRoute
//
//  Created by Karol Josef Bustamante. <karoljosefb@gmail.com>
//  Copyright (c) 2015 UCSC Android. All rights reserved.
//

import android.app.Activity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import slugapp.com.ucscstudentapp.R;
import slugapp.com.ucscstudentapp.main.ActivityReference;

public class Map extends SupportMapFragment implements DialogCallback {
    private MapEditor mapEditor;
    private ActivityReference mCallBack;
    private int toolbar_height;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallBack = (ActivityReference) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Map editor
        mapEditor = new MapEditor(getActivity(), this, mCallBack);
        toolbar_height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                (float) 48, getActivity().getResources().getDisplayMetrics());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        mCallBack.setTitle("Map");

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mCallBack.setButtons(R.id.map_button);

        // Set map
        mapEditor.initializeMap(getMap());
        mapEditor.setMarkers();
        mapEditor.setCustomMarkers();
        mapEditor.setListeners();
        Bundle b = getArguments();
        if (b != null && b.containsKey("name")) {
            switch (b.getString("name")) {
                case "College Eight / Oakes":
                    mapEditor.moveTo(new LatLng(36.991565, -122.065267), b.getString("name"));
                    break;
                case "Porter / Kresge":
                    mapEditor.moveTo(new LatLng(36.994344, -122.065800), b.getString("name"));
                    break;
                case "College Nine / College Ten":
                    mapEditor.moveTo(new LatLng(37.001096, -122.058031), b.getString("name"));
                    break;
                case "Crown / Merrill":
                    mapEditor.moveTo(new LatLng(36.999971, -122.054448), b.getString("name"));
                    break;
                case "Cowell / Stevenson":
                    mapEditor.moveTo(new LatLng(36.997157, -122.053150), b.getString("name"));
                    break;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapEditor != null) mapEditor.hideInfoWindows();
    }

    /**
     * Creates new Marker
     */
    @Override
    public void createMarker(String title, float lat, float lng) {
        mapEditor.createMarker(title, lat, lng);
    }
}