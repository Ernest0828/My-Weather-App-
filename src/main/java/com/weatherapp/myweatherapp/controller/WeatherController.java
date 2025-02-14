package com.weatherapp.myweatherapp.controller;

import com.weatherapp.myweatherapp.model.CityInfo;
import com.weatherapp.myweatherapp.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalTime;
import java.time.Duration;

@Controller
public class WeatherController {

  @Autowired
  WeatherService weatherService;

  @GetMapping("/forecast/{city}")
  public ResponseEntity<CityInfo> forecastByCity(@PathVariable("city") String city) {

    CityInfo ci = weatherService.forecastByCity(city);

    return ResponseEntity.ok(ci);
  }

  // TODO: given two city names, compare the length of the daylight hours and return the city with the longest day
  @GetMapping("/compare-daylight/{city1}/{city2}")
  public ResponseEntity<String> compareDaylight(@PathVariable("city1") String city1, @PathVariable("city2") String city2) {
    CityInfo ci1 = weatherService.forecastByCity(city1);
    CityInfo ci2 = weatherService.forecastByCity(city2);
    
    /*Obtain Sunrise and Sunset times */
    String sunriseStr1 = ci1.getCurrentConditions().getSunrise();
    String sunsetStr1 = ci1.getCurrentConditions().getSunset();
    String sunriseStr2 = ci2.getCurrentConditions().getSunrise();
    String sunsetStr2 = ci2.getCurrentConditions().getSunset();

    /*Convert to Local Time */
    LocalTime sunrise1 = LocalTime.parse(sunriseStr1);
    LocalTime sunset1 = LocalTime.parse(sunsetStr1);
    LocalTime sunrise2 = LocalTime.parse(sunriseStr2);
    LocalTime sunset2 = LocalTime.parse(sunsetStr2);

    /*Compare daylight hours */
    double daylight1 = Duration.between(sunrise1, sunset1).toMinutes() / 60.0; /*displays time difference in the form of minutes */
    double daylight2 = Duration.between(sunrise2, sunset2).toMinutes() / 60.0;

    /*Compare results */
    /*displays the city with more daylight hours and their daylight hours */
    String result;
    if (daylight1 > daylight2) {
      result = city1 + " has longer daylight hours (" + daylight1 + " hours) than " + city2 + " (" + daylight2 + " hours)."; 
    } else if (daylight2 > daylight1) {
      result = city2 + " has longer daylight hours (" + daylight2 + " hours) than " + city1 + " (" + daylight1 + " hours).";
    } else {
      result = "Both cities have the same amount of daylight";
    }

    return ResponseEntity.ok(result);
  }
  

  // TODO: given two city names, check which city its currently raining in
  @GetMapping("/compare-rain/{city1}/{city2}")
  public ResponseEntity<String> compareRain(@PathVariable("city1") String city1, @PathVariable("city2") String city2) {
    CityInfo ci1 = weatherService.forecastByCity(city1);
    CityInfo ci2 = weatherService.forecastByCity(city2);
    
    /*Extract precipitation values which indicate the amount of rain */
    double precipCity1 = parseDoubleSafe(ci1.getCurrentConditions().getPrecip());
    double precipCity2 = parseDoubleSafe(ci2.getCurrentConditions().getPrecip());
    
    /*Determine which city is raining */
    String result;
    if (precipCity1 > 0 && precipCity2 > 0) {
      result = "Both " + city1 + " and " + city2 + " are experiencing rain.";
    } else if (precipCity1 > 0) {
      result = city1;
    } else if (precipCity2 > 0) {
      result = city2;
    }else {
      result = "No city is currently raining";
    }

    return ResponseEntity.ok(result);
  }
  // Helper method to safely parse precipitation values
  private double parseDoubleSafe(String value) {
    if (value == null || value.trim().isEmpty()) {
      return 0.0; // Return 0.0 if value is null or empty
    }
    try {
        return Double.parseDouble(value);
    } catch (NumberFormatException e) {
        return 0.0; // Return 0.0 if the value is not a valid number
    }
}

}
