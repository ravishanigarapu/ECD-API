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
package com.iemr.ecd.service.call_conf_allocation;

import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.iemr.ecd.dao.CallConfiguration;
import com.iemr.ecd.dao.associate.ChildRecord;
import com.iemr.ecd.dao.associate.MotherRecord;
import com.iemr.ecd.dao.associate.OutboundCalls;
import com.iemr.ecd.dto.OutboundCallsDTO;
import com.iemr.ecd.dto.RequestCallAllocationDTO;
import com.iemr.ecd.dto.supervisor.ResponseEligibleCallRecordsDTO;
import com.iemr.ecd.repo.call_conf_allocation.CallConfigurationRepo;
import com.iemr.ecd.repo.call_conf_allocation.ChildRecordRepo;
import com.iemr.ecd.repo.call_conf_allocation.MotherRecordRepo;
import com.iemr.ecd.repo.call_conf_allocation.OutboundCallsRepo;
import com.iemr.ecd.utils.advice.exception_handler.ECDException;
import com.iemr.ecd.utils.advice.exception_handler.InvalidRequestException;
import com.iemr.ecd.utils.constants.Constants;

import jakarta.transaction.Transactional;

@Service
public class CallAllocationImpl {

	@Autowired
	private MotherRecordRepo motherRecordRepo;
	@Autowired
	private OutboundCallsRepo outboundCallsRepo;
	@Autowired
	private ChildRecordRepo childRecordRepo;
	@Autowired
	private CallConfigurationRepo callConfigurationRepo;

	@Transactional(rollbackOn = Exception.class)
	public String allocateCalls(RequestCallAllocationDTO callAllocationDto) {

		try {
			if (callAllocationDto != null && callAllocationDto.getRoleName().equalsIgnoreCase("associate")
					&& callAllocationDto.getRecordType().equalsIgnoreCase("mother")) {
				return allocateMotherRecordsAssociates(callAllocationDto);
			} else if (callAllocationDto != null && callAllocationDto.getRoleName().equalsIgnoreCase("associate")
					&& callAllocationDto.getRecordType().equalsIgnoreCase("child")) {
				return allocateChildRecordsAssociates(callAllocationDto);
			} else if (callAllocationDto != null && callAllocationDto.getRoleName().equalsIgnoreCase("ANM")
					&& callAllocationDto.getRecordType().equalsIgnoreCase("mother")) {
				return allocateMotherRecordsToANM(callAllocationDto);
			} else if (callAllocationDto != null && callAllocationDto.getRoleName().equalsIgnoreCase("ANM")
					&& callAllocationDto.getRecordType().equalsIgnoreCase("child")) {
				return allocateChildRecordsToANM(callAllocationDto);
			} else if (callAllocationDto != null && callAllocationDto.getRoleName().equalsIgnoreCase("MO")
					&& callAllocationDto.getRecordType().equalsIgnoreCase("mother")) {
				return allocateMotherRecordsToMO(callAllocationDto);
			} else if (callAllocationDto != null && callAllocationDto.getRoleName().equalsIgnoreCase("MO")
					&& callAllocationDto.getRecordType().equalsIgnoreCase("child")) {
				return allocateChildRecordsToMO(callAllocationDto);
			} else
				throw new ECDException("please pass valid role and record type");
		} catch (Exception e) {
			throw new ECDException(e);
		}

	}

	private String allocateMotherRecordsAssociates(RequestCallAllocationDTO callAllocationDto) throws ParseException {
		List<OutboundCalls> outBoundCallList = new ArrayList<>();
		int totalRecordToAllocate = 0;
		if (callAllocationDto != null && callAllocationDto.getToUserIds() != null
				&& callAllocationDto.getToUserIds().length > 0 && callAllocationDto.getNoOfCalls() > 0)
			totalRecordToAllocate = (callAllocationDto.getToUserIds().length) * (callAllocationDto.getNoOfCalls());
		else
			throw new InvalidRequestException();

		if (totalRecordToAllocate <= 0)
			throw new InvalidRequestException();
		else {

			Timestamp tempFDateStamp = null;
			Timestamp tempTDateStamp = null;
			if (callAllocationDto.getFDate() != null && callAllocationDto.getTDate() != null) {
				tempFDateStamp = getTimestampFromString(
						callAllocationDto.getFDate().split(Constants.T)[0].concat(Constants.TIME_FORMAT_START_TIME));
				tempTDateStamp = getTimestampFromString(
						callAllocationDto.getTDate().split(Constants.T)[0].concat(Constants.TIME_FORMAT_END_TIME));
			} else
				throw new InvalidRequestException(Constants.FROM_DATE_TO_DATE_IS_NULL);

			List<MotherRecord> resultSet = motherRecordRepo.getMotherRecordForAllocation(tempFDateStamp, tempTDateStamp,
					callAllocationDto.getPhoneNoType(), totalRecordToAllocate);

			OutboundCalls outboundCalls;

			int callCountPointer = 0;
			if (resultSet != null && resultSet.size() > 0) {
				List<Long> motherIds = new ArrayList<>();
				for (MotherRecord motherRecord : resultSet) {
					try {

						outboundCalls = new OutboundCalls();

						if (motherRecord.getEcdIdNo() != null)
							outboundCalls.setMotherId(motherRecord.getEcdIdNo());
						if (motherRecord.getBeneficiaryRegID() != null)
							outboundCalls.setBeneficiaryRegId(motherRecord.getBeneficiaryRegID());
						if (callAllocationDto.getPsmId() != null)
							outboundCalls.setPsmId(callAllocationDto.getPsmId());

						if (motherRecord.getWhomPhoneNo() != null)
							outboundCalls.setPhoneNumberType(motherRecord.getWhomPhoneNo());

						outboundCalls.setEcdCallType("introductory");
						outboundCalls.setDisplayEcdCallType("introductory");
						outboundCalls.setCallStatus("Open");
						outboundCalls.setAllocationStatus(Constants.ALLOCATED);
						outboundCalls.setAllocatedUserId(
								callAllocationDto.getToUserIds()[callCountPointer / callAllocationDto.getNoOfCalls()]);

						outboundCalls.setCreatedBy(callAllocationDto.getCreatedBy());
						if (motherRecord.getHighRisk() != null)
							outboundCalls.setIsHighRisk(motherRecord.getHighRisk());

						if (motherRecord.getHighRiskReason() != null)
							outboundCalls.setHighRiskReason(motherRecord.getHighRiskReason());

						if (motherRecord.getCreatedDate() != null)
							outboundCalls.setCallDateFrom(getTimestampDaysLater(motherRecord.getCreatedDate(), 0));

						outboundCalls.setCallDateTo(getTimestampDaysLater(motherRecord.getCreatedDate(), 30));

						outboundCalls.setCallAttemptNo(0);

						outBoundCallList.add(outboundCalls);

						callCountPointer++;

						motherIds.add(motherRecord.getEcdIdNo());
					} catch (Exception e) {
						// log
						callCountPointer++;
					}
				}
				outboundCallsRepo.saveAll(outBoundCallList);

				int i = motherRecordRepo.updateIsAllocatedStatus(motherIds);

				Map<String, Object> responseMap = new HashMap<>();
				responseMap.put("response", outBoundCallList.size() + " mother record allocated successfully");
				return new Gson().toJson(responseMap);
			} else
				throw new ECDException("no eligible record available to allocate, please contact administrator");

		}
	}

