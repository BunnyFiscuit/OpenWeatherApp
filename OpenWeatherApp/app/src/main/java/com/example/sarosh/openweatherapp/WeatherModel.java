package com.example.sarosh.openweatherapp;


import net.aksingh.owmjapis.api.APIException;
import net.aksingh.owmjapis.core.OWM;
import net.aksingh.owmjapis.model.CurrentWeather;
import net.aksingh.owmjapis.model.HourlyWeatherForecast;
import net.aksingh.owmjapis.model.param.WeatherData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class WeatherModel implements IWeather {
    private static final String TAG = WeatherModel.class.toString();

    private static WeatherModel instance = null;

    private final String API_KEY = "ddbb487d7785b2dc6f9069b418732ee6";
    protected OWM weatherApp = new OWM(API_KEY);

    // We could use the phone's GPS to get the longitude and latitude.
    // but we'll just use this for now.
    private String city = "Gothenburg";
    private CurrentWeather currentWeather;
    private OWM.Unit unit = OWM.Unit.METRIC;

    public WeatherModel() {
        weatherApp.setUnit(unit);
        weatherApp.setLanguage(OWM.Language.ENGLISH);
    }

    public static WeatherModel getInstance(){
        if (instance == null){
            instance = new WeatherModel();
        }
        return instance;
    }

    @Override
    public ArrayList<String> getWeatherInfo() throws APIException {
        ArrayList<String> info = new ArrayList<>();
        currentWeather = weatherApp.currentWeatherByCityName(city);

        info.add(getCityName());
        info.add(getCityTemp());
        info.add(getCityHumidity());
        info.add(getCityWindSpeed());
        info.add(getWeatherDescription());
        info.add(String.valueOf(getConditionId()));

        return info;
    }

    public ArrayList<String> getForecastInfo() throws APIException{
        HourlyWeatherForecast hourlyWeatherForecast = weatherApp.hourlyWeatherForecastByCityName(city);
        if (hourlyWeatherForecast.hasDataList()) {
            ArrayList<String> info = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                WeatherData wd = Objects.requireNonNull(hourlyWeatherForecast.getDataList()).get(i);

                String time = hourlyWeatherForecast.getDataList().get(i).getDateTimeText();
                try{
                    Date d = new SimpleDateFormat("yyyy-mm-dd h:mm:ss", Locale.ENGLISH).parse(time);

                    info.add(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(d.getTime()));
                } catch (ParseException e){
                    e.printStackTrace();
                }



                double temp = wd.getMainData().getTemp();
                String deg;
                if (unit == OWM.Unit.METRIC)
                    deg = "°C";
                else deg = "°F";
                info.add(String.format(Locale.ENGLISH, "%.0f%s", temp, deg));
            }

            return info;
        }
        return null;
    }


    @Override
    public String getCityName() {
        if (currentWeather.hasMainData())
        return currentWeather.getCityName();
        else return "No city name found";
    }

    @Override
    public String getCityTemp() {
        if (currentWeather.hasMainData()) {
            double temp = currentWeather.getMainData().getTemp();
            String deg;
            if (unit == OWM.Unit.METRIC)
                deg = "°C";
            else deg = "°F";
            return String.format(Locale.ENGLISH, "%.0f%s", temp, deg);
        }
        else return "No temperature found";
    }

    @Override
    public String getCityHumidity(){
        if(currentWeather.hasMainData()) {
            int humidity = currentWeather.getMainData().getHumidity();
            return String.format(Locale.ENGLISH, "%s%s", humidity,"%");
        }
        return null;
    }

    @Override
    public String getCityWindSpeed() {
        if(currentWeather.hasMainData()){
            double speed = currentWeather.getWindData().getSpeed();
            String ut;
            if (unit == OWM.Unit.METRIC)
                ut = "meter/sec";
            else ut = "miles/sec";
            return String.format(Locale.ENGLISH, "%s %s", speed, ut);

        }
        return null;
    }

    @Override
    public String getWeatherDescription() {
        if (currentWeather.hasWeatherList())
        return currentWeather.getWeatherList().get(0).getDescription();
        return "No weather description";
    }

    @Override
    public int getConditionId() {
        if (currentWeather.hasWeatherList()) {
            int id;
            switch(currentWeather.getWeatherList().get(0).getIconCode()){
                case "01d": id = R.drawable.sunny;
                    break;
                case "01n": id = R.drawable.night_clear;
                    break;
                case "02d": id = R.drawable.cloudy;
                    break;
                case "02n": id = R.drawable.night_cloudy;
                    break;
                case "03d": id = R.drawable.cloudy_2;
                    break;
                case "03n": id = R.drawable.night_cloudy_2;
                    break;
                case "04d": id = R.drawable.cloudy_3;
                    break;
                case "04n": id = R.drawable.night_cloudy_3;
                    break;
                case "09d": id = R.drawable.rainy_2;
                    break;
                case "09n": id = R.drawable.night_rainy_2;
                    break;
                case "10d": id = R.drawable.rainy;
                    break;
                case "10n": id = R.drawable.night_rainy;
                    break;
                case "11d": id = R.drawable.stormy;
                    break;
                case "11n": id = R.drawable.night_stormy;
                    break;
                default: id = R.drawable.current_icon;
                    break;
            }
            return id;
        }


        else return R.drawable.current_icon;
    }

    @Override
    public void setCity(String name) {
        this.city = name;
    }

    @Override
    public void changeUnit() {
        if (unit == OWM.Unit.METRIC)
            unit = OWM.Unit.IMPERIAL;
        else unit = OWM.Unit.METRIC;

        weatherApp.setUnit(unit);

    }
}
