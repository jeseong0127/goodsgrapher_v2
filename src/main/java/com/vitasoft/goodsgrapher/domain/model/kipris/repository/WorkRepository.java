package com.vitasoft.goodsgrapher.domain.model.kipris.repository;

import com.vitasoft.goodsgrapher.domain.model.kipris.entity.Work;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkRepository extends JpaRepository<Work, Integer> {
    Work findTopByModelSeqAndRegIdOrderByRegDateDesc(int modelSeq, String memberId);

    Work findTopByModelSeqAndRegIdOrderByRegDate(int modelSeq, String memberId);

    int countByRegIdAndStatus(String memberId, String status);

    List<Work> findAllByStatus(String status);

    @Query("select w from Work w where w.regId = :memberId group by w.modelSeq")
    List<Work> findAllByMetadata(@Param("memberId") String memberId);

    List<Work> findAllByRegIdAndStatus(String memberId, String status);

    Work findByRegIdAndModelSeqAndStatus(String memberId, int modelSeq, String status);

    @Query("select work from Work work where work.regId = :memberId and work.status = '3' or work.status = '4' or work.status = '5' ")
    List<Work> findAllByRegId(String memberId);
}