	private String allocateChildRecordsAssociates(RequestCallAllocationDTO callAllocationDto) throws ParseException {
		List<OutboundCalls> outBoundCallList = new ArrayList<>();
		int totalRecordToAllocate = 0;

		if (callAllocationDto != null && callAllocationDto.getToUserIds() != null
				&& callAllocationDto.getToUserIds().length > 0 && callAllocationDto.getNoOfCalls() > 0)
			totalRecordToAllocate = (callAllocationDto.getToUserIds().length) * (callAllocationDto.getNoOfCalls());
		else
			throw new InvalidRequestException();

		if (totalRecordToAllocate <= 0)
			throw new InvalidRequestException();
		else {

			Timestamp tempFDateStamp = null;
			Timestamp tempTDateStamp = null;
			if (callAllocationDto.getFDate() != null && callAllocationDto.getTDate() != null) {
				tempFDateStamp = getTimestampFromString(
						callAllocationDto.getFDate().split(Constants.T)[0].concat(Constants.TIME_FORMAT_START_TIME));
				tempTDateStamp = getTimestampFromString(
						callAllocationDto.getTDate().split(Constants.T)[0].concat(Constants.TIME_FORMAT_END_TIME));
			} else
				throw new InvalidRequestException(Constants.FROM_DATE_TO_DATE_IS_NULL);

			List<ChildRecord> resultSet = childRecordRepo.getChildRecordForAllocation(tempFDateStamp, tempTDateStamp,
					callAllocationDto.getPhoneNoType(), totalRecordToAllocate);

			OutboundCalls outboundCalls;

			int callCountPointer = 0;

			if (resultSet != null && !resultSet.isEmpty()) {
				List<Long> childIds = new ArrayList<>();
				for (ChildRecord childRecord : resultSet) {

					try {
						outboundCalls = new OutboundCalls();

						if (childRecord.getEcdIdNoChildId() != null)
							outboundCalls.setChildId(childRecord.getEcdIdNoChildId());
						if (childRecord.getMotherId() != null)
							outboundCalls.setMotherId(childRecord.getMotherId());
						if (childRecord.getBeneficiaryRegId() != null)
							outboundCalls.setBeneficiaryRegId(childRecord.getBeneficiaryRegId());
						if (callAllocationDto.getPsmId() != null)
							outboundCalls.setPsmId(callAllocationDto.getPsmId());

						if (childRecord.getWhomPhoneNo() != null)
							outboundCalls.setPhoneNumberType(childRecord.getWhomPhoneNo());

						outboundCalls.setEcdCallType("introductory");
						outboundCalls.setDisplayEcdCallType("introductory");
						outboundCalls.setCallStatus("Open");
						outboundCalls.setAllocationStatus(Constants.ALLOCATED);
						outboundCalls.setAllocatedUserId(
								callAllocationDto.getToUserIds()[callCountPointer / callAllocationDto.getNoOfCalls()]);

						outboundCalls.setCreatedBy(callAllocationDto.getCreatedBy());

						outboundCalls.setIsHrni(childRecord.getIsHrni());
						outboundCalls.setHrniReason(childRecord.getHrni_Reason());

						if (childRecord.getCreatedDate() != null)
							outboundCalls.setCallDateFrom(getTimestampDaysLater(childRecord.getCreatedDate(), 0));

						outboundCalls.setCallDateTo(getTimestampDaysLater(childRecord.getCreatedDate(), 30));

						outboundCalls.setCallAttemptNo(0);

						outBoundCallList.add(outboundCalls);

						childIds.add(childRecord.getEcdIdNoChildId());
						callCountPointer++;
					} catch (Exception e) {
						callCountPointer++;
						// log
					}

				}
				outboundCallsRepo.saveAll(outBoundCallList);
				int i = childRecordRepo.updateIsAllocatedStatus(childIds);

				Map<String, Object> responseMap = new HashMap<>();
				responseMap.put("response", outBoundCallList.size() + " child record allocated successfully");
				return new Gson().toJson(responseMap);

			}
			throw new ECDException("no eligible record available to allocate, please contact administrator");

		}
	}

