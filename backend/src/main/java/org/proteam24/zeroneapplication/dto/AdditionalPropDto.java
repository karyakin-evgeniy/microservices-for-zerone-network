package org.proteam24.zeroneapplication.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdditionalPropDto {
    private String additionalProp1;
    private String additionalProp2;
    private String additionalProp3;

    public AdditionalPropDto(String additionalProp1) {

        this.additionalProp1 = additionalProp1;
    }

    public AdditionalPropDto(String additionalProp1, String additionalProp2, String additionalProp3) {
        this.additionalProp1 = additionalProp1;
        this.additionalProp2 = additionalProp2;
        this.additionalProp3 = additionalProp3;
    }
}
