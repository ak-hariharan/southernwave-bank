package com.southernwavebank.report_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.southernwavebank.report_service.model.entity.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long>{
}