	private String allocateMotherRecordsToANM(RequestCallAllocationDTO callAllocationDto) throws ParseException {

		int totalRecordToAllocate = 0;

		if (callAllocationDto != null && callAllocationDto.getToUserIds() != null
				&& callAllocationDto.getToUserIds().length > 0 && callAllocationDto.getNoOfCalls() > 0)
			totalRecordToAllocate = (callAllocationDto.getToUserIds().length) * (callAllocationDto.getNoOfCalls());
		else
			throw new InvalidRequestException();

		Timestamp tempFDateStamp = null;
		Timestamp tempTDateStamp = null;
		if (callAllocationDto.getFDate() != null && callAllocationDto.getTDate() != null) {
			tempFDateStamp = getTimestampFromString(
					callAllocationDto.getFDate().split(Constants.T)[0].concat(Constants.TIME_FORMAT_START_TIME));
			tempTDateStamp = getTimestampFromString(
					callAllocationDto.getTDate().split(Constants.T)[0].concat(Constants.TIME_FORMAT_END_TIME));
		} else
			throw new InvalidRequestException(Constants.FROM_DATE_TO_DATE_IS_NULL);

		Pageable pageable = PageRequest.of(0, totalRecordToAllocate);

		Page<OutboundCalls> outboundCallsPage = outboundCallsRepo.getMotherRecordsForANM(pageable, "unallocated",
				callAllocationDto.getPsmId(), tempFDateStamp, tempTDateStamp, callAllocationDto.getPreferredLanguage());

		List<OutboundCalls> outboundCallsList = outboundCallsPage.getContent();

		int callCountPointer = 0;
		if (!outboundCallsList.isEmpty()) {
			for (OutboundCalls outboundCalls : outboundCallsList) {
				try {
					outboundCalls.setAllocationStatus(Constants.ALLOCATED);
					outboundCalls.setAllocatedUserId(
							callAllocationDto.getToUserIds()[callCountPointer / callAllocationDto.getNoOfCalls()]);

					outboundCalls.setCallAttemptNo(0);

					callCountPointer++;
				} catch (Exception e) {
					callCountPointer++;
				}
			}

			outboundCallsRepo.saveAll(outboundCallsList);

			Map<String, Object> responseMap = new HashMap<>();
			responseMap.put("response",
					outboundCallsList.size() + " mother record allocated successfully to selected ANM");
			return new Gson().toJson(responseMap);

		} else
			throw new ECDException("no eligible record available to allocate, please contact administrator");

	}

	private String allocateChildRecordsToANM(RequestCallAllocationDTO callAllocationDto) throws ParseException {
		int totalRecordToAllocate = 0;

		if (callAllocationDto != null && callAllocationDto.getToUserIds() != null
				&& callAllocationDto.getToUserIds().length > 0 && callAllocationDto.getNoOfCalls() > 0)
			totalRecordToAllocate = (callAllocationDto.getToUserIds().length) * (callAllocationDto.getNoOfCalls());
		else
			throw new InvalidRequestException();

		Timestamp tempFDateStamp = null;
		Timestamp tempTDateStamp = null;
		if (callAllocationDto.getFDate() != null && callAllocationDto.getTDate() != null) {
			tempFDateStamp = getTimestampFromString(
					callAllocationDto.getFDate().split(Constants.T)[0].concat(Constants.TIME_FORMAT_START_TIME));
			tempTDateStamp = getTimestampFromString(
					callAllocationDto.getTDate().split(Constants.T)[0].concat(Constants.TIME_FORMAT_END_TIME));
		} else
			throw new InvalidRequestException(Constants.FROM_DATE_TO_DATE_IS_NULL);

		Pageable pageable = PageRequest.of(0, totalRecordToAllocate);

		Page<OutboundCalls> outboundCallsPage = outboundCallsRepo.getChildRecordsForANM(pageable, "unallocated",
				callAllocationDto.getPsmId(), tempFDateStamp, tempTDateStamp, callAllocationDto.getPreferredLanguage());

		List<OutboundCalls> outboundCallsList = outboundCallsPage.getContent();

		int callCountPointer = 0;
		if (outboundCallsList != null && outboundCallsList.size() > 0) {
			for (OutboundCalls outboundCalls : outboundCallsList) {
				try {
					outboundCalls.setAllocationStatus(Constants.ALLOCATED);
					outboundCalls.setAllocatedUserId(
							callAllocationDto.getToUserIds()[callCountPointer / callAllocationDto.getNoOfCalls()]);

					outboundCalls.setCallAttemptNo(0);

					callCountPointer++;
				} catch (Exception e) {
					// log
					callCountPointer++;
				}
			}

			outboundCallsRepo.saveAll(outboundCallsList);

			Map<String, Object> responseMap = new HashMap<>();
			responseMap.put("response",
					outboundCallsList.size() + " child record allocated successfully to selected ANM");
			return new Gson().toJson(responseMap);
		} else
			throw new ECDException("no eligible record available to allocate, please contact administrator");

	}

