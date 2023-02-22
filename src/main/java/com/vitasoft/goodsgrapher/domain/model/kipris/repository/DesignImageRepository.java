package com.vitasoft.goodsgrapher.domain.model.kipris.repository;

import com.vitasoft.goodsgrapher.domain.model.kipris.entity.DesignImage;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DesignImageRepository extends JpaRepository<DesignImage, Integer> {
    List<DesignImage> findAllByDesignSeqAndUseYn(int designSeq, String useYn);
}
