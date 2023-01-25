package com.vitasoft.goodsgrapher.domain.model.kipris.repository;

import com.vitasoft.goodsgrapher.domain.model.kipris.entity.Work;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkRepository extends JpaRepository<Work, Integer> {
    Work findTopByModelSeqAndRegIdOrderByRegDateDesc(int modelSeq, String memberId);

    int countByRegIdAndStatus(String memberId, String status);

    List<Work> findAllByStatus(String status);
}