	private String allocateMotherRecordsToMO(RequestCallAllocationDTO callAllocationDto) throws ParseException {
		int totalRecordToAllocate = 0;

		if (callAllocationDto != null && callAllocationDto.getToUserIds() != null
				&& callAllocationDto.getToUserIds().length > 0 && callAllocationDto.getNoOfCalls() > 0)
			totalRecordToAllocate = (callAllocationDto.getToUserIds().length) * (callAllocationDto.getNoOfCalls());
		else
			throw new InvalidRequestException();

		Timestamp tempFDateStamp = null;
		Timestamp tempTDateStamp = null;
		if (callAllocationDto.getFDate() != null && callAllocationDto.getTDate() != null) {
			tempFDateStamp = getTimestampFromString(
					callAllocationDto.getFDate().split(Constants.T)[0].concat(Constants.TIME_FORMAT_START_TIME));
			tempTDateStamp = getTimestampFromString(
					callAllocationDto.getTDate().split(Constants.T)[0].concat(Constants.TIME_FORMAT_END_TIME));
		} else
			throw new InvalidRequestException(Constants.FROM_DATE_TO_DATE_IS_NULL);

		Pageable pageable = PageRequest.of(0, totalRecordToAllocate);

		Page<OutboundCalls> outboundCallsPage = outboundCallsRepo.getMotherRecordsForMO(pageable, "unallocated",
				callAllocationDto.getPsmId());

		List<OutboundCalls> outboundCallsList = outboundCallsPage.getContent();

		int callCountPointer = 0;
		if (!outboundCallsList.isEmpty()) {
			for (OutboundCalls outboundCalls : outboundCallsList) {
				try {
					outboundCalls.setAllocationStatus(Constants.ALLOCATED);
					outboundCalls.setAllocatedUserId(
							callAllocationDto.getToUserIds()[callCountPointer / callAllocationDto.getNoOfCalls()]);
					outboundCalls.setCallAttemptNo(0);

					callCountPointer++;
				} catch (Exception e) {
					callCountPointer++;
				}
			}

			outboundCallsRepo.saveAll(outboundCallsList);

			Map<String, Object> responseMap = new HashMap<>();
			responseMap.put("response",
					outboundCallsList.size() + " mother record allocated successfully to selected MO");
			return new Gson().toJson(responseMap);
		} else
			throw new ECDException("no eligible record available to allocate, please contact administrator");

	}

	private String allocateChildRecordsToMO(RequestCallAllocationDTO callAllocationDto) throws ParseException {
		int totalRecordToAllocate = 0;

		if (callAllocationDto != null && callAllocationDto.getToUserIds() != null
				&& callAllocationDto.getToUserIds().length > 0 && callAllocationDto.getNoOfCalls() > 0)
			totalRecordToAllocate = (callAllocationDto.getToUserIds().length) * (callAllocationDto.getNoOfCalls());
		else
			throw new InvalidRequestException("please pass valid users");

		Timestamp tempFDateStamp = null;
		Timestamp tempTDateStamp = null;
		if (callAllocationDto.getFDate() != null && callAllocationDto.getTDate() != null) {
			tempFDateStamp = getTimestampFromString(
					callAllocationDto.getFDate().split(Constants.T)[0].concat(Constants.TIME_FORMAT_START_TIME));
			tempTDateStamp = getTimestampFromString(
					callAllocationDto.getTDate().split(Constants.T)[0].concat(Constants.TIME_FORMAT_END_TIME));
		} else
			throw new InvalidRequestException(Constants.FROM_DATE_TO_DATE_IS_NULL);

		Pageable pageable = PageRequest.of(0, totalRecordToAllocate);
		Page<OutboundCalls> outboundCallsPage = outboundCallsRepo.getChildRecordsForMO(pageable, Constants.UNALLOCATED,
				callAllocationDto.getPsmId(), tempFDateStamp, tempTDateStamp);

		List<OutboundCalls> outboundCallsList = outboundCallsPage.getContent();

		int callCountPointer = 0;
		if (!outboundCallsList.isEmpty()) {
			for (OutboundCalls outboundCalls : outboundCallsList) {
				try {
					outboundCalls.setAllocationStatus(Constants.ALLOCATED);
					outboundCalls.setAllocatedUserId(
							callAllocationDto.getToUserIds()[callCountPointer / callAllocationDto.getNoOfCalls()]);
					outboundCalls.setCallAttemptNo(0);
					callCountPointer++;
				} catch (Exception e) {
					callCountPointer++;
				}
			}

			outboundCallsRepo.saveAll(outboundCallsList);

			Map<String, Object> responseMap = new HashMap<>();
			responseMap.put("response",
					outboundCallsList.size() + " child record allocated successfully to selected MO");
			return new Gson().toJson(responseMap);
		} else
			throw new ECDException("no eligible record available to allocate, please contact administrator");

	}

