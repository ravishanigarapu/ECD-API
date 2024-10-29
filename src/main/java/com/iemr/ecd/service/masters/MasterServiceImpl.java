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
package com.iemr.ecd.service.masters;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
import com.iemr.ecd.repo.call_conf_allocation.QuestionnaireSectionRepo;
import com.iemr.ecd.repository.masters.AgentsViewMasterRepo;
import com.iemr.ecd.repository.masters.AnswerTypeRepo;
import com.iemr.ecd.repository.masters.CallNotAnsweredRepo;
import com.iemr.ecd.repository.masters.CongenitalAnomaliesRepo;
import com.iemr.ecd.repository.masters.CycleRepo;
import com.iemr.ecd.repository.masters.FrequencyRepo;
import com.iemr.ecd.repository.masters.GenderRepo;
import com.iemr.ecd.repository.masters.GradeMasterRepo;
import com.iemr.ecd.repository.masters.HRNIReasonsRepo;
import com.iemr.ecd.repository.masters.HRPReasonsRepo;
import com.iemr.ecd.repository.masters.LanguageRepo;
import com.iemr.ecd.repository.masters.NoFurtherCallsReasonRepo;
import com.iemr.ecd.repository.masters.OfficesRepo;
import com.iemr.ecd.repository.masters.ParametersRepo;
import com.iemr.ecd.repository.masters.QuestionnaireTypeRepo;
import com.iemr.ecd.repository.masters.RoleRepo;
import com.iemr.ecd.repository.masters.SMSTypeRepo;
import com.iemr.ecd.repository.masters.TypeOfComplaintsRepo;
import com.iemr.ecd.repository.masters.UserServiceRoleMappingRepo;
import com.iemr.ecd.repository.masters.V_GetUserlangmappingRepo;
import com.iemr.ecd.repository.masters.ValueRepo;
import com.iemr.ecd.utils.advice.exception_handler.ECDException;

@Service
public class MasterServiceImpl {
	@Autowired
	private HRPReasonsRepo hrpReasonsRepo;
	@Autowired
	private HRNIReasonsRepo hrniReasonsRepo;
	@Autowired
	private CycleRepo cycleRepo;
	@Autowired
	private OfficesRepo officesRepo;
	@Autowired
	private UserServiceRoleMappingRepo agentRepo;
	@Autowired
	private AnswerTypeRepo answerTypeRepo;
	@Autowired
	private CallNotAnsweredRepo callNotAnsweredRepo;
	@Autowired
	private CongenitalAnomaliesRepo congenitalAnomaliesRepo;
	@Autowired
	private GradeMasterRepo gradeMasterRepo;
	@Autowired
	private LanguageRepo languageRepo;
	@Autowired
	private NoFurtherCallsReasonRepo noFurtherCallsReasonRepo;
	@Autowired
	private ParametersRepo parametersRepo;
	@Autowired
	private QuestionnaireTypeRepo questionnaireTypeRepo;
	@Autowired
	private RoleRepo roleRepo;
	@Autowired
	private SMSTypeRepo smsTypeRepo;
	@Autowired
	private TypeOfComplaintsRepo typeOfComplaintsRepo;
	@Autowired
	private ValueRepo valueRepo;
	@Autowired
	private AgentsViewMasterRepo agentsViewMasterRepo;
	@Autowired
	private QuestionnaireSectionRepo questionnaireSectionRepo;
	@Autowired
	private FrequencyRepo frequencyRepo;
	@Autowired
	private V_GetUserlangmappingRepo v_GetUserlangmappingRepo;
	@Autowired
	private GenderRepo genderRepo;

	// HRP master
	public List<HRPReasons> getAllHRPReasons() throws ECDException {
		return hrpReasonsRepo.findByDeleted(false);
	}

	public List<HRNIReasons> getAllHRNIReasons() throws ECDException {
		return hrniReasonsRepo.findByDeleted(false);
	}

	// cycle master
	public List<Cycles> getAllCycles() throws ECDException {
		return cycleRepo.findByDeleted(false);
	}

	// office master
	public List<Offices> findOffices() throws ECDException {
		return officesRepo.findByDeleted(false);
	}

	public List<Offices> findOfficesByPsmId(Integer psmId) throws ECDException {
		return officesRepo.findByPsmId(psmId);
	}

	public List<Offices> findOfficesByDistrictId(Integer distId) throws ECDException {
		return officesRepo.findByDistrictId(distId);
	}

	public List<Offices> findOfficesByDistrictIdAndPsmId(Integer distId, Integer psmId) throws ECDException {
		return officesRepo.findByDistrictIdAndPsmId(distId, psmId);
	}
	// agent master

	public List<AgentsViewMaster> getAgentByRoleId(Integer roleId) throws ECDException {
		return agentsViewMasterRepo.findByRoleId(roleId);
	}

	// answer type master
	public List<AnswerType> getAnswerType() throws ECDException {
		return answerTypeRepo.findByDeleted(false);
	}
	// callNotAnswered reasons master

	public List<CallNotAnsweredReason> getCallNotAnsweredReasons() {
		return callNotAnsweredRepo.findByDeleted(false);
	}

	// congenitalAnomalies master
	public List<CongenitalAnomalies> getCongenitalAnomalies() {
		return congenitalAnomaliesRepo.findByDeleted(false);
	}
	//grade master
	public List<GradeMaster> getGrades() {
		return gradeMasterRepo.findByDeleted(false);
	}


	

	// language master
	public List<Language> getLanguage() {
		return languageRepo.findByDeleted(false);
	}

	// no further calls reason master
	public List<NoFurtherCallsReason> getNoFurtherCallsReason() {
		return noFurtherCallsReasonRepo.findByDeleted(false);
	}

	// sms parameters master
	public List<SMSParametersMapping> getSMSParameters() {
		return parametersRepo.findByDeleted(false);
	}

	// questionnaire type master
	public List<QuestionnaireType> getQuestionnaireType() {
		return questionnaireTypeRepo.findByDeleted(false);
	}

	// Role master
	public List<Role> getRolesByPsmId(Integer psmId) {
		return roleRepo.findByPsmIdAndDeleted(psmId, false);
	}

	// sms type master
	public List<SMSType> getSMSTypes() {
		return smsTypeRepo.findByDeleted(false);
	}

	// type of complaints master
	public List<TypeOfComplaints> getTypeOfComplaints() {
		return typeOfComplaintsRepo.findByDeleted(false);
	}

	// sms value master
	public List<SMSParameters> getSMSValues() {
		return valueRepo.findByDeleted(false);
	}
	
	public List<QuestionnaireSections> getSectionsByPsmId(Integer psmId) {
		return questionnaireSectionRepo.findByPsmIdAndDeleted(psmId, false);
		
	}
	//frequency master
	public List<Frequency> getFrequency() {
		return frequencyRepo.findByDeleted(false);
	}
	// language mapping master
	public List<V_GetUserlangmapping> getLanguageByUserId(Integer userId) throws ECDException {
		return v_GetUserlangmappingRepo.findByUserId(userId);
	}
	
	
	//gender master
		public List<Gender> getGenders() {
			return genderRepo.findByDeleted(false);
		}

		public List<AgentsViewMaster> getAgentByRoleIdAndLanguage(Integer roleId, String preferredLanguage) {
			return agentsViewMasterRepo.findByRoleIdAndLanguage(roleId, preferredLanguage);
		}

}
