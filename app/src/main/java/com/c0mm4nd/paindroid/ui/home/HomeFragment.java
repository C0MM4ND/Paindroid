package com.c0mm4nd.paindroid.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.c0mm4nd.paindroid.MainActivity;
import com.c0mm4nd.paindroid.databinding.FragmentHomeBinding;
import com.c0mm4nd.paindroid.model.weather.CurrentWeatherResponse;
import com.c0mm4nd.paindroid.model.weather.Weather;
import com.c0mm4nd.paindroid.retrofit.WeatherClient;
import com.c0mm4nd.paindroid.retrofit.WeatherInterface;
import com.c0mm4nd.paindroid.ui.entry.EntryFragment;
import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private final String APP_ID = "0ce3bdf69b103284228165374203c3dd";
    private FragmentHomeBinding binding;

    private WeatherInterface weatherInterface;
    private LatLng currentLoc;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = com.c0mm4nd.paindroid.databinding.FragmentHomeBinding.inflate(inflater, container, false);

        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

        weatherInterface = WeatherClient.getCurrentWeatherService();
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), "no permission to get the weather", Toast.LENGTH_LONG).show();
            String[] reqPerms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            requireActivity().requestPermissions(reqPerms, 0);
        } else {
            // got permission
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    LatLng newLoc = new LatLng(location.getLatitude(), location.getLongitude());

                    if (newLoc.equals(currentLoc)) {
                        return;
                    }
                    currentLoc = newLoc;
                    binding.weatherLocView.setText(String.format(Locale.getDefault(), "(%.2f, %.2f)", newLoc.latitude, newLoc.longitude));

                    weatherInterface.fetchCurrentWeather(currentLoc.latitude, currentLoc.longitude, APP_ID).enqueue(new Callback<CurrentWeatherResponse>() {
                        @Override
                        public void onResponse(@NotNull Call<CurrentWeatherResponse> call, @NotNull Response<CurrentWeatherResponse> response) {
                            if (response.isSuccessful()) {
                                Log.d("DELETEME", "got loc weather resp");
                                assert response.body() != null;
                                Weather main = response.body().main;

                                binding.tempView.setText(String.format(Locale.getDefault(), "%.2f K", main.getTemperature()));
                                binding.humidView.setText(String.format(Locale.getDefault(), "%d %%", main.getHumidity()));
                                binding.pressureView.setText(String.format(Locale.getDefault(), "%d hPa", main.getPressure()));
                                if (getActivity() != null) {
                                    saveWeatherToStorage(main.getTemperature(), main.getHumidity(), main.getPressure());
                                }
                            } else {
                                Log.e("DELETEME ", "Response failed");
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<CurrentWeatherResponse> call, @NotNull Throwable t) {
                            Toast.makeText(getContext(), "failed to get response: " + t.getMessage(), Toast.LENGTH_LONG).show();
                            Log.e("DELETEME", t.getMessage());
                        }
                    });
                }
            });
        }

        binding.updateWeatherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLoc == null) {
                    Toast.makeText(getContext(), "failed to get current location", Toast.LENGTH_LONG).show();
                    return;
                }

                weatherInterface.fetchCurrentWeather(currentLoc.latitude, currentLoc.longitude, APP_ID)
                        .enqueue(new Callback<CurrentWeatherResponse>() {
                            @Override
                            public void onResponse(@NotNull Call<CurrentWeatherResponse> call, @NotNull Response<CurrentWeatherResponse> response) {
                                if (response.isSuccessful()) {
                                    Log.d("DELETEME", "got loc weather resp");
                                    assert response.body() != null;
                                    Weather main = response.body().main;

                                    binding.tempView.setText(String.format(Locale.getDefault(), "%.2f K", main.getTemperature()));
                                    binding.humidView.setText(String.format(Locale.getDefault(), "%d %%", main.getHumidity()));
                                    binding.pressureView.setText(String.format(Locale.getDefault(), "%d hPa", main.getPressure()));

                                    if (getActivity() != null) {
                                        saveWeatherToStorage(main.getTemperature(), main.getHumidity(), main.getPressure());
                                    }
                                } else {
                                    Log.e("DELETEME ", "Response failed");
                                }
                            }

                            @Override
                            public void onFailure(@NotNull Call<CurrentWeatherResponse> call, @NotNull Throwable t) {
                                Toast.makeText(getContext(), "failed to get response: " + t.getMessage(), Toast.LENGTH_LONG).show();
                                Log.e("DELETEME", t.getMessage());
                            }
                        });
            }
        });

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) requireActivity()).popupFragment(new EntryFragment(0));
            }
        });

        return binding.getRoot();
    }

    private void saveWeatherToStorage(double temp, int humid, int pressure) {
        SharedPreferences sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat("weather_temp", Double.valueOf(temp).floatValue());
        editor.putInt("weather_humid", humid);
        editor.putInt("weather_pressure", pressure);
        editor.apply();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
