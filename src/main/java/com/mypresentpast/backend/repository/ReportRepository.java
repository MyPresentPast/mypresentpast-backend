package com.mypresentpast.backend.repository;

import com.mypresentpast.backend.enums.ReportStatus;
import com.mypresentpast.backend.model.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query("select count(r) > 0 from Report r where r.post.id = :postId and r.reporter.id = :reporterId and r.status in :statuses")
    boolean existsActiveReportByPostIdAndReporterId(@Param("postId") Long postId,
                                                    @Param("reporterId") Long reporterId,
                                                    @Param("statuses") Collection<ReportStatus> statuses);

    Page<Report> findByStatus(ReportStatus status, Pageable pageable);
}
