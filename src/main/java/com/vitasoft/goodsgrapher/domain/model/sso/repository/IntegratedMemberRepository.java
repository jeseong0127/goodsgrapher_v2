package com.vitasoft.goodsgrapher.domain.model.sso.repository;

import com.vitasoft.goodsgrapher.domain.model.sso.entity.IntegratedMember;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IntegratedMemberRepository extends JpaRepository<IntegratedMember, String> {
    Optional<IntegratedMember> findByMemberId(String memberId);
}