	public ResponseEligibleCallRecordsDTO getEligibleRecordsInfo(int psmId, String phoneNoType, String recordType,
			String fDate, String tDate) {

		try {

			Timestamp tempFDateStamp = null;
			Timestamp tempTDateStamp = null;
			if (fDate != null && tDate != null) {
				tempFDateStamp = getTimestampFromString(fDate.split(Constants.T)[0].concat(Constants.TIME_FORMAT_START_TIME));
				tempTDateStamp = getTimestampFromString(tDate.split(Constants.T)[0].concat(Constants.TIME_FORMAT_END_TIME));
			} else
				throw new InvalidRequestException(Constants.FROM_DATE_TO_DATE_IS_NULL);

			ResponseEligibleCallRecordsDTO responseEligibleCallRecordsDTO = new ResponseEligibleCallRecordsDTO();
			int totalIntroductoryRecord = 0;
			int totalLowRisk = 0;
			int totalHighRisk = 0;
			int totalAllocated = 0;

			if (recordType != null && recordType.equalsIgnoreCase(Constants.MOTHER)) {

				totalIntroductoryRecord = motherRecordRepo.getRecordCount(false, tempFDateStamp, tempTDateStamp,
						phoneNoType);

				totalLowRisk = outboundCallsRepo.getMotherUnAllocatedCountLR(Constants.UNALLOCATED, psmId, tempFDateStamp,
						tempTDateStamp, phoneNoType);
				totalHighRisk = outboundCallsRepo.getMotherUnAllocatedCountHR(Constants.UNALLOCATED, psmId,phoneNoType);

				totalAllocated = outboundCallsRepo.getTotalAllocatedCountMother(Constants.ALLOCATED, psmId, tempFDateStamp,
						tempTDateStamp, phoneNoType);

			} else if (recordType != null && recordType.equalsIgnoreCase("Child")) {

				totalIntroductoryRecord = childRecordRepo.getRecordCount(false, tempFDateStamp, tempTDateStamp,
						phoneNoType);

				totalLowRisk = outboundCallsRepo.getChildUnAllocatedCountLR("unallocated", psmId, tempFDateStamp,
						tempTDateStamp, phoneNoType);
				totalHighRisk = outboundCallsRepo.getChildUnAllocatedCountHR("unallocated", psmId, tempFDateStamp,
						tempTDateStamp, phoneNoType);

				totalAllocated = outboundCallsRepo.getTotalAllocatedCountChild("allocated", psmId, tempFDateStamp,
						tempTDateStamp, phoneNoType);

			}

			responseEligibleCallRecordsDTO.setTotalIntroductoryRecord(totalIntroductoryRecord);
			responseEligibleCallRecordsDTO.setTotalLowRiskRecord(totalLowRisk);
			responseEligibleCallRecordsDTO.setTotalHighRiskRecord(totalHighRisk);
			responseEligibleCallRecordsDTO.setTotalRecord(totalIntroductoryRecord + totalLowRisk + totalHighRisk);

			responseEligibleCallRecordsDTO.setTotalAllocatedRecord(totalAllocated);

			return responseEligibleCallRecordsDTO;
		} catch (Exception e) {
			throw new ECDException(e);
		}

	}

	@Transactional(rollbackOn = Exception.class)
	public String moveAllocatedCallsToBin(RequestCallAllocationDTO callAllocationDto) {
		try {
			if (callAllocationDto.getUserId() != null && callAllocationDto.getNoOfCalls() != null
					&& callAllocationDto.getRecordType() != null && callAllocationDto.getPhoneNoType() != null
					&& callAllocationDto.getFDate() != null && callAllocationDto.getTDate() != null) {

				Timestamp tempFDateStamp = null;
				Timestamp tempTDateStamp = null;
				if (callAllocationDto.getFDate() != null && callAllocationDto.getTDate() != null) {
					tempFDateStamp = getTimestampFromString(
							callAllocationDto.getFDate().split(Constants.T)[0].concat(Constants.TIME_FORMAT_START_TIME));
					tempTDateStamp = getTimestampFromString(
							callAllocationDto.getTDate().split(Constants.T)[0].concat(Constants.TIME_FORMAT_END_TIME));
				} else
					throw new InvalidRequestException(Constants.FROM_DATE_TO_DATE_IS_NULL);

				Pageable pageable = PageRequest.of(0, callAllocationDto.getNoOfCalls());

				Page<OutboundCalls> outboundCallsPage = null;

				if (callAllocationDto.getRecordType().equalsIgnoreCase("Mother")) {
					if (null != callAllocationDto.getRoleName()
							&& callAllocationDto.getRoleName().equalsIgnoreCase("ANM")) {
						outboundCallsPage = outboundCallsRepo.getAllocatedRecordsUserByRecordTypeAndPhoneTypeMotherANM(
								pageable, callAllocationDto.getUserId(), "open", callAllocationDto.getPhoneNoType(),
								tempFDateStamp, tempTDateStamp, callAllocationDto.getPreferredLanguage());
					} else {
						outboundCallsPage = outboundCallsRepo.getAllocatedRecordsUserByRecordTypeAndPhoneTypeMother(
								pageable, callAllocationDto.getUserId(), "open", callAllocationDto.getPhoneNoType(),
								tempFDateStamp, tempTDateStamp);
					}
				} else if (callAllocationDto.getRecordType().equalsIgnoreCase("Child")) {
					if (null != callAllocationDto.getRoleName()
							&& callAllocationDto.getRoleName().equalsIgnoreCase("ANM")) {
						outboundCallsPage = outboundCallsRepo.getAllocatedRecordsUserByRecordTypeAndPhoneTypeChildANM(
								pageable, callAllocationDto.getUserId(), "open", callAllocationDto.getPhoneNoType(),
								tempFDateStamp, tempTDateStamp, callAllocationDto.getPreferredLanguage());
					} else {
						outboundCallsPage = outboundCallsRepo.getAllocatedRecordsUserByRecordTypeAndPhoneTypeChild(
								pageable, callAllocationDto.getUserId(), "open", callAllocationDto.getPhoneNoType(),
								tempFDateStamp, tempTDateStamp);
					}
				}

				List<Long> motherIds = new ArrayList<>();
				List<Long> childIds = new ArrayList<>();

				if (outboundCallsPage != null && outboundCallsPage.getSize() > 0) {
					List<OutboundCalls> resultList = outboundCallsPage.getContent();
					for (OutboundCalls outboundCalls : resultList) {

						outboundCalls.setAllocatedUserId(null);
						outboundCalls.setAllocationStatus("unallocated");

						if (outboundCalls.getEcdCallType() != null
								&& outboundCalls.getEcdCallType().equalsIgnoreCase("introductory")) {
							outboundCalls.setDeleted(true);
							// write logic to update in mother or child table also - isAllocated = false
							if (outboundCalls.getChildId() != null)
								childIds.add(outboundCalls.getChildId());
							else if (outboundCalls.getMotherId() != null)
								motherIds.add(outboundCalls.getMotherId());

						}

					}

					outboundCallsRepo.saveAll(resultList);

					if (motherIds.size() > 0)
						motherRecordRepo.updateIsAllocatedFalse(motherIds);
					if (childIds.size() > 0)
						childRecordRepo.updateIsAllocatedFalse(childIds);
				} else
					throw new ECDException("no record available for move to bin. please contact administrator");
			} else
				throw new InvalidRequestException(callAllocationDto.toString(),
						"NULL or part of required request is NULL");

			Map<String, Object> responseMap = new HashMap<>();
			responseMap.put("response", "records successfully moved to bin");
			return new Gson().toJson(responseMap);
		} catch (Exception e) {
			throw new ECDException(e);
		}

	}

