package swp.group2.swpbe.report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import swp.group2.swpbe.report.entities.Report;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {
    List<Report> findAllByOrderByReportDateDesc();

    Report findById(int id);
}
