package com.example.weatherappv2_edmt;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.weatherappv2_edmt.Adapter.WeatherForecastAdapter;
import com.example.weatherappv2_edmt.Common.Common;
import com.example.weatherappv2_edmt.Model.WeatherForecastResult;
import com.example.weatherappv2_edmt.Retrofit.IOpenWeatherMap;
import com.example.weatherappv2_edmt.Retrofit.RetrofitClient;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 */
public class ForecastFragment extends Fragment {

    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mService;

    TextView txtCity, txtGeoCoord;
    RecyclerView recyclerForecast;


    static ForecastFragment instance;

    public static ForecastFragment getInstance(){
        if(instance == null){
            instance = new ForecastFragment();
        }
        return instance;
    }


    public ForecastFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.fragment_forecast, container, false);

        txtCity = itemView.findViewById(R.id.txtCityName);
        txtGeoCoord = itemView.findViewById(R.id.txtGeoCoords);

        recyclerForecast = itemView.findViewById(R.id.recyclerForecast);
        recyclerForecast.setHasFixedSize(true);
        //
        recyclerForecast.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        getForecastWeatherInfomation();

        return itemView;
    }


    //Clear Composite to stop fragment
    // Ctr+O

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



    private void getForecastWeatherInfomation() {
        compositeDisposable.add(mService.getForecastWeatherByLatLng(
           String.valueOf(Common.current_location.getLatitude()),
            String.valueOf(Common.current_location.getLongitude()),
            Common.APP_ID,
            "metric")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<WeatherForecastResult>() {
                @Override
                public void accept(WeatherForecastResult weatherForecastResult) throws Exception {
                    displayForecastWeather(weatherForecastResult);
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    Log.d("ERROR", "" + throwable.getMessage());
                }
            })
        );
    }

    private void displayForecastWeather(WeatherForecastResult weatherForecastResult) {
        txtCity.setText(new StringBuilder(weatherForecastResult.city.name));
        txtGeoCoord.setText(new StringBuilder(weatherForecastResult.city.coord.toString()));

        WeatherForecastAdapter adapter = new WeatherForecastAdapter(getContext(), weatherForecastResult);
        recyclerForecast.setAdapter(adapter);
    }

}
