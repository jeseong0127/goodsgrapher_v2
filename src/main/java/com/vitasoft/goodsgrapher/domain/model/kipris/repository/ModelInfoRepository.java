package com.vitasoft.goodsgrapher.domain.model.kipris.repository;

import com.vitasoft.goodsgrapher.domain.model.kipris.entity.ModelInfo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelInfoRepository extends JpaRepository<ModelInfo, Integer> {

    @Query("select m from ModelInfo m where (:codeId is null or m.codeId = :codeId) and m.modelName is not null and m.regId is null and (m.articleName like %:searchWord% or m.modelName like %:searchWord% or m.companyName Like %:searchWord%) and m.useYn = 'Y'")
    List<ModelInfo> findAllByMetadata(@Param("codeId") String codeId, @Param("searchWord") String searchWord);

    List<ModelInfo> findAllByModelNameIsNotNullAndUseYn(char useYn);

    ModelInfo findTopByRegistrationNumber(String registrationNumber);

    ModelInfo findByModelSeq(int modelSeq);
}
