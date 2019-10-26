package com.example.vshop.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vshop.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ThongtinFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private int mZoom;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //inflate the layout to this fragment
        return inflater.inflate(R.layout.fragment_thongtin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null){
            mZoom = savedInstanceState.getInt("zoom");
        } else {
            mZoom = 16;
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mMap);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {

        googleMap = map;
        int mapType = GoogleMap.MAP_TYPE_NORMAL;
        googleMap.setMapType(mapType);

        // Add a marker in Sydney and move the camera
        Double mLatitude = 10.762939;
        Double mLongitude = 106.682156;
        LatLng KHTN = new LatLng(mLatitude, mLongitude);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(KHTN).zoom(mZoom).build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        googleMap.moveCamera(cameraUpdate);
        googleMap.addMarker(new MarkerOptions().position(KHTN)
                .title("Đại học Khoa Học Tự Nhiên")
                .snippet("227 Nguyễn Văn Cừ Quận 5 Tp.HCM")
                .position(KHTN)
                .icon(BitmapDescriptorFactory.defaultMarker()));
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        CameraPosition position = googleMap.getCameraPosition();
        outState.putFloat("zoom", position.zoom);
    }
}

