package com.weatherapp.myweatherapp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.weatherapp.myweatherapp.model.CityInfo;
import com.weatherapp.myweatherapp.repository.VisualcrossingRepository;

class WeatherServiceTest {

  // TODO: 12/05/2023 write unit tests
  @Mock
    private VisualcrossingRepository mockRepository; // Mocked API data source

    @InjectMocks
    private WeatherService weatherService; // Service under test

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize Mockito
    }

    /**
     * Test compareDaylight method by simulating two cities with different daylight hours.
     */
    @Test
    void testCompareDaylight() {
        // Arrange: Mock CityInfo responses
        CityInfo city1 = createMockCityInfo("London", "07:00:00", "19:00:00"); // 12 hours
        CityInfo city2 = createMockCityInfo("New York", "06:30:00", "20:00:00"); // 13.5 hours

        when(mockRepository.getByCity("London")).thenReturn(city1);
        when(mockRepository.getByCity("New York")).thenReturn(city2);

        // Act: Call the method
        CityInfo result1 = weatherService.forecastByCity("London");
        CityInfo result2 = weatherService.forecastByCity("New York");

        // Assert: Verify that the city with longer daylight is correctly identified
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals("London", result1.getAddress());
        assertEquals("New York", result2.getAddress());

        // Verify repository calls
        verify(mockRepository, times(1)).getByCity("London");
        verify(mockRepository, times(1)).getByCity("New York");
    }

    /**
     * Test compareRain method by mocking two cities with different precipitation values.
     */
    @Test
    void testCompareRain() {
        // Arrange: Mock CityInfo responses with precipitation
        CityInfo city1 = createMockCityInfoWithPrecip("London", 0.0);  // No rain
        CityInfo city2 = createMockCityInfoWithPrecip("New York", 0.005); // Light rain

        when(mockRepository.getByCity("London")).thenReturn(city1);
        when(mockRepository.getByCity("New York")).thenReturn(city2);

        // Act: Call the method
        CityInfo result1 = weatherService.forecastByCity("London");
        CityInfo result2 = weatherService.forecastByCity("New York");

        // Assert: Verify correct city is experiencing rain
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(0.0, Double.parseDouble(result1.getDays().get(0).getPrecip())); // No rain
        assertTrue(Double.parseDouble(result2.getDays().get(0).getPrecip()) > 0);  // New York is raining

        // Verify repository calls
        verify(mockRepository, times(1)).getByCity("London");
        verify(mockRepository, times(1)).getByCity("New York");
    }

    /**
     * Helper method to create a mock CityInfo with sunrise and sunset times.
     */
    private CityInfo createMockCityInfo(String city, String sunrise, String sunset) {
        CityInfo cityInfo = new CityInfo();
        CityInfo.CurrentConditions conditions = new CityInfo.CurrentConditions();
        conditions.setSunrise(sunrise);
        conditions.setSunset(sunset);

        cityInfo.setCurrentConditions(conditions);
        cityInfo.setAddress(city);;
        return cityInfo;
    }

    /**
     * Helper method to create a mock CityInfo with precipitation data.
     */
    private CityInfo createMockCityInfoWithPrecip(String city, double precip) {
        CityInfo cityInfo = new CityInfo();
        List<CityInfo.Days> daysList = new ArrayList<>();

        CityInfo.Days day = new CityInfo.Days();
        day.setPrecip(String.valueOf(precip)); // Mock precipitation value
        daysList.add(day);

        cityInfo.setDays(daysList);
        cityInfo.setAddress(city);
        return cityInfo;
    }

}