package com.vitasoft.goodsgrapher.domain.model.kipris.repository;

import com.vitasoft.goodsgrapher.domain.model.kipris.entity.Metadata;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MetadataRepository extends JpaRepository<Metadata, Integer> {
    List<Metadata> findAllByImgCountLessThan(int imgCount);

    int countByReserveId(String reserveId);

    List<Metadata> findAllByReserveIdOrRegIdOrderByReserveIdDesc(String reserveId, String regId);

    Optional<Metadata> findByMetaSeqAndRegId(int metaSeq, String memberId);

    @Query("select m from Metadata m where (:codeId is null or m.codeId = :codeId) and m.modelName is not null and m.reserveId = 'N/A' and m.regId is null and m.imgCount < 62 and (m.articleName like %:articleName% or m.modelName like %:modelName% or m.companyName Like %:companyName%) and m.useYn = 'Y'")
    List<Metadata> findAllByMetadata(@Param("codeId") String codeId, @Param("articleName") String articleName, @Param("modelName") String modelName, @Param("companyName") String companyName);

    List<Metadata> findAllByReserveDateNotNull();

    List<Metadata> findAllByModelNameIsNotNullAndReserveIdAndRegIdNullAndImgCountLessThanAndUseYn(String defaultReserveId, int imgCount, char useYn);
}
