package org.proteam24.reports.repository;

import org.proteam24.reports.entity.ReportEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ReportRepository extends CrudRepository<ReportEntity, Long> {

}
