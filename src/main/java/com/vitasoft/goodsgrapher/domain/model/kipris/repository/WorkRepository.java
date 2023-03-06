package com.vitasoft.goodsgrapher.domain.model.kipris.repository;

import com.vitasoft.goodsgrapher.domain.model.kipris.entity.Work;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkRepository extends JpaRepository<Work, Integer> {
    Work findTopByModelSeqAndRegIdOrderByRegDateDesc(int modelSeq, String memberId);

    int countByRegIdAndStatus(String memberId, String status);

    List<Work> findAllByStatus(String status);

    List<Work> findAllByRegIdAndStatus(String memberId, String status);

    @Query("select work from Work work where work.regId = :memberId and work.modelSeq = :modelSeq and work.status in ('1','2', '3', '4')")
    Work findByRegIdAndModelSeq(String memberId, int modelSeq);

    @Query("select work from Work work where work.regId = :memberId and work.status in ('1', '2', '3', '4')")
    List<Work> findAllByRegId(String memberId);

    int countByModelSeqAndStatusNot(int modelSeq, String s);

    List<Work> findAllByRegIdOrderByRegDateDesc(String memberId);
}
