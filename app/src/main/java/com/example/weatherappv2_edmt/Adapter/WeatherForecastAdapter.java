package com.example.weatherappv2_edmt.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherappv2_edmt.Common.Common;
import com.example.weatherappv2_edmt.Model.WeatherForecastResult;
import com.example.weatherappv2_edmt.R;
import com.squareup.picasso.Picasso;

public class WeatherForecastAdapter extends RecyclerView.Adapter<WeatherForecastAdapter.MyViewHolder> {

    Context context;
    WeatherForecastResult weatherForecastResult;

    public WeatherForecastAdapter(Context context, WeatherForecastResult weatherForecastResult) {
        this.context = context;
        this.weatherForecastResult = weatherForecastResult;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_weather_forecast, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //Load icon
        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/wn/")
                .append(weatherForecastResult.list.get(position).weather.get(0).getIcon())
                .append("@2x.png")
                .toString())
                .into(holder.imgWeather);

        holder.txtDateTime.setText(new StringBuilder(Common.convertUnixToDate(weatherForecastResult.list.get(position).dt)));

        holder.txtDesc.setText(new StringBuilder(weatherForecastResult.list.get(position).weather.get(0).getDescription()));

        holder.txtTemperature.setText(new StringBuilder(String.valueOf(weatherForecastResult.list.get(position).main.getTemp())).append("°C"));
    }

    @Override
    public int getItemCount() {
        return weatherForecastResult.list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView txtDateTime, txtDesc, txtTemperature;
        ImageView imgWeather;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgWeather = itemView.findViewById(R.id.imgWeather);
            txtDateTime = itemView.findViewById(R.id.txtDate);
            txtDesc = itemView.findViewById(R.id.txtDesc);
            txtTemperature = itemView.findViewById(R.id.txtTemperature);
        }
    }
}
