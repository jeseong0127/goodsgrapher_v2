package com.vitasoft.goodsgrapher.domain.model.kipris.repository;

import com.vitasoft.goodsgrapher.domain.model.kipris.entity.DesignInfo;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.ModelInfo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DesignInfoRepository extends JpaRepository<DesignInfo, Integer> {
    DesignInfo findByRegistrationNumber(String registrationNumber);

    @Query("select distinct designInfo.imgPath from DesignInfo designInfo " +
            "inner join ModelInfo modelInfo " +
            "on modelInfo.registrationNumber = designInfo.registrationNumber " +
            "where designInfo.registrationNumber = :registrationNumber")
    String findAllByRegistrationNumber(String registrationNumber);
}
