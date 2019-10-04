package com.example.weatherappv2_edmt;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weatherappv2_edmt.Common.Common;
import com.example.weatherappv2_edmt.Model.Weather;
import com.example.weatherappv2_edmt.Model.WeatherResult;
import com.example.weatherappv2_edmt.Retrofit.IOpenWeatherMap;
import com.example.weatherappv2_edmt.Retrofit.RetrofitClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.label305.asynctask.SimpleAsyncTask;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 */
public class CityFragment extends Fragment {


    private List<String> lstCities;
    private MaterialSearchBar searchBar;
    ImageView imgWeather;
    TextView txtCityName, txtHumidity, txtSunrise, txtSunset, txtPressure, txtTemperature, txtDesc, txtDateTime, txtWind, txtGeoCoords;
    LinearLayout weatherPanel;
    ProgressBar loading;

    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mService;

    Weather weather;



    static CityFragment instance;

    public static CityFragment getInstance(){
        if (instance == null){
            instance = new CityFragment();
        }
        return instance;
    }


    public CityFragment() {
        // Required empty public constructor
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_city, container, false);

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

        searchBar = itemView.findViewById(R.id.searchBar);
        searchBar.setEnabled(false);

        weather = new Weather();

        //AsyncTask class to load Cities List
        new LoadCities().execute();

        return itemView;
    }

    private class LoadCities extends SimpleAsyncTask<List<String>> {

        @Override
        protected List<String> doInBackgroundSimple() {
            lstCities = new ArrayList<>();
            try {
                StringBuilder builder = new StringBuilder();
                InputStream is = getResources().openRawResource(R.raw.city_list);
                GZIPInputStream gzipInputStream = new GZIPInputStream(is);

                InputStreamReader reader = new InputStreamReader(gzipInputStream);
                BufferedReader in = new BufferedReader(reader);

                String read;
                while((read = in.readLine()) != null){
                    builder.append(read);
                }

                lstCities = new Gson().fromJson(builder.toString(), new TypeToken<List<String>>(){}.getType());

            } catch (IOException e) {
                e.printStackTrace();
            }

            return lstCities; // size = 209579 //size_new = 2731
        }


        @Override
        protected void onSuccess(final List<String> listCity) {
            super.onSuccess(listCity);

            searchBar.setEnabled(true);
            searchBar.addTextChangeListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    List<String> suggest = new ArrayList<>();
                    for(String search: listCity){
                        if (search.toLowerCase().contains(searchBar.getText().toLowerCase())){
                            suggest.add(search);
                        }
                    }
                    searchBar.setLastSuggestions(suggest);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                @Override
                public void onSearchStateChanged(boolean enabled) {

                }

                @Override
                public void onSearchConfirmed(CharSequence text) {
                    getWeatherInfomation(text.toString());

                    searchBar.setLastSuggestions(listCity);
                }

                @Override
                public void onButtonClicked(int buttonCode) {

                }
            });

            searchBar.setLastSuggestions(listCity);

            loading.setVisibility(View.GONE);
        }
    }

    private void getWeatherInfomation(String cityName) {
        compositeDisposable.add(mService.getWeatherByCityName(
                cityName,
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
//                        Toast.makeText(getActivity(), "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(getActivity(), "City not found", Toast.LENGTH_SHORT).show();
                    }
                })
        );
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public void onStop() {
//        compositeDisposable.clear();

        super.onStop();
    }
}
