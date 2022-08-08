package org.proteam24.zeroneapplication.service;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import lombok.extern.slf4j.Slf4j;
import org.proteam24.zeroneapplication.dto.BaseResponseDto;
import org.proteam24.zeroneapplication.dto.CountriesAndCitiesDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@Component
public class CountryAndCityService {

    private RestTemplate restTemplate = new RestTemplate();
    private static final String urlApi = "https://api.vk.com/method/database.";

    @Value("${vk.app-id}")
    private int appId;

    @Value("${vk.secret}")
    private String secret;

    @Value("${vk.token}")
    private String token;


    public BaseResponseDto<List<CountriesAndCitiesDto.CountryAndCityDto>> getCities(int countryId, String city) {
        if (countryId == 0) {
            return new BaseResponseDto<>(null, "add country id for request");
        }
        String url = urlApi +
                "getCities?country_id=" + countryId +
                "&count=40&v=5.131&access_token=" + token +
                "&q=" + city;
        List<CountriesAndCitiesDto.CountryAndCityDto> cities = Objects.requireNonNull(restTemplate.getForEntity(url, CountriesAndCitiesDto.class).getBody()).getResponse().getItems();


        return new BaseResponseDto<>(cities, "");
    }

    //@Cacheable("countries")
    public List<CountriesAndCitiesDto.CountryAndCityDto> getCountries() {
        String url = urlApi +
                "getCountries?count=20&v=5.131&access_token=" + token;
        log.info("Created request to VK API");
        return Objects.requireNonNull(restTemplate.getForEntity(url, CountriesAndCitiesDto.class).getBody()).getResponse().getItems();

    }

    public BaseResponseDto<List<CountriesAndCitiesDto.CountryAndCityDto>> getCountriesToName(List<CountriesAndCitiesDto.CountryAndCityDto> countries, String country) {
        List<CountriesAndCitiesDto.CountryAndCityDto> result = new ArrayList<>();
        if (country.isEmpty()) {
            result = countries;
        } else {
            List<CountriesAndCitiesDto.CountryAndCityDto> finalResult = result;
            countries.forEach(countryAndCityDto -> {
                if (countryAndCityDto.getTitle().startsWith(country)) {
                    finalResult.add(countryAndCityDto);
                }
            });
        }

        return new BaseResponseDto<>(result, "");
    }
}
