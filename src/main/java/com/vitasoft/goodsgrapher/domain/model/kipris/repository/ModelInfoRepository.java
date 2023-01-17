package com.vitasoft.goodsgrapher.domain.model.kipris.repository;

import com.vitasoft.goodsgrapher.domain.model.kipris.entity.ModelInfo;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelInfoRepository extends JpaRepository<ModelInfo, Integer> {
//    int countByReserveId(String reserveId);

    List<ModelInfo> findAllByRegId(String memberId);

//    Optional<ModelInfo> findByMetaSeqAndRegId(int metaSeq, String memberId);

    @Query("select m from ModelInfo m where (:codeId is null or m.codeId = :codeId) and m.modelName is not null and m.regId is null and (m.articleName like %:articleName% or m.modelName like %:modelName% or m.companyName Like %:companyName%) and m.useYn = 'Y'")
    List<ModelInfo> findAllByMetadata(@Param("codeId") String codeId, @Param("articleName") String articleName, @Param("modelName") String modelName, @Param("companyName") String companyName);

//    List<ModelInfo> findAllByReserveDateNotNull();

    List<ModelInfo> findAllByModelNameIsNotNullAndUseYn(char useYn);

    Optional<ModelInfo> findByModelSeqAndRegId(int metaSeq, String memberId);
}