	public String getAllocatedCallCountUser(RequestCallAllocationDTO callAllocationDto) {
		Map<String, Integer> responseMap = new HashMap<>();
		try {
			if (callAllocationDto != null && callAllocationDto.getRecordType() != null) {

				Timestamp tempFDateStamp = null;
				Timestamp tempTDateStamp = null;
				if (callAllocationDto.getFDate() != null && callAllocationDto.getTDate() != null) {
					tempFDateStamp = getTimestampFromString(
							callAllocationDto.getFDate().split(Constants.T)[0].concat(Constants.TIME_FORMAT_START_TIME));
					tempTDateStamp = getTimestampFromString(
							callAllocationDto.getTDate().split(Constants.T)[0].concat(Constants.TIME_FORMAT_END_TIME));
				} else
					throw new InvalidRequestException(Constants.FROM_DATE_TO_DATE_IS_NULL);

				int cnt = 0;

				if (callAllocationDto.getRecordType().equalsIgnoreCase(Constants.MOTHER)) {
					if(null != callAllocationDto.getRoleName() && callAllocationDto.getRoleName().equalsIgnoreCase(Constants.ANM)) {
						cnt = outboundCallsRepo.getAllocatedRecordsCountMotherUserANM(callAllocationDto.getUserId(),
								tempFDateStamp, tempTDateStamp, Constants.OPEN, callAllocationDto.getPhoneNoType(),callAllocationDto.getPreferredLanguage());
					}else {
						cnt = outboundCallsRepo.getAllocatedRecordsCountMotherUser(callAllocationDto.getUserId(),
								tempFDateStamp, tempTDateStamp, Constants.OPEN, callAllocationDto.getPhoneNoType());
					}
				} else if (callAllocationDto.getRecordType().equalsIgnoreCase(Constants.CHILD)) {
					if(null != callAllocationDto.getRoleName() && callAllocationDto.getRoleName().equalsIgnoreCase(Constants.ANM)) {
						cnt = outboundCallsRepo.getAllocatedRecordsCountChildUserANM(callAllocationDto.getUserId(),
								tempFDateStamp, tempTDateStamp, Constants.OPEN, callAllocationDto.getPhoneNoType(), callAllocationDto.getPreferredLanguage());
					}else {
						cnt = outboundCallsRepo.getAllocatedRecordsCountChildUser(callAllocationDto.getUserId(),
								tempFDateStamp, tempTDateStamp, Constants.OPEN, callAllocationDto.getPhoneNoType());
					}
				} else
					throw new InvalidRequestException("Invalid recordType",
							"please pass valid recordType - Mother / Child");

				responseMap.put("totalCount", cnt);

			} else
				throw new InvalidRequestException("NULL recordType", "please pass valid data/recordType");
			return new Gson().toJson(responseMap);
		} catch (Exception e) {
			throw new ECDException(e);
		}
	}

