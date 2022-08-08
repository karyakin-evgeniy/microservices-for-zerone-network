package org.proteam24.zeroneapplication.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CountriesAndCitiesDto {

    private Response response;

    @Getter
    public static class Response {
        private List<CountryAndCityDto> items;

    }

    @Getter
    public static class CountryAndCityDto {
        private int id;
        private String title;

    }
}
