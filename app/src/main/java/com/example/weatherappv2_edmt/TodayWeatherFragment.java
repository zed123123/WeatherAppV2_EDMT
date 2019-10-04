package com.example.weatherappv2_edmt;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weatherappv2_edmt.Common.Common;
import com.example.weatherappv2_edmt.Model.WeatherResult;
import com.example.weatherappv2_edmt.Retrofit.IOpenWeatherMap;
import com.example.weatherappv2_edmt.Retrofit.RetrofitClient;
import com.squareup.picasso.Picasso;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 */
public class TodayWeatherFragment extends Fragment {

    ImageView imgWeather;
    TextView txtCityName, txtHumidity, txtSunrise, txtSunset, txtPressure, txtTemperature, txtDesc, txtDateTime, txtWind, txtGeoCoords;
    LinearLayout weatherPanel;
    ProgressBar loading;

    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mService;

    static TodayWeatherFragment instance;

    public static TodayWeatherFragment getInstance(){
        if (instance == null){
            instance = new TodayWeatherFragment();
        }
        return instance;
    }

    public TodayWeatherFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView =  inflater.inflate(R.layout.fragment_today_weather, container, false);

        imgWeather = itemView.findViewById(R.id.imgWeather);
        txtCityName = itemView.findViewById(R.id.txtCityName);
        txtHumidity = itemView.findViewById(R.id.txtHumidity);
        txtSunrise = itemView.findViewById(R.id.txtSunrise);
        txtSunset = itemView.findViewById(R.id.txtSunset);
        txtPressure = itemView.findViewById(R.id.txtPressure);
        txtTemperature = itemView.findViewById(R.id.txtTemperature);
        txtDesc = itemView.findViewById(R.id.txtDesc);
        txtDateTime = itemView.findViewById(R.id.txtDateTime);
        txtWind = itemView.findViewById(R.id.txtWind);
        txtGeoCoords = itemView.findViewById(R.id.txtGeoCoords);

        weatherPanel = itemView.findViewById(R.id.weatherPanel);
        loading = itemView.findViewById(R.id.loading);

        getWeatherInfomation();

        return itemView;
    }

    private void getWeatherInfomation() {
        compositeDisposable.add(mService.getWeatherByLatLng(
                String.valueOf(Common.current_location.getLatitude()),
                String.valueOf(Common.current_location.getLongitude()),
                Common.APP_ID,
                "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherResult>() {
                    @Override
                    public void accept(WeatherResult weatherResult) throws Exception {

                        //Load image
                        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/wn/")
                                .append(weatherResult.getWeather().get(0).getIcon())
                                .append("@2x.png")
                                .toString())
                                .into(imgWeather);

                        //Load Info
                        txtCityName.setText(weatherResult.getName());
                        txtDesc.setText(new StringBuilder(String.valueOf(weatherResult.getWeather().get(0).getDescription())));
                        txtTemperature.setText(new StringBuffer(String.valueOf(weatherResult.getMain().getTemp()))
                                .append("°C")
                                .toString());
                        txtDateTime.setText(Common.convertUnixToDate(weatherResult.getDt()));
                        txtPressure.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getPressure()))
                                .append(" hpa")
                                .toString());
                        txtHumidity.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getHumidity()))
                                .append(" %")
                                .toString());
                        txtSunrise.setText(Common.convertUnixToHour(weatherResult.getSys().getSunrise()));
                        txtSunset.setText(Common.convertUnixToHour(weatherResult.getSys().getSunset()));
                        txtGeoCoords.setText(new StringBuilder(weatherResult.getCoord().toString())
                                .toString());

                        //Display panel
                        weatherPanel.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.GONE);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(getActivity(), "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
        );

    }

    //Clear Composite to stop fragment


    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }
}
