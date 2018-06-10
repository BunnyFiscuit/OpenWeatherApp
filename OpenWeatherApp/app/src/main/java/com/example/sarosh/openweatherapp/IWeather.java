package com.example.sarosh.openweatherapp;

import net.aksingh.owmjapis.api.APIException;

import java.util.ArrayList;

public interface IWeather {

    String getCityName() throws APIException;
    String getCityTemp() throws APIException;
    String getCityHumidity() throws APIException;
    String getCityWindSpeed() throws APIException;


    void setCity(String name) throws APIException;
    void changeUnit() throws APIException;
    ArrayList<String> getWeatherInfo() throws APIException;

}
