package com.example.sarosh.openweatherapp;


import android.location.Location;
import net.aksingh.owmjapis.api.APIException;
import net.aksingh.owmjapis.core.OWM;
import net.aksingh.owmjapis.model.CurrentWeather;

import java.util.ArrayList;
import java.util.Locale;

public class WeatherModel implements IWeather {
    private static final String TAG = WeatherModel.class.toString();

    private static WeatherModel instance = null;

    private final String API_KEY = "ddbb487d7785b2dc6f9069b418732ee6";
    protected OWM weatherApp = new OWM(API_KEY);
    private Location loc = new Location("Gothenburg");
    private String city = "Gothenburg";
    private CurrentWeather currentWeather;
    private OWM.Unit unit = OWM.Unit.METRIC;

    public WeatherModel() {
        weatherApp.setUnit(unit);
        weatherApp.setLanguage(OWM.Language.ENGLISH);

        // We could use the phone's GPS to get the longitude and latitude.
        // but we'll just use Gothenburg's longitude and latitude for now.
        loc.setLatitude(57.71);
        loc.setLongitude(11.97);
    }

    public static WeatherModel getInstance(){
        if (instance == null){
            instance = new WeatherModel();
        }
        return instance;
    }

    @Override
    public ArrayList<String> getWeatherInfo() throws APIException, android.os.NetworkOnMainThreadException {
        ArrayList<String> info = new ArrayList<>();
        currentWeather = weatherApp.currentWeatherByCityName(city);

        info.add(getCityName());
        info.add(getCityTemp());
        info.add(getCityHumidity());
        info.add(getCityWindSpeed());

        return info;
    }

    @Override
    public String getCityName() throws APIException {
        if (currentWeather.hasMainData())
        return currentWeather.getCityName();
        else return "No city name found";
    }

    @Override
    public String getCityTemp() throws APIException {
        if (currentWeather.hasMainData()) {
            double temp = currentWeather.getMainData().getTemp();
            String deg;
            if (unit == OWM.Unit.METRIC)
                deg = "°C";
            else deg = "°F";
            return String.format(Locale.ENGLISH, "%.0f %s", temp, deg);
        }
        else return "No temperature found";
    }

    @Override
    public String getCityHumidity() throws APIException {
        if(currentWeather.hasMainData()) {
            int humidity = currentWeather.getMainData().getHumidity();
            return String.format(Locale.ENGLISH, "%s%s ", humidity,"%");
        }
        return null;
    }

    @Override
    public String getCityWindSpeed() throws APIException {
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
    public void setCity(String name) throws APIException {
        this.city = name;
    }

    @Override
    public void changeUnit() throws APIException {
        if (unit == OWM.Unit.METRIC)
            unit = OWM.Unit.IMPERIAL;
        else unit = OWM.Unit.METRIC;

        weatherApp.setUnit(unit);

    }
}
