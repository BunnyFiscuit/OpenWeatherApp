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
    private OWM weatherApp = new OWM(API_KEY);

    // We could use the phone's GPS to get the longitude and latitude.
    // but we'll just use this for now.
    private String city = "Gothenburg";
    private CurrentWeather currentWeather;
    private OWM.Unit unit = OWM.Unit.METRIC;

    private WeatherModel() {
        weatherApp.setUnit(unit);
        weatherApp.setLanguage(OWM.Language.ENGLISH);
    }

    // Singleton
    public static WeatherModel getInstance(){
        if (instance == null){
            instance = new WeatherModel();
        }
        return instance;
    }


    // Returns an ArrayList with the weather information about the chosen city.
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

    // Gets Forecast information from the API. Saves the temperature in and the hour into an
    // ArrayList which is then returned.
    @Override
    public ArrayList<String> getForecastInfo() throws APIException{
        HourlyWeatherForecast hourlyWeatherForecast = weatherApp.hourlyWeatherForecastByCityName(city);
        if (hourlyWeatherForecast.hasDataList()) {
            ArrayList<String> info = new ArrayList<>();
            // Loop through the next 3 forecasts to save the weather data in info
            for (int i = 0; i < 3; i++) {
                // Get the weather data and time for weather data
                WeatherData wd = Objects.requireNonNull(hourlyWeatherForecast.getDataList()).get(i);
                String time = hourlyWeatherForecast.getDataList().get(i).getDateTimeText();

                // Format the time to match how we want it
                try{
                    Date d = new SimpleDateFormat("yyyy-mm-dd h:mm:ss", Locale.ENGLISH).parse(time);
                    info.add(new SimpleDateFormat("h:mm a", Locale.ENGLISH).format(d.getTime()));
                } catch (ParseException e){
                    e.printStackTrace();
                }

                // Formatting the temperature
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

    // Returns the name of the city
    @Override
    public String getCityName() {
        if (currentWeather.hasMainData())
        return currentWeather.getCityName() + ", " +
                currentWeather.getSystemData().getCountryCode();
        else return "No city name found";
    }


    // Returns the city temperature
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


    // Returns the city humidity
    @Override
    public String getCityHumidity(){
        if(currentWeather.hasMainData()) {
            int humidity = currentWeather.getMainData().getHumidity();
            return String.format(Locale.ENGLISH, "%s%s", humidity,"%");
        }
        return null;
    }

    // Returns the wind speed in the city
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

    // Returns the weather description
    @Override
    public String getWeatherDescription() {
        if (currentWeather.hasWeatherList())
        return currentWeather.getWeatherList().get(0).getDescription();
        return "No weather description";
    }


    // Returns the id for the icon to use for the current weather.
    @Override
    public int getConditionId() {
        if (currentWeather.hasWeatherList()) {
            String code = currentWeather.getWeatherList().get(0).getIconCode();
            int id;
            switch(code){
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


    // Sets the new city for the API to get information from.
    @Override
    public void setCity(String name) {
        this.city = name;
    }

    // Changes unit from Metric to Imperial, and vice-versa.
    @Override
    public void changeUnit() {
        if (unit == OWM.Unit.METRIC)
            unit = OWM.Unit.IMPERIAL;
        else unit = OWM.Unit.METRIC;

        weatherApp.setUnit(unit);

    }
}
