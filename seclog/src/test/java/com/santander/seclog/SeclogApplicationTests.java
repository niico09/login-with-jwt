package com.santander.seclog;

import com.santander.seclog.controllers.RoomController;
import com.santander.seclog.service.DeterminateBeer;
import com.santander.seclog.service.Weather;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest()
class SeclogApplicationTests {

    @Autowired
    private DeterminateBeer determinateBeer;

    @Autowired
    Weather weather;

    @Mock
    private RoomController roomController;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(roomController).build();
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void unathorizedTest() throws IOException {

        HttpUriRequest request = new HttpGet("http://localhost:8080/api/meetings/weather/41");

        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        Assert.assertEquals(HttpStatus.SC_UNAUTHORIZED, httpResponse.getStatusLine().getStatusCode());
        System.out.println("STATUS CODE: " + httpResponse.getStatusLine().getStatusCode());

    }

    @Test
    public void getBeers() throws IOException, InterruptedException {

        Random random = new Random();

        final String hourTest = "2020-10-19T18:46:19";
        final double persons = random.nextInt(300);

        int temperature = weather.getTemperatureDay(hourTest);
        int beers = determinateBeer.getCantBeers((int) persons, "2020-10-19T18:46:19");

        System.out.println("Hay " + persons + " en la meeting");

        if (temperature < 20) {
            System.out.println("La temperatura es menor a 20°");

            double totalDrink = persons * 0.75;

            System.out.println("Se tomaran: " + totalDrink + " cervezas.");

            Assert.assertEquals(totalDrink, beers, 0.01);
        } else if (temperature > 24) {
            System.out.println("La temperatura es mayor a 24°");

            double totalDrink = persons * 3;

            System.out.println("Se tomaran: " + totalDrink + " cervezas.");
            Assert.assertEquals(totalDrink, beers, 0.01);
        } else {
            System.out.println("La temperatura es ambiente");
            System.out.println("Se tomaran: " + persons + " cervezas.");

            Assert.assertEquals(persons, beers, 0.01);
        }

    }


}
