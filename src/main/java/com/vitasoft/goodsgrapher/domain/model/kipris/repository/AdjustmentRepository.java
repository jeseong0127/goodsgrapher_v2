package com.vitasoft.goodsgrapher.domain.model.kipris.repository;

import com.vitasoft.goodsgrapher.domain.model.kipris.entity.Adjustment;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdjustmentRepository extends JpaRepository<Adjustment, Integer> {
    Optional<Adjustment> findByMetaSeqAndAdjustId(int metaSeq, String memberId);

    List<Adjustment> findByAdjustId(String memberId);
}
