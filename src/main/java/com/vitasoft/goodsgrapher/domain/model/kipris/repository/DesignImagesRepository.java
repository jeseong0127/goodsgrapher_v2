package com.vitasoft.goodsgrapher.domain.model.kipris.repository;

import com.vitasoft.goodsgrapher.domain.model.kipris.entity.DesignImages;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DesignImagesRepository extends JpaRepository<DesignImages, Integer> {
    List<DesignImages> findAllByDesignSeq(int designSeq);
}
