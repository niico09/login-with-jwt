package com.santander.seclog.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class DeterminateBeer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeterminateBeer.class);

    @Autowired
    Weather weather;

    @Value("${santander.beer}")
    private int beers;

    @Value("${santander.tempMin}")
    private int tempMin;

    @Value("${santander.tempMax}")
    private int tempMax;

    public void getBeers() {
        System.out.println("Cantidad de cervezas:" + beers);
    }

    public int getCantBeers(int persons, String hourStart) {

        LOGGER.info("Request to method, persons: {} , hourStart: {} " , persons,hourStart);

        try {
            int temp = weather.getTemperatureDay(hourStart);
            double drinkPerson = howDrinkOnePerson(temp);
            double totalDrink = persons * drinkPerson;
            double subtotal = totalDrink / beers;

            if (totalDrink < beers) {
                LOGGER.info("The meeting have {} persons each drink {} beers and needs {} beers",
                        persons, totalDrink, beers);

                return beers;
            } else if (totalDrink % beers == 0) {
                beers = (int) (Math.round(subtotal) * beers);
                LOGGER.info("The meeting have {} persons each drink {} beers and needs {} beers",
                        persons, totalDrink, beers);

                return beers;
            } else {
                beers += (int) (Math.floor(subtotal) * beers);
                LOGGER.info("The meeting have {} persons each drink {} beers and needs {} beers",
                        persons, totalDrink, beers);

                return beers;
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Internal error Service, persons: {}, hourStart: {}, ", persons, hourStart, e);
            LOGGER.error("For defect, return 6 beers");
            return beers;
        }
    }

    private double howDrinkOnePerson(int temp) {

        if (temp < tempMin) {
            return 0.75;
        } else if (temp > tempMax) {
            return 3;
        } else {
            return 1;
        }

    }
}