package com.vitasoft.goodsgrapher.domain.model.kipris.repository;

import com.vitasoft.goodsgrapher.domain.model.kipris.entity.ModelImage;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelImageRepository extends JpaRepository<ModelImage, Integer> {
    List<ModelImage> findAllByModelSeqAndRegId(int modelSeq, String memberId);

    ModelImage findByFileName(String fileName);

    List<ModelImage> findByModelSeqAndIsDeletedAndRegId(int modelSeq, String s, String memberId);

    Optional<ModelImage> findTopByModelSeqAndRegIdOrderByUploadSeqDesc(int modelSeq, String memberId);

    int countAllByRegIdAndModelSeqAndIsDeleted(String memberId, int modelSeq, String isDeleted);
}
