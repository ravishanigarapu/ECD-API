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
package com.iemr.ecd.controller.masters;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.ecd.dao.QuestionnaireSections;
import com.iemr.ecd.dao.masters.AgentsViewMaster;
import com.iemr.ecd.dao.masters.AnswerType;
import com.iemr.ecd.dao.masters.CallNotAnsweredReason;
import com.iemr.ecd.dao.masters.CongenitalAnomalies;
import com.iemr.ecd.dao.masters.Cycles;
import com.iemr.ecd.dao.masters.Frequency;
import com.iemr.ecd.dao.masters.Gender;
import com.iemr.ecd.dao.masters.GradeMaster;
import com.iemr.ecd.dao.masters.HRNIReasons;
import com.iemr.ecd.dao.masters.HRPReasons;
import com.iemr.ecd.dao.masters.Language;
import com.iemr.ecd.dao.masters.NoFurtherCallsReason;
import com.iemr.ecd.dao.masters.Offices;
import com.iemr.ecd.dao.masters.QuestionnaireType;
import com.iemr.ecd.dao.masters.Role;
import com.iemr.ecd.dao.masters.SMSParameters;
import com.iemr.ecd.dao.masters.SMSParametersMapping;
import com.iemr.ecd.dao.masters.SMSType;
import com.iemr.ecd.dao.masters.TypeOfComplaints;
import com.iemr.ecd.dao.masters.V_GetUserlangmapping;
import com.iemr.ecd.service.masters.MasterServiceImpl;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping(value = "/master", headers = "Authorization")
@CrossOrigin()
public class MastersController {
	@Autowired
	private MasterServiceImpl masterServiceImpl;

	@Operation(summary = "Get agents by role id", description = "Desc - RCH data upload")
	@GetMapping("/getAgentsByRoleId/{roleId}")
	public ResponseEntity<List<AgentsViewMaster>> getAgentsByRoleId(
			@RequestHeader(value = "Authorization") String Authorization, @PathVariable Integer roleId) {
		return new ResponseEntity<>(masterServiceImpl.getAgentByRoleId(roleId), HttpStatus.OK);

	}

	@Operation(summary = "Get answer type", description = "Desc - RCH data upload")
	@GetMapping("/getAnswerType")
	public ResponseEntity<List<AnswerType>> getAnswerType() {
		return new ResponseEntity<>(masterServiceImpl.getAnswerType(), HttpStatus.OK);

	}

	@Operation(summary = "Get call not answered reasons", description = "Desc - RCH data upload")
	@GetMapping("/getCallNotAnswered")
	public ResponseEntity<List<CallNotAnsweredReason>> getCallNotAnsweredReasons() {
		return new ResponseEntity<>(masterServiceImpl.getCallNotAnsweredReasons(), HttpStatus.OK);

	}

	@Operation(summary = "Get congenital anomalies", description = "Desc - RCH data upload")
	@GetMapping("/getCongenitalAnomalies")
	public ResponseEntity<List<CongenitalAnomalies>> getCongenitalAnomalies() {
		return new ResponseEntity<>(masterServiceImpl.getCongenitalAnomalies(), HttpStatus.OK);

	}

	@Operation(summary = "Get cycles", description = "Desc - RCH data upload")
	@GetMapping("/getCycles")
	public ResponseEntity<List<Cycles>> getCycles() {
		return new ResponseEntity<>(masterServiceImpl.getAllCycles(), HttpStatus.OK);

	}

	@Operation(summary = "Get high risk newborn infant reasons", description = "Desc - RCH data upload")
	@GetMapping("/getHRNIReasons")
	public ResponseEntity<List<HRNIReasons>> getHRNIReasons() {
		return new ResponseEntity<>(masterServiceImpl.getAllHRNIReasons(), HttpStatus.OK);

	}

	@Operation(summary = "Get high risk pregnancy reasons", description = "Desc - RCH data upload")
	@GetMapping("/getHRPReasons")
	public ResponseEntity<List<HRPReasons>> getHRPReasons() {
		return new ResponseEntity<>(masterServiceImpl.getAllHRPReasons(), HttpStatus.OK);

	}

	@Operation(summary = "Get language", description = "Desc - RCH data upload")
	@GetMapping("/getLanguage")
	public ResponseEntity<List<Language>> getLanguage() {
		return new ResponseEntity<>(masterServiceImpl.getLanguage(), HttpStatus.OK);

	}

	@Operation(summary = "Get no further calls reason", description = "Desc - RCH data upload")
	@GetMapping("/getNoFurtherCallsReason")
	public ResponseEntity<List<NoFurtherCallsReason>> getNoFurtherCallsReason() {
		return new ResponseEntity<>(masterServiceImpl.getNoFurtherCallsReason(), HttpStatus.OK);

	}

	@Operation(summary = "Get offices", description = "Desc - RCH data upload")
	@GetMapping("/getOffices")
	public ResponseEntity<List<Offices>> getOffices() {
		return new ResponseEntity<>(masterServiceImpl.findOffices(), HttpStatus.OK);

	}

	@Operation(summary = "Get offices by PSM id", description = "Desc - RCH data upload")
	@GetMapping("/getOfficesByPSMID/{psmId}")
	public ResponseEntity<List<Offices>> getOfficesByPSMID(@PathVariable Integer psmId) {
		return new ResponseEntity<>(masterServiceImpl.findOfficesByPsmId(psmId), HttpStatus.OK);

	}

