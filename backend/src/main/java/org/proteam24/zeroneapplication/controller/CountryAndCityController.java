package org.proteam24.zeroneapplication.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.proteam24.zeroneapplication.dto.BaseResponseDto;
import org.proteam24.zeroneapplication.dto.CountriesAndCitiesDto;
import org.proteam24.zeroneapplication.service.CountryAndCityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/platform")
@Tag(name = "Страны и города", description = "Работа со списком стран и городов")
public class CountryAndCityController {
    private final CountryAndCityService countryAndCityService;

    public CountryAndCityController(CountryAndCityService countryAndCityService) {
        this.countryAndCityService = countryAndCityService;
    }

    @Operation(security = @SecurityRequirement(name = "bearer-key"),
            summary = "Получение списка городов", description = "Получение списка город выбраной страны")
    @GetMapping(value = "/cities")
    public BaseResponseDto<List<CountriesAndCitiesDto.CountryAndCityDto>> getCities(@RequestParam(defaultValue = "1") int countryId, @RequestParam(defaultValue = "") String city){
        try {
            return countryAndCityService.getCities(countryId, city);
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponseDto<>(null, e.getMessage());
        }
    }

    @GetMapping(value = "/languages")
    @Operation(summary = "Языки", description = "Получение списка языков")
    public Map<Object, String> languages(){
        return Collections.emptyMap();
    }

    @Operation(security = @SecurityRequirement(name = "bearer-key"),
            summary = "Получение списка стран", description = "Получение списка стран согласно введенным данным")
    @GetMapping(value = "/countries")
    public BaseResponseDto<List<CountriesAndCitiesDto.CountryAndCityDto>> countries(@RequestParam(defaultValue = "0") int countryId, @RequestParam(defaultValue = "") String country){
        try {
            List<CountriesAndCitiesDto.CountryAndCityDto> countries = countryAndCityService.getCountries();
            return countryAndCityService.getCountriesToName(countries, country);
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponseDto<>(null, e.getMessage());
        }
    }
}
