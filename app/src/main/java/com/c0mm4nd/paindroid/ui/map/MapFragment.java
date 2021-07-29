package com.c0mm4nd.paindroid.ui.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.c0mm4nd.paindroid.databinding.FragmentMapBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapFragment extends Fragment {
    private int entryID;
    private FragmentMapBinding binding;
    private GoogleMap googleMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = com.c0mm4nd.paindroid.databinding.FragmentMapBinding.inflate(inflater, container, false);
        binding.mapView.onCreate(savedInstanceState);
        binding.mapView.onResume();

        binding.addressInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // when press enter
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Geocoder geocoder = new Geocoder(getContext());
                    List<Address> addresses;

                    try {
                        addresses = geocoder.getFromLocationName(binding.addressInput.getText().toString(), 5);
                        if (addresses == null || addresses.size() == 0) {
                            Toast.makeText(getContext(), "cannot get the location", Toast.LENGTH_LONG).show();
                            return false;
                        }

                        Address location = addresses.get(0);
                        location.getLatitude();
                        location.getLongitude();

                        LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                        Log.d("DELETEME", String.format(Locale.getDefault(), "%f %f", loc.latitude, loc.longitude));

                        gotoLocOnMap(loc);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });

        return binding.getRoot();
    }

    private void gotoLocOnMap(LatLng loc) {
        if (binding.mapView.getVisibility() == View.INVISIBLE) {
            binding.mapView.setVisibility(View.VISIBLE);
        }

        binding.mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap map) {
                googleMap = map;

                // For showing a move to my location button
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "no permission to run a map", Toast.LENGTH_LONG).show();
                    String[] reqPerms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                    requireActivity().requestPermissions(reqPerms, 0);
                    return;
                }

                map.setMyLocationEnabled(true);

                // For dropping a marker at a point on the Map
                map.addMarker(new MarkerOptions().position(loc).title("Home").snippet(String.format(Locale.getDefault(), "(%.2f, %.2f)", loc.latitude, loc.longitude)));

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(12).build();
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
