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

import com.iemr.ecd.dao.associate.MotherRecord;

import jakarta.transaction.Transactional;

@Repository
public interface MotherRecordRepo extends CrudRepository<MotherRecord, Long> {

//	@Query(" SELECT m FROM MotherRecord m WHERE m.isAllocated is false AND date(m.createdDate) BETWEEN "
//			+ " date(:fDate) AND date(:tDate)")
//	public List<MotherRecord> getMotherRecordForAllocation(@Param("fDate") Timestamp fDate,
//			@Param("tDate") Timestamp tDate);

	public List<MotherRecord> findByIsAllocatedAndWhomPhoneNoAndCreatedDateBetween(Boolean isAllocated,
			String phoneType, Timestamp fromDate, Timestamp toDate);

	@Query(value = " SELECT * FROM t_mothervalidrecord WHERE IsAllocated is false AND "
			+ " CreatedDate BETWEEN :fDate AND :tDate AND PhoneNo_Of_Whom =:phoneType LIMIT :recordLimit ", nativeQuery = true)
	public List<MotherRecord> getMotherRecordForAllocation(@Param("fDate") Timestamp fDate,
			@Param("tDate") Timestamp tDate, @Param("phoneType") String phoneType,
			@Param("recordLimit") int recordLimit);

	@Modifying
	@Transactional
	@Query(" UPDATE MotherRecord SET isAllocated = true WHERE ecdIdNo IN :motherId ")
	public int updateIsAllocatedStatus(@Param("motherId") List<Long> motherId);

	// get eligible introductory Records
	@Query(value = " SELECT COUNT(1) FROM MotherRecord as t WHERE t.isAllocated=:isAllocated AND "
			+ " t.createdDate >=:fDate AND t.createdDate <=:tDate AND t.whomPhoneNo=:whomPhoneNo  ")
	public int getRecordCount(@Param("isAllocated") Boolean isAllocated, @Param("fDate") Timestamp fDate,
			@Param("tDate") Timestamp tDate, @Param("whomPhoneNo") String whomPhoneNo);

	@Modifying
	@Transactional
	@Query(" UPDATE MotherRecord SET beneficiaryRegID = :beneficiaryRegID, motherName = :motherName, husbandName = :husbandName, whomPhoneNo = :whomPhoneNo, phoneNo = :phoneNo, "
			+ "lmpDate = :lmpDate, edd = :edd, address = :address, ashaName = :ashaName, ashaPh = :ashaPh, anmName = :anmName, anmPh = :anmPh, PHCName = :PHCName, blockName = :blockName, age = :age WHERE ecdIdNo = :motherId ")
	public int updateBeneficiaryRegIdForMother(@Param("beneficiaryRegID") Long beneficiaryRegID,
			@Param("motherId") Long motherId, @Param("motherName") String motherName,
			@Param("husbandName") String husbandName, @Param("whomPhoneNo") String whomPhoneNo,
			@Param("phoneNo") String phoneNo, @Param("lmpDate") Timestamp lmpDate, @Param("edd") Timestamp edd,
			@Param("address") String address, @Param("ashaName") String ashaName, @Param("ashaPh") String ashaPh, @Param("anmName") String anmName,
			@Param("anmPh") String anmPh, @Param("PHCName") String PHCName, @Param("blockName") String blockName, @Param("age") Integer age);

	@Modifying
	@Transactional
	@Query(" UPDATE MotherRecord SET motherName = :motherName, husbandName = :husbandName, whomPhoneNo = :whomPhoneNo, phoneNo = :phoneNo, "
			+ "lmpDate = :lmpDate, edd = :edd, address = :address, ashaName = :ashaName, ashaPh = :ashaPh, anmName = :anmName, anmPh = :anmPh, PHCName = :PHCName, blockName = :blockName, age = :age WHERE ecdIdNo = :motherId AND beneficiaryRegID = :beneficiaryRegID")
	public int updateBeneficiaryDetails(@Param("beneficiaryRegID") Long beneficiaryRegID,
			@Param("motherId") Long motherId, @Param("motherName") String motherName,
			@Param("husbandName") String husbandName, @Param("whomPhoneNo") String whomPhoneNo,
			@Param("phoneNo") String phoneNo, @Param("lmpDate") Timestamp lmpDate, @Param("edd") Timestamp edd,
			@Param("address") String address, @Param("ashaName") String ashaName, @Param("ashaPh") String ashaPh, @Param("anmName") String anmName,
			@Param("anmPh") String anmPh, @Param("PHCName") String PHCName, @Param("blockName") String blockName, @Param("age") Integer age);

	public MotherRecord findByEcdIdNo(Long motherId);

	@Modifying
	@Transactional
	@Query(" UPDATE MotherRecord SET isAllocated = false WHERE ecdIdNo IN :motherId ")
	public int updateIsAllocatedFalse(@Param("motherId") List<Long> motherId);

	@Modifying
	@Transactional
	@Query(" UPDATE MotherRecord SET preferredLanguage = :preferredLanguage WHERE ecdIdNo = :motherId ")
	public void updatePreferredLanguage(@Param("preferredLanguage") String preferredLanguage,@Param("motherId") Long motherId);

}
