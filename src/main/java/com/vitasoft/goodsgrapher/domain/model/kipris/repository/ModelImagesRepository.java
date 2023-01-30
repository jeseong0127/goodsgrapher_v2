package com.vitasoft.goodsgrapher.domain.model.kipris.repository;

import com.vitasoft.goodsgrapher.domain.model.kipris.entity.ModelImages;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelImagesRepository extends JpaRepository<ModelImages, Integer> {
    List<ModelImages> findAllByModelSeqAndRegId(int modelSeq, String memberId);

    List<ModelImages> findByModelSeqAndIsDeletedAndRegId(int modelSeq, String s, String memberId);
}