	@Transactional(rollbackOn = Exception.class)
	public String reAllocateCalls(RequestCallAllocationDTO callAllocationDto) {
		try {
			if (callAllocationDto != null && callAllocationDto.getUserId() != null
					&& callAllocationDto.getToUserIds() != null && callAllocationDto.getToUserIds().length > 0
					&& callAllocationDto.getRecordType() != null && callAllocationDto.getPhoneNoType() != null
					&& callAllocationDto.getNoOfCalls() != null) {

				int totalRecordToAllocate = 0;

				Timestamp tempFDateStamp = null;
				Timestamp tempTDateStamp = null;
				if (callAllocationDto.getFDate() != null && callAllocationDto.getTDate() != null) {
					tempFDateStamp = getTimestampFromString(
							callAllocationDto.getFDate().split(Constants.T)[0].concat(Constants.TIME_FORMAT_START_TIME));
					tempTDateStamp = getTimestampFromString(
							callAllocationDto.getTDate().split(Constants.T)[0].concat(Constants.TIME_FORMAT_END_TIME));
				} else
					throw new InvalidRequestException(Constants.FROM_DATE_TO_DATE_IS_NULL);

				if (callAllocationDto.getNoOfCalls() > 0)
					totalRecordToAllocate = (callAllocationDto.getToUserIds().length)
							* (callAllocationDto.getNoOfCalls());
				else
					throw new InvalidRequestException("NULL or Invalid NoOfCalls",
							"pass valid NoOfCalls, greater than 0");

				Pageable pageable = PageRequest.of(0, totalRecordToAllocate);

				Page<OutboundCalls> outboundCallsPage = null;

				if (callAllocationDto.getRecordType().equalsIgnoreCase(Constants.MOTHER)) {
					if(null != callAllocationDto.getRoleName() && callAllocationDto.getRoleName().equalsIgnoreCase(Constants.ANM)) {
						outboundCallsPage = outboundCallsRepo.getAllocatedRecordsUserByRecordTypeAndPhoneTypeMotherANM(
								pageable, callAllocationDto.getUserId(), Constants.OPEN, callAllocationDto.getPhoneNoType(),
								tempFDateStamp, tempTDateStamp, callAllocationDto.getPreferredLanguage());
					}else {
					outboundCallsPage = outboundCallsRepo.getAllocatedRecordsUserByRecordTypeAndPhoneTypeMother(
							pageable, callAllocationDto.getUserId(), Constants.OPEN, callAllocationDto.getPhoneNoType(),
							tempFDateStamp, tempTDateStamp);
					}
				} else if (callAllocationDto.getRecordType().equalsIgnoreCase(Constants.CHILD)) {
					if(null != callAllocationDto.getRoleName() && callAllocationDto.getRoleName().equalsIgnoreCase(Constants.ANM)) {
						outboundCallsPage = outboundCallsRepo.getAllocatedRecordsUserByRecordTypeAndPhoneTypeChildANM(pageable,
								callAllocationDto.getUserId(), Constants.OPEN, callAllocationDto.getPhoneNoType(), tempFDateStamp,
								tempTDateStamp,callAllocationDto.getPreferredLanguage() );
					}else {
					outboundCallsPage = outboundCallsRepo.getAllocatedRecordsUserByRecordTypeAndPhoneTypeChild(pageable,
							callAllocationDto.getUserId(), Constants.OPEN, callAllocationDto.getPhoneNoType(), tempFDateStamp,
							tempTDateStamp);
					}
				}

				if (outboundCallsPage != null && outboundCallsPage.getSize() > 0) {
					List<OutboundCalls> outboundCallsList = outboundCallsPage.getContent();

					int callCountPointer = 0;
					if (outboundCallsList != null && !outboundCallsList.isEmpty()) {
						for (OutboundCalls outboundCalls : outboundCallsList) {
							try {
								outboundCalls.setAllocationStatus(Constants.ALLOCATED);
								outboundCalls.setAllocatedUserId(callAllocationDto.getToUserIds()[callCountPointer
										/ callAllocationDto.getNoOfCalls()]);

								callCountPointer++;
							} catch (Exception e) {
								callCountPointer++;
							}
						}

						outboundCallsRepo.saveAll(outboundCallsList);
					}
				}

			} else
				throw new InvalidRequestException("NULL userId/recordType/phoneNoType/noOfCalls/toUserIds",
						"pass valid userId-recordType-phoneNoType-noOfCalls-toUserIds");

			Map<String, Object> responseMap = new HashMap<>();
			responseMap.put("response", "records successfully re-allocated");
			return new Gson().toJson(responseMap);
		} catch (Exception e) {
			throw new ECDException(e);
		}

	}

	private Timestamp getTimestampFromString(String date) throws ParseException {

		DateTimeFormatter ISO_DATE_TIME = DateTimeFormatter.ISO_DATE_TIME;
		LocalDateTime localDateTime = LocalDateTime.from(ISO_DATE_TIME.parse(date));

		Timestamp timestamp = Timestamp.valueOf(localDateTime);

		return timestamp;

	}

