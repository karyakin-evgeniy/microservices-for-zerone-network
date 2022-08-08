package org.proteam24.zeroneapplication.mapper;

import org.proteam24.zeroneapplication.entity.Support;
import org.proteam24.zeroneapplication.dto.SupportWriteDto;
import org.springframework.stereotype.Component;


@Component
public class SupportWriteMapper implements Mapper<SupportWriteDto, Support> {

    public Support map(SupportWriteDto object) {
        Support support = new Support();
        support.setEmail(object.getE_mail());
        support.setLastname(object.getLast_name());
        support.setMassage(object.getMessage());
        support.setFirstname(object.getFirst_name());

        return support;
    }
}
