/*
* AMRIT – Accessible Medical Records via Integrated Technology
* Integrated EHR (Electronic Health Records) Solution
*
* Copyright (C) "Piramal Swasthya Management and Research Institute"
*
* This file is part of AMRIT.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see https://www.gnu.org/licenses/.
*/
package com.iemr.ecd.repo.call_conf_allocation;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iemr.ecd.dao.associate.ChildRecord;

import jakarta.transaction.Transactional;

@Repository
public interface ChildRecordRepo extends CrudRepository<ChildRecord, Long> {

	public List<ChildRecord> findByIsAllocatedAndWhomPhoneNoAndCreatedDateBetween(Boolean isAllocated, String phoneType,
			Timestamp fromDate, Timestamp toDate);

	@Query(value = " SELECT * FROM t_childvaliddata WHERE IsAllocated is false AND "
			+ " CreatedDate BETWEEN :fDate AND :tDate AND Phone_No_of =:phoneType LIMIT :recordLimit ", nativeQuery = true)
	public List<ChildRecord> getChildRecordForAllocation(@Param("fDate") Timestamp fDate,
			@Param("tDate") Timestamp tDate, @Param("phoneType") String phoneType,
			@Param("recordLimit") int recordLimit);

	@Modifying
	@Transactional
	@Query(" UPDATE ChildRecord SET isAllocated = true WHERE ecdIdNoChildId IN :childId ")
	public int updateIsAllocatedStatus(@Param("childId") List<Long> childId);

	// get eligible introductory Records
	@Query(value = " SELECT COUNT(1) FROM ChildRecord as t WHERE t.isAllocated=:isAllocated AND "
			+ " t.createdDate >=:fDate AND t.createdDate <=:tDate AND t.whomPhoneNo=:whomPhoneNo  ")
	public int getRecordCount(@Param("isAllocated") Boolean isAllocated, @Param("fDate") Timestamp fDate,
			@Param("tDate") Timestamp tDate, @Param("whomPhoneNo") String whomPhoneNo);

	@Modifying
	@Transactional
	@Query(" UPDATE ChildRecord SET beneficiaryRegId = :beneficiaryRegId, childName = :childName, fatherName = :fatherName, whomPhoneNo = :whomPhoneNo, phoneNo = :phoneNo, "
			+ "dob = :dob, motherName = :motherName, gender = :gender, address = :address, ashaName = :ashaName, ashaPh = :ashaPh, anmName = :anmName, anmPh = :anmPh, PHCName = :PHCName, blockName = :blockName   WHERE ecdIdNoChildId = :childId ")
	public int updateBeneficiaryRegIdForChild(@Param("beneficiaryRegId") Long beneficiaryRegId,
			@Param("childId") Long childId, @Param("childName") String childName,
			@Param("fatherName") String fatherName, @Param("whomPhoneNo") String whomPhoneNo,
			@Param("phoneNo") String phoneNo, @Param("dob") Timestamp dob, @Param("motherName") String motherName,
			@Param("gender") String gender, @Param("address") String address, @Param("ashaName") String ashaName,
			@Param("ashaPh") String ashaPh, @Param("anmName") String anmName, @Param("anmPh") String anmPh,
			@Param("PHCName") String PHCName, @Param("blockName") String blockName);

	@Modifying
	@Transactional
	@Query(" UPDATE ChildRecord SET childName = :childName, fatherName = :fatherName, whomPhoneNo = :whomPhoneNo, phoneNo = :phoneNo, "
			+ "dob = :dob, motherName = :motherName, gender = :gender, address = :address, ashaName = :ashaName, ashaPh = :ashaPh, anmName = :anmName, anmPh = :anmPh, PHCName = :PHCName, blockName = :blockName  WHERE ecdIdNoChildId = :childId AND  beneficiaryRegId = :beneficiaryRegId")
	public int updateBeneficiaryDetails(@Param("beneficiaryRegId") Long beneficiaryRegId,
			@Param("childId") Long childId, @Param("childName") String childName,
			@Param("fatherName") String fatherName, @Param("whomPhoneNo") String whomPhoneNo,
			@Param("phoneNo") String phoneNo, @Param("dob") Timestamp dob, @Param("motherName") String motherName,
			@Param("gender") String gender, @Param("address") String address, @Param("ashaName") String ashaName,
			@Param("ashaPh") String ashaPh, @Param("anmName") String anmName, @Param("anmPh") String anmPh,
			@Param("PHCName") String PHCName, @Param("blockName") String blockName);

	public ChildRecord findByEcdIdNoChildId(Long childId);

	@Modifying
	@Transactional
	@Query(" UPDATE ChildRecord SET isAllocated = false WHERE ecdIdNoChildId IN :childId ")
	public int updateIsAllocatedFalse(@Param("childId") List<Long> childId);

	@Modifying
	@Transactional
	@Query(" UPDATE ChildRecord SET preferredLanguage = :preferredLanguage WHERE ecdIdNoChildId = :childId ")
	public void updatePreferredLanguage(@Param("preferredLanguage") String preferredLanguage,@Param("childId") Long childId);
}