	private Timestamp getTimestampDaysLater(Timestamp timeStamp, int daysLater) throws ParseException {
		if (timeStamp != null && daysLater >= 0) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(timeStamp);
			cal.add(Calendar.DAY_OF_WEEK, daysLater);

			return new Timestamp(cal.getTime().getTime());
		} else
			return new Timestamp(System.currentTimeMillis());
	}
	
	@Transactional(rollbackOn = Exception.class)
	public String insertRecordsInOutboundCalls(OutboundCallsDTO outboundCallsDTO) {
		try {

			List<CallConfiguration> callConfigurationDetails = callConfigurationRepo
					.getCallConfiguration(outboundCallsDTO.getPsmId());
			List<OutboundCalls> resultSet = outboundCallsRepo.getIntroductoryRecordsUser(outboundCallsDTO.getPsmId(),
					"completed", "introductory");

			if (resultSet != null) {
				if (callConfigurationDetails != null && callConfigurationDetails.size() > 0) {

					ChildRecord childRecord = null;
					MotherRecord motherRecord = null;
					List<OutboundCalls> outboundCallsList = new ArrayList<>();
					for (OutboundCalls outboundCalls : resultSet) {
						if (outboundCalls.getChildId() != null) {
							childRecord = childRecordRepo.findByEcdIdNoChildId(outboundCalls.getChildId());

						} else {
							if (outboundCalls.getMotherId() != null) {
								motherRecord = motherRecordRepo.findByEcdIdNo(outboundCalls.getMotherId());
							} else {
								throw new InvalidRequestException("Missing RCH Id");
							}
						}

						OutboundCalls outboundCallsSet;
						Timestamp callStartDate = null;

						for (CallConfiguration callConfiguration : callConfigurationDetails) {
							outboundCallsSet = new OutboundCalls();

							// set object values based on logic
							if (motherRecord != null && motherRecord.getLmpDate() != null) {
								if (callConfiguration.getBaseLine().equalsIgnoreCase("DOB"))
									continue;

								Timestamp callEndDate = null;
								if (callStartDate == null)
									callStartDate = motherRecord.getLmpDate();
								outboundCallsSet.setCallDateFrom(callStartDate);

								if (callConfiguration.getConfigTerms() != null
										&& callConfiguration.getConfigTerms().equalsIgnoreCase("days")) {

									Calendar cal = Calendar.getInstance();
									cal.setTime(motherRecord.getLmpDate());
									cal.add(Calendar.DAY_OF_WEEK, callConfiguration.getTermRange());

									callEndDate = new Timestamp(cal.getTime().getTime());

								} else if (callConfiguration.getConfigTerms() != null
										&& callConfiguration.getConfigTerms().equalsIgnoreCase("months")) {

									Calendar cal = Calendar.getInstance();
									cal.setTime(motherRecord.getLmpDate());
									cal.add(Calendar.DAY_OF_WEEK, callConfiguration.getTermRange() * 30);

									callEndDate = new Timestamp(cal.getTime().getTime());
								}

								if (callEndDate != null)
									outboundCallsSet.setCallDateTo(callEndDate);

								Calendar cal = Calendar.getInstance();
								cal.setTime(callEndDate);
								cal.add(Calendar.DAY_OF_WEEK, 1);
								callStartDate = new Timestamp(cal.getTime().getTime());

							} else if (childRecord != null && childRecord.getDob() != null) {
								if (callConfiguration.getBaseLine().equalsIgnoreCase("LMP"))
									continue;

								Timestamp callEndDate = null;

								if (callStartDate == null)
									callStartDate = childRecord.getDob();
								outboundCallsSet.setCallDateFrom(callStartDate);

								if (callConfiguration.getConfigTerms() != null
										&& callConfiguration.getConfigTerms().equalsIgnoreCase("days")) {

									Calendar cal = Calendar.getInstance();
									cal.setTime(childRecord.getDob());
									cal.add(Calendar.DAY_OF_WEEK, callConfiguration.getTermRange());

									callEndDate = new Timestamp(cal.getTime().getTime());

								} else if (callConfiguration.getConfigTerms() != null
										&& callConfiguration.getConfigTerms().equalsIgnoreCase("months")) {

									Calendar cal = Calendar.getInstance();
									cal.setTime(childRecord.getDob());
									cal.add(Calendar.DAY_OF_WEEK, callConfiguration.getTermRange() * 30);

									callEndDate = new Timestamp(cal.getTime().getTime());
								}

								if (callEndDate != null)
									outboundCallsSet.setCallDateTo(callEndDate);

								Calendar cal = Calendar.getInstance();
								cal.setTime(callEndDate);
								cal.add(Calendar.DAY_OF_WEEK, 1);
								callStartDate = new Timestamp(cal.getTime().getTime());
							}

							if (outboundCalls.getPhoneNumberType() != null)
								outboundCallsSet.setPhoneNumberType(outboundCalls.getPhoneNumberType());

							// from request
							if (outboundCalls.getBeneficiaryRegId() != null)
								outboundCallsSet.setBeneficiaryRegId(outboundCalls.getBeneficiaryRegId());
							if (outboundCalls.getMotherId() != null)
								outboundCallsSet.setMotherId(outboundCalls.getMotherId());
							if (outboundCalls.getChildId() != null)
								outboundCallsSet.setChildId(outboundCalls.getChildId());
							if (outboundCalls.getPsmId() != null)
								outboundCallsSet.setPsmId(outboundCalls.getPsmId());
							if (outboundCalls.getCreatedBy() != null)
								outboundCallsSet.setCreatedBy(outboundCalls.getCreatedBy());
							if (outboundCalls.getIsHighRisk() != null)
								outboundCallsSet.setIsHighRisk(outboundCalls.getIsHighRisk());
							if (outboundCalls.getIsHrni() != null)
								outboundCallsSet.setIsHrni(outboundCalls.getIsHrni());

							outboundCallsSet.setCallAttemptNo(0);
							outboundCallsSet.setCallStatus("open");
							outboundCallsSet.setAllocationStatus("unallocated");

							// from conf
							if (callConfiguration.getCallType() != null)
								outboundCallsSet.setEcdCallType(callConfiguration.getCallType());
							if (callConfiguration.getDisplayName() != null)
								outboundCallsSet.setDisplayEcdCallType(callConfiguration.getDisplayName());

							outboundCallsList.add(outboundCallsSet);
						}
					}
					outboundCallsRepo.saveAll(outboundCallsList);

				}

			}

			Map<String, Object> responseMap = new HashMap<>();
			responseMap.put("response", "records successfully inserted");
			return new Gson().toJson(responseMap);
		} catch (Exception e) {
			throw new ECDException(e);
		}
	}

	public ResponseEligibleCallRecordsDTO getEligibleRecordsLanguageInfo(int psmId, String phoneNoType, String recordType, String fDate,
			String tDate, String preferredLanguage) {
		try {
			if (preferredLanguage == null || preferredLanguage.trim().isEmpty()) {
				throw new InvalidRequestException("preferred language is required");
			}
			Timestamp tempFDateStamp = null;
			Timestamp tempTDateStamp = null;
			if (fDate != null && tDate != null) {
				tempFDateStamp = getTimestampFromString(fDate.split(Constants.T)[0].concat(Constants.TIME_FORMAT_START_TIME));
				tempTDateStamp = getTimestampFromString(tDate.split(Constants.T)[0].concat(Constants.TIME_FORMAT_END_TIME));
			} else
				throw new InvalidRequestException(Constants.FROM_DATE_TO_DATE_IS_NULL);

			ResponseEligibleCallRecordsDTO responseEligibleCallRecordsDTO = new ResponseEligibleCallRecordsDTO();
			int totalLowRisk = 0;
			if (recordType != null && recordType.equalsIgnoreCase(Constants.MOTHER)) {
				totalLowRisk = outboundCallsRepo.getMotherUnAllocatedCountLRByLanguage(Constants.UNALLOCATED, psmId, tempFDateStamp,
						tempTDateStamp, phoneNoType, preferredLanguage);
			} else if (recordType != null && recordType.equalsIgnoreCase(Constants.CHILD)) {
				totalLowRisk = outboundCallsRepo.getChildUnAllocatedCountLRByLanguage(Constants.UNALLOCATED, psmId, tempFDateStamp,
						tempTDateStamp, phoneNoType, preferredLanguage);
			}
			responseEligibleCallRecordsDTO.setTotalLowRiskRecord(totalLowRisk);
			return responseEligibleCallRecordsDTO;
		} catch (Exception e) {
			throw new ECDException(e);
		}

	}
}