	@Operation(summary = "Get offices by district id", description = "Desc - RCH data upload")
	@GetMapping("/getOfficesByDistrictId/{districtId}")
	public ResponseEntity<List<Offices>> getOfficesByDistrictId(Integer districtId) {
		return new ResponseEntity<>(masterServiceImpl.findOfficesByDistrictId(districtId), HttpStatus.OK);

	}

	@Operation(summary = "Get offices by district id and PSM id", description = "Desc - RCH data upload")
	@GetMapping("/getOfficesByDistrictIdAndPSMID/{districtId}/{psmId}")
	public ResponseEntity<List<Offices>> getOfficesByDistrictIdAndPSMID(@PathVariable Integer districtId,
			@PathVariable Integer psmId) {
		return new ResponseEntity<>(masterServiceImpl.findOfficesByDistrictIdAndPsmId(districtId, psmId),
				HttpStatus.OK);

	}

	@Operation(summary = "Get SMS parameters", description = "Desc - RCH data upload")
	@GetMapping("/getSMSParameters")
	public ResponseEntity<List<SMSParametersMapping>> getSMSParameters() {
		return new ResponseEntity<>(masterServiceImpl.getSMSParameters(), HttpStatus.OK);

	}

	@Operation(summary = "Get questionnaire type", description = "Desc - RCH data upload")
	@GetMapping("/getQuestionnaireType")
	public ResponseEntity<List<QuestionnaireType>> getQuestionnaireType() {
		return new ResponseEntity<>(masterServiceImpl.getQuestionnaireType(), HttpStatus.OK);

	}

	@Operation(summary = "Get roles by PSM id", description = "Desc - RCH data upload")
	@GetMapping("/getRolesByPsmId/{psmId}")
	public ResponseEntity<List<Role>> getRolesByPsmId(@PathVariable Integer psmId) {
		return new ResponseEntity<>(masterServiceImpl.getRolesByPsmId(psmId), HttpStatus.OK);

	}

	@Operation(summary = "Get SMS types", description = "Desc - RCH data upload")
	@GetMapping("/getSMSTypes")
	public ResponseEntity<List<SMSType>> getSMSTypes() {
		return new ResponseEntity<>(masterServiceImpl.getSMSTypes(), HttpStatus.OK);

	}

	@Operation(summary = "Get type of complaints", description = "Desc - RCH data upload")
	@GetMapping("/getTypeOfComplaints")
	public ResponseEntity<List<TypeOfComplaints>> getTypeOfComplaints() {
		return new ResponseEntity<>(masterServiceImpl.getTypeOfComplaints(), HttpStatus.OK);

	}

	@Operation(summary = "Get SMS values", description = "Desc - RCH data upload")
	@GetMapping("/getSMSValues")
	public ResponseEntity<List<SMSParameters>> getSMSValues() {
		return new ResponseEntity<>(masterServiceImpl.getSMSValues(), HttpStatus.OK);

	}

	@Operation(summary = "Get grades", description = "Desc - RCH data upload")
	@GetMapping("/getGrades")
	public ResponseEntity<List<GradeMaster>> getGrades() {
		return new ResponseEntity<>(masterServiceImpl.getGrades(), HttpStatus.OK);

	}

	@Operation(summary = "Get sections by PSM id", description = "Desc - RCH data upload")
	@GetMapping("/getSectionsByPsmId/{psmId}")
	public ResponseEntity<List<QuestionnaireSections>> getSectionsByPsmId(@PathVariable Integer psmId) {
		return new ResponseEntity<>(masterServiceImpl.getSectionsByPsmId(psmId), HttpStatus.OK);
	}

	@Operation(summary = "Get frequency", description = "Desc - RCH data upload")
	@GetMapping("/getFrequency")
	public ResponseEntity<List<Frequency>> getFrequency() {
		return new ResponseEntity<>(masterServiceImpl.getFrequency(), HttpStatus.OK);

	}

	@Operation(summary = "Get language by user id", description = "Desc - RCH data upload")
	@GetMapping("/getLanguageByUserId/{userId}")
	public ResponseEntity<List<V_GetUserlangmapping>> getLanguageByUserId(@PathVariable Integer userId) {
		return new ResponseEntity<>(masterServiceImpl.getLanguageByUserId(userId), HttpStatus.OK);

	}

	@Operation(summary = "Get gender", description = "Desc - RCH data upload")
	@GetMapping("/getGender")
	public ResponseEntity<List<Gender>> getGender() {
		return new ResponseEntity<>(masterServiceImpl.getGenders(), HttpStatus.OK);

	}
	@Operation(summary = "Get agents by role id and preferred language", description = "Fetches agents filtered by both role ID and their preferred language setting")
	@GetMapping("/getAgentsByRoleIdAndPreferredLanguage/{roleId}/{preferredLanguage}")
	public ResponseEntity<List<AgentsViewMaster>> getAgentsByRoleIdAndLanguage(
			@RequestHeader(value = "Authorization") String authorization, @PathVariable Integer roleId, @PathVariable String preferredLanguage) {
		return new ResponseEntity<>(masterServiceImpl.getAgentByRoleIdAndLanguage(roleId, preferredLanguage), HttpStatus.OK);

	}
}
