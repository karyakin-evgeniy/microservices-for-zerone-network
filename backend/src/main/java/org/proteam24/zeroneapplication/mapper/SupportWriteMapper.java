package org.proteam24.zeroneapplication.mapper;

import org.proteam24.zeroneapplication.entity.Support;
import org.proteam24.zeroneapplication.dto.SupportWriteDto;
import org.springframework.stereotype.Component;


@Component
public class SupportWriteMapper implements Mapper<SupportWriteDto, Support> {

    public Support map(SupportWriteDto object) {
        Support support = new Support();
        support.setFirstname(object.getFirstname());
        support.setLastname(object.getLastname());
        support.setEmail(object.getEmail());
        support.setMassage(object.getMessage());

        return support;
    }
}
