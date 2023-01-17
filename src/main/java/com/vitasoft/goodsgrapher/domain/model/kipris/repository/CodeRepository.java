package com.vitasoft.goodsgrapher.domain.model.kipris.repository;

import com.vitasoft.goodsgrapher.domain.model.kipris.entity.Code;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.CodePK;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodeRepository extends JpaRepository<Code, CodePK> {
    List<Code> findAllByHighCodeIdAndCodeGroup(String highCodeId, String codeGroup);

    List<Code> findAllByHighCodeId(String codeId);
}
