package com.vitasoft.goodsgrapher.domain.model.kipris.repository;

import com.vitasoft.goodsgrapher.domain.model.dto.GetImageSearchDto;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.DesignInfo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DesignInfoRepository extends JpaRepository<DesignInfo, Integer> {
    DesignInfo findByRegistrationNumber(String registrationNumber);

    GetImageSearchDto findDistinctByRegistrationNumber(String registrationNumber);
}
