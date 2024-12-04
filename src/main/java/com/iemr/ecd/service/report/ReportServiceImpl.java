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
package com.iemr.ecd.service.report;

import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iemr.ecd.dto.report.BenCallDetail;
import com.iemr.ecd.model.excel.Criteria;
import com.iemr.ecd.model.excel.ExcelHelper;
import com.iemr.ecd.repository.report.EcdReportRepo;
import com.iemr.ecd.utils.advice.exception_handler.ECDException;
import com.iemr.ecd.utils.mapper.InputMapper;

@Service
public class ReportServiceImpl implements ReportService {
	private static final String PERIOD_OF_OCCURENCE = "Period of occurence";
	private static final String REASON_OF_INCIDENT = "Reason of incident";
	private static final String REGISTRATION_NO = "Registration No";
	private static final String MOTHER_NAME = "Mother Name";
	private static final String PHONENO_OF_BENEFICIARY = "PhoneNo of Beneficiary";
	private static final String CALL_DATE = "Call Date";
	private static final String HUSBAND_NAME = "Husband Name";
	private static final String EDD_DOB = "EDD/DOB";
	private static final String RCH_ID = "RCH Id";
	private static final String BENEFICIARY_NAME = "Beneficiary Name";
	private static final String BLOCK = "Block";
	private static final String PHONE_NO = "Phone No";
	
	
	@Autowired
	private EcdReportRepo ecdReportRepo;

	@Override
	public ByteArrayInputStream getCallDetailsReport(String request, String filename) throws Exception {
		String[] headers = { "SNO", "User ID", "Call Time", "District Name", "Health Block Name", "PHC Name",
				"Sub Centre Name", "Village Name", "Address", PHONE_NO, "Mobile Of", "Alternative No",
				HUSBAND_NAME, MOTHER_NAME, "LMP", EDD_DOB, REGISTRATION_NO, "Call Category", "Call Status",
				"Is Verified(Yes/No)", "Is HRP", "HRP Indicators", "Call Duration", "Remarks", "Call Answered",
				"Is Call Disconnected", "Is Wrong Number" };
		String[] criteriaColumns = { "Start_Date", "End_Date", "Role", "Agent_ID" };
		ByteArrayInputStream response = null;
		List<Object[]> result = null;
		try {
			BenCallDetail callDetailReport = InputMapper.gson().fromJson(request, BenCallDetail.class);
			Criteria c = new Criteria();
			Timestamp startDate = null;
			Timestamp endDate = null;
			if (callDetailReport.getStartDate() != null) {
				startDate = getTimestampFromString(
						callDetailReport.getStartDate().split("T")[0].concat("T00:00:00+05:30"));
				c.setStart_Date(startDate.toString());
			}
			if (callDetailReport.getEndDate() != null) {
				endDate = getTimestampFromString(callDetailReport.getEndDate().split("T")[0].concat("T23:59:59+05:30"));
				c.setEnd_Date(endDate.toString());
			}
			if (callDetailReport.getAgentId() != null)
				c.setAgent_Id(callDetailReport.getAgentId().toString());
			if (callDetailReport.getRole() != null)
				c.setRole(callDetailReport.getRole());
			result = ecdReportRepo.getCallDetailsReport(startDate, endDate, callDetailReport.getAgentId(),
					callDetailReport.getPsmId());

			if (result != null && !result.isEmpty())
				response = ExcelHelper.tutorialsToExcel(headers, result, c, criteriaColumns);
			else
				return null;
		} catch (ECDException e) {
			throw new ECDException(e.getMessage());
		}
		return response;
	}

	@Override
	public ByteArrayInputStream getCallSummaryReport(String request, String filename) throws Exception {
		String[] headers = { "SNO", "Date", "Total Calls Made", "Answered", "Verified",
				"Number Busy/No reply/Out of Reach/Switched off/Not Connnected",
				"Invalid Number/Out of order/Wrong number/Other Numbers", "ECD-1", "ECD-2", "ECD-3", "ECD-4", "ECD-5",
				"ECD-6", "ECD-7", "ECD-8", "ECD-9", "ECD-10", "ECD-11", "ECD-12", "ECD-13", "ECD-14", "ECD-15",
				"ECD-16", "ECD-17", "ECD-18", "ECD-19", "ECD-20", "ECD-21",
				"Miscarriage/Abortion/still birth/Baby died etc" };
		String[] criteriaColumns = { "Start_Date", "End_Date", "Role", "Agent_ID" };
		ByteArrayInputStream response = null;
		List<Object[]> result = null;
		try {
			BenCallDetail callSummaryReport = InputMapper.gson().fromJson(request, BenCallDetail.class);
			Criteria c = new Criteria();
			Timestamp startDate = null;
			Timestamp endDate = null;
			if (callSummaryReport.getStartDate() != null) {
				startDate = getTimestampFromString(
						callSummaryReport.getStartDate().split("T")[0].concat("T00:00:00+05:30"));
				c.setStart_Date(startDate.toString());
			}
			if (callSummaryReport.getEndDate() != null) {
				endDate = getTimestampFromString(
						callSummaryReport.getEndDate().split("T")[0].concat("T23:59:59+05:30"));
				c.setEnd_Date(endDate.toString());
			}
			if (callSummaryReport.getAgentId() != null)
				c.setAgent_Id(callSummaryReport.getAgentId().toString());
			if (callSummaryReport.getRole() != null)
				c.setRole(callSummaryReport.getRole());
			result = ecdReportRepo.getCallSummaryReport(startDate, endDate, callSummaryReport.getAgentId(),
					callSummaryReport.getPsmId(), callSummaryReport.getRole());

			if (result != null && !result.isEmpty())
				response = ExcelHelper.tutorialsToExcel(headers, result, c, criteriaColumns);
			else
				return null;
		} catch (ECDException e) {
			throw new ECDException(e.getMessage());
		}
		return response;
	}

	@Override
	public ByteArrayInputStream getCumulativeDistrictReport(String request, String filename) throws Exception {
		String[] headers = { "SNO", "District", "Calls Made", "Answered Call", "Verified Call", "ECD-1", "ECD-2",
				"ECD-3", "ECD-4", "ECD-5", "ECD-6", "ECD-7", "ECD-8", "ECD-9", "ECD-10", "ECD-11", "ECD-12", "ECD-13",
				"ECD-14", "ECD-15", "ECD-16", "ECD-17", "ECD-18", "ECD-19", "ECD-20", "ECD-21" };
		String[] criteriaColumns = { "Start_Date", "End_Date", "Role", "Agent_ID" };
		ByteArrayInputStream response = null;
		List<Object[]> result = null;
		try {
			BenCallDetail cumulativeDistrictReport = InputMapper.gson().fromJson(request, BenCallDetail.class);
			Criteria c = new Criteria();
			Timestamp startDate = null;
			Timestamp endDate = null;
			if (cumulativeDistrictReport.getStartDate() != null) {
				startDate = getTimestampFromString(
						cumulativeDistrictReport.getStartDate().split("T")[0].concat("T00:00:00+05:30"));
				c.setStart_Date(startDate.toString());
			}

			if (cumulativeDistrictReport.getEndDate() != null) {
				endDate = getTimestampFromString(
						cumulativeDistrictReport.getEndDate().split("T")[0].concat("T23:59:59+05:30"));
				c.setEnd_Date(endDate.toString());
			}

			if (cumulativeDistrictReport.getAgentId() != null)
				c.setAgent_Id(cumulativeDistrictReport.getAgentId().toString());
			if (cumulativeDistrictReport.getRole() != null)
				c.setRole(cumulativeDistrictReport.getRole());
			result = ecdReportRepo.getCumulativeDistrictReport(startDate, endDate,
					cumulativeDistrictReport.getAgentId(), cumulativeDistrictReport.getPsmId(),
					cumulativeDistrictReport.getRole());

			if (result != null && !result.isEmpty())
				response = ExcelHelper.tutorialsToExcel(headers, result, c, criteriaColumns);
			else
				return null;
		} catch (ECDException e) {
			throw new ECDException(e.getMessage());
		}
		return response;
	}

	@Override
	public ByteArrayInputStream getBeneficiarywisefollowupdetails(String request, String filename) throws Exception {
		String[] headers = { "SNO", "RCH NO", PHONE_NO, BENEFICIARY_NAME, "District", BLOCK, "LMP", EDD_DOB,
				"ECD-1", "ECD-2", "ECD-3", "ECD-4", "ECD-5", "ECD-6", "ECD-7", "ECD-8", "ECD-9", "ECD-10", "ECD-11",
				"ECD-12", "ECD-13", "ECD-14", "ECD-15", "ECD-16",
				"Total number of services received by Beneficiaries" };
		String[] criteriaColumns = { "Start_Date", "End_Date", "Role", "Agent_ID" };
		ByteArrayInputStream response = null;
		List<Object[]> result = null;
		try {
			BenCallDetail beneficiarywisefollowupdetails = InputMapper.gson().fromJson(request, BenCallDetail.class);
			Criteria c = new Criteria();
			Timestamp startDate = null;
			Timestamp endDate = null;
			if (beneficiarywisefollowupdetails.getStartDate() != null) {
				startDate = getTimestampFromString(
						beneficiarywisefollowupdetails.getStartDate().split("T")[0].concat("T00:00:00+05:30"));
				c.setStart_Date(startDate.toString());
			}
			if (beneficiarywisefollowupdetails.getEndDate() != null) {
				endDate = getTimestampFromString(
						beneficiarywisefollowupdetails.getEndDate().split("T")[0].concat("T23:59:59+05:30"));
				c.setEnd_Date(endDate.toString());
			}
			if (beneficiarywisefollowupdetails.getAgentId() != null)
				c.setAgent_Id(beneficiarywisefollowupdetails.getAgentId().toString());
			if (beneficiarywisefollowupdetails.getRole() != null)
				c.setRole(beneficiarywisefollowupdetails.getRole());
			result = ecdReportRepo.getBeneficiarywisefollowupdetails(startDate, endDate,
					beneficiarywisefollowupdetails.getAgentId(), beneficiarywisefollowupdetails.getPsmId());

			if (result != null && !result.isEmpty())
				response = ExcelHelper.tutorialsToExcel(headers, result, c, criteriaColumns);
			else
				return null;
		} catch (ECDException e) {
			throw new ECDException(e.getMessage());
		}
		return response;
	}

	@Override
	public ByteArrayInputStream getCallDetailReportUnique(String request, String filename) throws Exception {
		String[] headers = { "Sno", "User ID", "Call Time", "Mother District", "Health Block Name", "PHC Name",
				"Sub Center Name", "Village Name", "Address", PHONE_NO, "Phone No Of Whom", "Alternative No",
				HUSBAND_NAME, MOTHER_NAME, "LMP Date", EDD_DOB, "RegistrationNO", "CallCategory", "Callstatus",
				"IsVerified", "Is HRP", "HRP indicators", "CallDuration", "Remarks", "Total", "Call Answered",
				"Is Call Disconnected", "Is Wrong Number" };
		String[] criteriaColumns = { "Start_Date", "End_Date", "Agent_ID" };
		ByteArrayInputStream response = null;
		List<Object[]> result = null;
		try {
			BenCallDetail benCallDetail = InputMapper.gson().fromJson(request, BenCallDetail.class);
			Criteria c = new Criteria();

			Timestamp startDate = null;
			Timestamp endDate = null;
			if (benCallDetail.getStartDate() != null) {
				startDate = getTimestampFromString(
						benCallDetail.getStartDate().split("T")[0].concat("T00:00:00+05:30"));
				c.setStart_Date(startDate.toString());
			}
			if (benCallDetail.getEndDate() != null) {
				endDate = getTimestampFromString(benCallDetail.getEndDate().split("T")[0].concat("T23:59:59+05:30"));
				c.setEnd_Date(endDate.toString());
			}
			if (benCallDetail.getAgentId() != null)
				c.setAgent_Id(benCallDetail.getAgentId().toString());
			if (benCallDetail.getRole() != null)
				c.setRole(benCallDetail.getRole());

			result = ecdReportRepo.getCallDetailReportUnique(startDate, endDate, benCallDetail.getAgentId(),
					benCallDetail.getPsmId());

			if (result != null && !result.isEmpty())
				response = ExcelHelper.tutorialsToExcel(headers, result, c, criteriaColumns);
			else
				return null;
		} catch (ECDException e) {
			throw new ECDException(e.getMessage());
		}
		return response;
	}

	@Override
	public ByteArrayInputStream getBirthDefectReport(String request, String filename) throws Exception {
		String[] headers = { "SNO", CALL_DATE, RCH_ID, PHONE_NO, BENEFICIARY_NAME, "District Name",
				"Block Name", "Date Of Birth(Child)", "Remarks" };
		String[] criteriaColumns = { "Start_Date", "End_Date", "Role", "Agent_ID" };
		ByteArrayInputStream response = null;
		List<Object[]> result = null;
		try {
			BenCallDetail birthDefectReport = InputMapper.gson().fromJson(request, BenCallDetail.class);
			Criteria c = new Criteria();
			Timestamp startDate = null;
			Timestamp endDate = null;
			if (birthDefectReport.getStartDate() != null) {
				startDate = getTimestampFromString(
						birthDefectReport.getStartDate().split("T")[0].concat("T00:00:00+05:30"));
				c.setStart_Date(startDate.toString());
			}
			if (birthDefectReport.getEndDate() != null) {
				endDate = getTimestampFromString(
						birthDefectReport.getEndDate().split("T")[0].concat("T23:59:59+05:30"));
				c.setEnd_Date(endDate.toString());
			}
			if (birthDefectReport.getAgentId() != null)
				c.setAgent_Id(birthDefectReport.getAgentId().toString());
			if (birthDefectReport.getRole() != null)
				c.setRole(birthDefectReport.getRole());
			result = ecdReportRepo.getBirthDefectReport(startDate, endDate, birthDefectReport.getAgentId(),
					birthDefectReport.getPsmId());

			if (result != null && !result.isEmpty())
				response = ExcelHelper.tutorialsToExcel(headers, result, c, criteriaColumns);
			else
				return null;
		} catch (ECDException e) {
			throw new ECDException(e.getMessage());
		}
		return response;
	}

	@Override
	public ByteArrayInputStream getAashaHomeVisitGapReport(String request, String filename) throws Exception {
		String[] headers = { "SNO", CALL_DATE, RCH_ID, PHONE_NO, BENEFICIARY_NAME, "District Name",
				"Block Name", "LMP", "EDD", "Remarks" };
		String[] criteriaColumns = { "Start_Date", "End_Date", "Role", "Agent_ID" };
		ByteArrayInputStream response = null;
		List<Object[]> result = null;
		try {
			BenCallDetail ashaHomeVisitGapReport = InputMapper.gson().fromJson(request, BenCallDetail.class);
			Criteria c = new Criteria();
			Timestamp startDate = null;
			Timestamp endDate = null;
			if (ashaHomeVisitGapReport.getStartDate() != null) {
				startDate = getTimestampFromString(
						ashaHomeVisitGapReport.getStartDate().split("T")[0].concat("T00:00:00+05:30"));
				c.setStart_Date(startDate.toString());
			}
			if (ashaHomeVisitGapReport.getEndDate() != null) {
				endDate = getTimestampFromString(
						ashaHomeVisitGapReport.getEndDate().split("T")[0].concat("T23:59:59+05:30"));
				c.setEnd_Date(endDate.toString());
			}
			if (ashaHomeVisitGapReport.getAgentId() != null)
				c.setAgent_Id(ashaHomeVisitGapReport.getAgentId().toString());
			if (ashaHomeVisitGapReport.getRole() != null)
				c.setRole(ashaHomeVisitGapReport.getRole());
			result = ecdReportRepo.getAashaHomeVisitGapReport(startDate, endDate, ashaHomeVisitGapReport.getAgentId(),
					ashaHomeVisitGapReport.getPsmId());

			if (result != null && !result.isEmpty())
				response = ExcelHelper.tutorialsToExcel(headers, result, c, criteriaColumns);
			else
				return null;
		} catch (ECDException e) {
			throw new ECDException(e.getMessage());
		}
		return response;
	}

	@Override
	public ByteArrayInputStream getCalciumIFATabNonadherenceReport(String request, String filename) throws Exception {
		String[] headers = { "SNO", CALL_DATE, RCH_ID, PHONE_NO, BENEFICIARY_NAME, "District Name",
				"Block Name", "LMP", "EDD", "Remarks" };
		String[] criteriaColumns = { "Start_Date", "End_Date", "Role", "Agent_ID" };
		ByteArrayInputStream response = null;
		List<Object[]> result = null;
		try {
			BenCallDetail calciumIFATabNonadherenceReport = InputMapper.gson().fromJson(request, BenCallDetail.class);
			Criteria c = new Criteria();
			Timestamp startDate = null;
			Timestamp endDate = null;
			if (calciumIFATabNonadherenceReport.getStartDate() != null) {
				startDate = getTimestampFromString(
						calciumIFATabNonadherenceReport.getStartDate().split("T")[0].concat("T00:00:00+05:30"));
				c.setStart_Date(startDate.toString());
			}
			if (calciumIFATabNonadherenceReport.getEndDate() != null) {
				endDate = getTimestampFromString(
						calciumIFATabNonadherenceReport.getEndDate().split("T")[0].concat("T23:59:59+05:30"));
				c.setEnd_Date(endDate.toString());
			}
			if (calciumIFATabNonadherenceReport.getAgentId() != null)
				c.setAgent_Id(calciumIFATabNonadherenceReport.getAgentId().toString());
			if (calciumIFATabNonadherenceReport.getRole() != null)
				c.setRole(calciumIFATabNonadherenceReport.getRole());
			result = ecdReportRepo.getCalciumIFATabNonadherenceReport(startDate, endDate,
					calciumIFATabNonadherenceReport.getAgentId(), calciumIFATabNonadherenceReport.getPsmId());

			if (result != null && !result.isEmpty())
				response = ExcelHelper.tutorialsToExcel(headers, result, c, criteriaColumns);
			else
				return null;
		} catch (ECDException e) {
			throw new ECDException(e.getMessage());
		}
		return response;
	}

	@Override
	public ByteArrayInputStream getAbsenceInVHSNDReport(String request, String filename) throws Exception {
		String[] headers = { "SNO", CALL_DATE, RCH_ID, PHONE_NO, BENEFICIARY_NAME, "District Name",
				"Block Name", "LMP", "EDD", "Reason Of Not Attain" };
		String[] criteriaColumns = { "Start_Date", "End_Date", "Role", "Agent_ID" };
		ByteArrayInputStream response = null;
		List<Object[]> result = null;
		try {
			BenCallDetail absenceInVHSNDReport = InputMapper.gson().fromJson(request, BenCallDetail.class);
			Criteria c = new Criteria();
			Timestamp startDate = null;
			Timestamp endDate = null;
			if (absenceInVHSNDReport.getStartDate() != null) {
				startDate = getTimestampFromString(
						absenceInVHSNDReport.getStartDate().split("T")[0].concat("T00:00:00+05:30"));
				c.setStart_Date(startDate.toString());
			}
			if (absenceInVHSNDReport.getEndDate() != null) {
				endDate = getTimestampFromString(
						absenceInVHSNDReport.getEndDate().split("T")[0].concat("T23:59:59+05:30"));
				c.setEnd_Date(endDate.toString());
			}
			if (absenceInVHSNDReport.getAgentId() != null)
				c.setAgent_Id(absenceInVHSNDReport.getAgentId().toString());
			if (absenceInVHSNDReport.getRole() != null)
				c.setRole(absenceInVHSNDReport.getRole());
			result = ecdReportRepo.getAbsenceInVHSNDReport(startDate, endDate, absenceInVHSNDReport.getAgentId(),
					absenceInVHSNDReport.getPsmId());

			if (result != null && !result.isEmpty())
				response = ExcelHelper.tutorialsToExcel(headers, result, c, criteriaColumns);
			else
				return null;
		} catch (ECDException e) {
			throw new ECDException(e.getMessage());
		}
		return response;
	}

	@Override
	public ByteArrayInputStream getVaccineDropOutIdentifiedReport(String request, String filename) throws Exception {
		String[] headers = { "SNO", CALL_DATE, RCH_ID, PHONE_NO, MOTHER_NAME, "District Name", "Block Name",
				"LMP", "DOB(Child)", "Remarks" };
		String[] criteriaColumns = { "Start_Date", "End_Date", "Role", "Agent_ID" };
		ByteArrayInputStream response = null;
		List<Object[]> result = null;
		try {
			BenCallDetail vaccineDropOutIdentifiedReport = InputMapper.gson().fromJson(request, BenCallDetail.class);
			Criteria c = new Criteria();
			Timestamp startDate = null;
			Timestamp endDate = null;
			if (vaccineDropOutIdentifiedReport.getStartDate() != null) {
				startDate = getTimestampFromString(
						vaccineDropOutIdentifiedReport.getStartDate().split("T")[0].concat("T00:00:00+05:30"));
				c.setStart_Date(startDate.toString());
			}
			if (vaccineDropOutIdentifiedReport.getEndDate() != null) {
				endDate = getTimestampFromString(
						vaccineDropOutIdentifiedReport.getEndDate().split("T")[0].concat("T23:59:59+05:30"));
				c.setEnd_Date(endDate.toString());
			}
			if (vaccineDropOutIdentifiedReport.getAgentId() != null)
				c.setAgent_Id(vaccineDropOutIdentifiedReport.getAgentId().toString());
			if (vaccineDropOutIdentifiedReport.getRole() != null)
				c.setRole(vaccineDropOutIdentifiedReport.getRole());
			result = ecdReportRepo.getVaccineDropOutIdentifiedReport(startDate, endDate,
					vaccineDropOutIdentifiedReport.getAgentId(), vaccineDropOutIdentifiedReport.getPsmId());

			if (result != null && !result.isEmpty())
				response = ExcelHelper.tutorialsToExcel(headers, result, c, criteriaColumns);
			else
				return null;
		} catch (ECDException e) {
			throw new ECDException(e.getMessage());
		}
		return response;
	}

	@Override
	public ByteArrayInputStream getVaccineLeftOutIdentifiedReport(String request, String filename) throws Exception {
		String[] headers = { "SNO", CALL_DATE, RCH_ID, PHONE_NO, BENEFICIARY_NAME, "District Name",
				"Block Name", "LMP", "DOB(Child)", "Remarks" };
		String[] criteriaColumns = { "Start_Date", "End_Date", "Role", "Agent_ID" };
		ByteArrayInputStream response = null;
		List<Object[]> result = null;
		try {
			BenCallDetail vaccineLeftOutIdentifiedReport = InputMapper.gson().fromJson(request, BenCallDetail.class);
			Criteria c = new Criteria();
			Timestamp startDate = null;
			Timestamp endDate = null;
			if (vaccineLeftOutIdentifiedReport.getStartDate() != null) {
				startDate = getTimestampFromString(
						vaccineLeftOutIdentifiedReport.getStartDate().split("T")[0].concat("T00:00:00+05:30"));
				c.setStart_Date(startDate.toString());
			}
			if (vaccineLeftOutIdentifiedReport.getEndDate() != null) {
				endDate = getTimestampFromString(
						vaccineLeftOutIdentifiedReport.getEndDate().split("T")[0].concat("T23:59:59+05:30"));
				c.setEnd_Date(endDate.toString());
			}
			if (vaccineLeftOutIdentifiedReport.getAgentId() != null)
				c.setAgent_Id(vaccineLeftOutIdentifiedReport.getAgentId().toString());
			if (vaccineLeftOutIdentifiedReport.getRole() != null)
				c.setRole(vaccineLeftOutIdentifiedReport.getRole());
			result = ecdReportRepo.getVaccineLeftOutIdentifiedReport(startDate, endDate,
					vaccineLeftOutIdentifiedReport.getAgentId(), vaccineLeftOutIdentifiedReport.getPsmId());

			if (result != null && !result.isEmpty())
				response = ExcelHelper.tutorialsToExcel(headers, result, c, criteriaColumns);
			else
				return null;
		} catch (ECDException e) {
			throw new ECDException(e.getMessage());
		}
		return response;
	}

	@Override
	public ByteArrayInputStream getDevelopmentalDelayReport(String request, String filename) throws Exception {
		String[] headers = { "SNO", CALL_DATE, RCH_ID, PHONE_NO, BENEFICIARY_NAME, "District Name",
				"Block Name", "LMP", "Date of birth (Child)", "Developmental delay(Findings)" };
		String[] criteriaColumns = { "Start_Date", "End_Date", "Role", "Agent_ID" };
		ByteArrayInputStream response = null;
		List<Object[]> result = null;
		try {
			BenCallDetail developmentalDelayReport = InputMapper.gson().fromJson(request, BenCallDetail.class);
			Criteria c = new Criteria();
			Timestamp startDate = null;
			Timestamp endDate = null;
			if (developmentalDelayReport.getStartDate() != null) {
				startDate = getTimestampFromString(
						developmentalDelayReport.getStartDate().split("T")[0].concat("T00:00:00+05:30"));
				c.setStart_Date(startDate.toString());
			}
			if (developmentalDelayReport.getEndDate() != null) {
				endDate = getTimestampFromString(
						developmentalDelayReport.getEndDate().split("T")[0].concat("T23:59:59+05:30"));
				c.setEnd_Date(endDate.toString());
			}
			if (developmentalDelayReport.getAgentId() != null)
				c.setAgent_Id(developmentalDelayReport.getAgentId().toString());
			if (developmentalDelayReport.getRole() != null)
				c.setRole(developmentalDelayReport.getRole());
			result = ecdReportRepo.getDevelopmentalDelayReport(startDate, endDate,
					developmentalDelayReport.getAgentId(), developmentalDelayReport.getPsmId());

			if (result != null && !result.isEmpty())
				response = ExcelHelper.tutorialsToExcel(headers, result, c, criteriaColumns);
			else
				return null;
		} catch (ECDException e) {
			throw new ECDException(e.getMessage());
		}
		return response;
	}

	@Override
	public ByteArrayInputStream getAbortionReport(String request, String filename) throws Exception {

		String[] headers = { "SNO", CALL_DATE, RCH_ID, PHONE_NO, BENEFICIARY_NAME, 
				"District Name", "Block Name", "LMP", "EDD", REASON_OF_INCIDENT, PERIOD_OF_OCCURENCE };

		String[] criteriaColumns = { "Start_Date", "End_Date", "Role", "Agent_ID" };
		ByteArrayInputStream response = null;
		List<Object[]> result = null;
		try {
			BenCallDetail abortionReport = InputMapper.gson().fromJson(request, BenCallDetail.class);
			Criteria c = new Criteria();
			Timestamp startDate = null;
			Timestamp endDate = null;
			
			if (abortionReport.getStartDate() != null) {
				startDate = getTimestampFromString(
						abortionReport.getStartDate().split("T")[0].concat("T00:00:00+05:30"));
				c.setStart_Date(startDate.toString());
			}
			if (abortionReport.getEndDate() != null) {
				endDate = getTimestampFromString(abortionReport.getEndDate().split("T")[0].concat("T23:59:59+05:30"));
				c.setEnd_Date(endDate.toString());
			}
			if (abortionReport.getAgentId() != null)
				c.setAgent_Id(abortionReport.getAgentId().toString());
			if (abortionReport.getRole() != null)
				c.setRole(abortionReport.getRole());
			
			result = ecdReportRepo.getAbortionReport(startDate, endDate, abortionReport.getAgentId(),
						abortionReport.getPsmId(),abortionReport.getRole());

			if (result != null && !result.isEmpty())
				response = ExcelHelper.tutorialsToExcel(headers, result, c, criteriaColumns);
			else
				return null;
		} catch (ECDException e) {
			throw new ECDException(e.getMessage());
		}
		return response;
	}

	@Override
	public ByteArrayInputStream getDeliveryStatusReport(String request, String filename) throws Exception {

		String[] headers = { "SNO", CALL_DATE, RCH_ID, PHONE_NO, BENEFICIARY_NAME, "District Name", 
				"Block Name", "LMP", "EDD", "Place of Delivery", "Mode of Delivery" };

		String[] criteriaColumns = { "Start_Date", "End_Date", "Role", "Agent_ID" };
		ByteArrayInputStream response = null;
		List<Object[]> result = null;
		try {
			BenCallDetail deliveryStatusReport = InputMapper.gson().fromJson(request, BenCallDetail.class);
			Criteria c = new Criteria();
			Timestamp startDate = null;
			Timestamp endDate = null;
			if (deliveryStatusReport.getStartDate() != null) {
				startDate = getTimestampFromString(
						deliveryStatusReport.getStartDate().split("T")[0].concat("T00:00:00+05:30"));
				c.setStart_Date(startDate.toString());
			}
			if (deliveryStatusReport.getEndDate() != null) {
				endDate = getTimestampFromString(
						deliveryStatusReport.getEndDate().split("T")[0].concat("T23:59:59+05:30"));
				c.setEnd_Date(endDate.toString());
			}
			if (deliveryStatusReport.getAgentId() != null)
				c.setAgent_Id(deliveryStatusReport.getAgentId().toString());
			if (deliveryStatusReport.getRole() != null)
				c.setRole(deliveryStatusReport.getRole());
			result = ecdReportRepo.getDeliveryStatusReport(startDate, endDate, deliveryStatusReport.getAgentId(),
					deliveryStatusReport.getPsmId());

			if (result != null && !result.isEmpty())
				response = ExcelHelper.tutorialsToExcel(headers, result, c, criteriaColumns);
			else
				return null;
		} catch (ECDException e) {
			throw new ECDException(e.getMessage());
		}
		return response;
	}

	@Override
	public ByteArrayInputStream getHRPCasesIdentifiedReport(String request, String filename) throws Exception {
		String[] headers = { "SNO", CALL_DATE, "Agetname", RCH_ID, PHONE_NO, BENEFICIARY_NAME, "District Name",
				"Block Name", "LMP", "EDD", "ReasonsforHRP" };
		String[] criteriaColumns = { "Start_Date", "End_Date", "Role", "Agent_ID" };
		ByteArrayInputStream response = null;
		List<Object[]> result = null;
		try {
			BenCallDetail hRPWCasesIdentifiedReport = InputMapper.gson().fromJson(request, BenCallDetail.class);
			Criteria c = new Criteria();
			Timestamp startDate = null;
			Timestamp endDate = null;
			if (hRPWCasesIdentifiedReport.getStartDate() != null) {
				startDate = getTimestampFromString(
						hRPWCasesIdentifiedReport.getStartDate().split("T")[0].concat("T00:00:00+05:30"));
				c.setStart_Date(startDate.toString());
			}
			if (hRPWCasesIdentifiedReport.getEndDate() != null) {
				endDate = getTimestampFromString(
						hRPWCasesIdentifiedReport.getEndDate().split("T")[0].concat("T23:59:59+05:30"));
				c.setEnd_Date(endDate.toString());
			}
			if (hRPWCasesIdentifiedReport.getAgentId() != null)
				c.setAgent_Id(hRPWCasesIdentifiedReport.getAgentId().toString());
			if (hRPWCasesIdentifiedReport.getRole() != null)
				c.setRole(hRPWCasesIdentifiedReport.getRole());
			result = ecdReportRepo.getHRPWCasesIdentifiedReport(startDate, endDate,
					hRPWCasesIdentifiedReport.getAgentId(), hRPWCasesIdentifiedReport.getPsmId());

			if (result != null && !result.isEmpty())
				response = ExcelHelper.tutorialsToExcel(headers, result, c, criteriaColumns);
			else
				return null;
		} catch (ECDException e) {
			throw new ECDException(e.getMessage());
		}
		return response;
	}

	@Override
	public ByteArrayInputStream getInfantsHighRiskReport(String request, String filename) throws Exception {
		String[] headers = { "SNO", CALL_DATE, "Agetname", RCH_ID, PHONE_NO, BENEFICIARY_NAME, "District Name",
				"Block Name", "LMP", "EDD", "ReasonsforHrni" };
		String[] criteriaColumns = { "Start_Date", "End_Date", "Role", "Agent_ID" };
		ByteArrayInputStream response = null;
		List<Object[]> result = null;
		try {
			BenCallDetail infantsHighRiskReport = InputMapper.gson().fromJson(request, BenCallDetail.class);
			Criteria c = new Criteria();
			Timestamp startDate = null;
			Timestamp endDate = null;
			if (infantsHighRiskReport.getStartDate() != null) {
				startDate = getTimestampFromString(
						infantsHighRiskReport.getStartDate().split("T")[0].concat("T00:00:00+05:30"));
				c.setStart_Date(startDate.toString());
			}
			if (infantsHighRiskReport.getEndDate() != null) {
				endDate = getTimestampFromString(
						infantsHighRiskReport.getEndDate().split("T")[0].concat("T23:59:59+05:30"));
				c.setEnd_Date(endDate.toString());
			}
			if (infantsHighRiskReport.getAgentId() != null)
				c.setAgent_Id(infantsHighRiskReport.getAgentId().toString());
			if (infantsHighRiskReport.getRole() != null)
				c.setRole(infantsHighRiskReport.getRole());
			result = ecdReportRepo.getInfantsHighRiskReport(startDate, endDate, infantsHighRiskReport.getAgentId(),
					infantsHighRiskReport.getPsmId());

			if (result != null && !result.isEmpty())
				response = ExcelHelper.tutorialsToExcel(headers, result, c, criteriaColumns);
			else
				return null;
		} catch (ECDException e) {
			throw new ECDException(e.getMessage());
		}
		return response;
	}

	@Override
	public ByteArrayInputStream getMaternalDeathReport(String request, String filename) throws Exception {
		String[] headers = { "SNO", CALL_DATE, REGISTRATION_NO, PHONENO_OF_BENEFICIARY, MOTHER_NAME, "District",
				BLOCK, "LMP", "EDD", "Remarks" };
		String[] criteriaColumns = { "Start_Date", "End_Date", "Role", "Agent_ID" };
		ByteArrayInputStream response = null;
		List<Object[]> result = null;
		try {
			BenCallDetail maternalDeathReport = InputMapper.gson().fromJson(request, BenCallDetail.class);
			Criteria c = new Criteria();
			Timestamp startDate = null;
			Timestamp endDate = null;
			if (maternalDeathReport.getStartDate() != null) {
				startDate = getTimestampFromString(
						maternalDeathReport.getStartDate().split("T")[0].concat("T00:00:00+05:30"));
				c.setStart_Date(startDate.toString());
			}
			if (maternalDeathReport.getEndDate() != null) {
				endDate = getTimestampFromString(
						maternalDeathReport.getEndDate().split("T")[0].concat("T23:59:59+05:30"));
				c.setEnd_Date(endDate.toString());
			}
			if (maternalDeathReport.getAgentId() != null)
				c.setAgent_Id(maternalDeathReport.getAgentId().toString());
			if (maternalDeathReport.getRole() != null)
				c.setRole(maternalDeathReport.getRole());
			result = ecdReportRepo.getMaternalDeathReport(startDate, endDate, maternalDeathReport.getAgentId(),
					maternalDeathReport.getPsmId());

			if (result != null && !result.isEmpty())
				response = ExcelHelper.tutorialsToExcel(headers, result, c, criteriaColumns);
			else
				return null;
		} catch (ECDException e) {
			throw new ECDException(e.getMessage());
		}
		return response;
	}

	@Override
	public ByteArrayInputStream getStillBirthReport(String request, String filename) throws Exception {

		String[] headers = { "SNO", CALL_DATE, REGISTRATION_NO, PHONENO_OF_BENEFICIARY, MOTHER_NAME, 
				"District", BLOCK, "LMP", "EDD", REASON_OF_INCIDENT, PERIOD_OF_OCCURENCE };

		String[] criteriaColumns = { "Start_Date", "End_Date", "Role", "Agent_ID" };
		ByteArrayInputStream response = null;
		List<Object[]> result = null;
		try {
			BenCallDetail stillBirthReport = InputMapper.gson().fromJson(request, BenCallDetail.class);
			Criteria c = new Criteria();
			Timestamp startDate = null;
			Timestamp endDate = null;
			if (stillBirthReport.getStartDate() != null) {
				startDate = getTimestampFromString(
						stillBirthReport.getStartDate().split("T")[0].concat("T00:00:00+05:30"));
				c.setStart_Date(startDate.toString());
			}
			if (stillBirthReport.getEndDate() != null) {
				endDate = getTimestampFromString(stillBirthReport.getEndDate().split("T")[0].concat("T23:59:59+05:30"));
				c.setEnd_Date(endDate.toString());
			}
			if (stillBirthReport.getAgentId() != null)
				c.setAgent_Id(stillBirthReport.getAgentId().toString());
			if (stillBirthReport.getRole() != null)
				c.setRole(stillBirthReport.getRole());
			
			result = ecdReportRepo.getStillBirthReport(startDate, endDate, stillBirthReport.getAgentId(),
					stillBirthReport.getPsmId(),stillBirthReport.getRole());
			

			if (result != null && !result.isEmpty())
				response = ExcelHelper.tutorialsToExcel(headers, result, c, criteriaColumns);
			else
				return null;
		} catch (ECDException e) {
			throw new ECDException(e.getMessage());
		}
		return response;
	}

	@Override
	public ByteArrayInputStream getBabyDeathReport(String request, String filename) throws Exception {

		String[] headers = { "SNO", CALL_DATE, REGISTRATION_NO, PHONENO_OF_BENEFICIARY, MOTHER_NAME, 
				"District", BLOCK, "LMP", "EDD", REASON_OF_INCIDENT, PERIOD_OF_OCCURENCE };

		String[] criteriaColumns = { "Start_Date", "End_Date", "Role", "Agent_ID" };
		ByteArrayInputStream response = null;
		List<Object[]> result = null;
		try {
			BenCallDetail babyDeathReport = InputMapper.gson().fromJson(request, BenCallDetail.class);
			Criteria c = new Criteria();
			Timestamp startDate = null;
			Timestamp endDate = null;
			if (babyDeathReport.getStartDate() != null) {
				startDate = getTimestampFromString(
						babyDeathReport.getStartDate().split("T")[0].concat("T00:00:00+05:30"));
				c.setStart_Date(startDate.toString());
			}
			if (babyDeathReport.getEndDate() != null) {
				endDate = getTimestampFromString(babyDeathReport.getEndDate().split("T")[0].concat("T23:59:59+05:30"));
				c.setEnd_Date(endDate.toString());
			}
			if (babyDeathReport.getAgentId() != null)
				c.setAgent_Id(babyDeathReport.getAgentId().toString());
			if (babyDeathReport.getRole() != null)
				c.setRole(babyDeathReport.getRole());
			
			result = ecdReportRepo.getBabyDeathReport(startDate, endDate, babyDeathReport.getAgentId(),
					babyDeathReport.getPsmId(), babyDeathReport.getRole());
			if (result != null && !result.isEmpty())
				response = ExcelHelper.tutorialsToExcel(headers, result, c, criteriaColumns);
			else
				return null;
		} catch (ECDException e) {
			throw new ECDException(e.getMessage());
		}
		return response;
	}

	@Override
	public ByteArrayInputStream getNotConnectedPhonelistDiffformatReport(String request, String filename)
			throws Exception {
		String[] headers = { "SNO", "Call Time", BENEFICIARY_NAME, HUSBAND_NAME, RCH_ID, "District", "Block Name",
				PHONE_NO, "Call Status", "ANM Name", "ANM PhoneNo", "ASHA Name", "ASHA PhoneNo" };
		String[] criteriaColumns = { "Start_Date", "End_Date", "Role", "Agent_ID" };
		ByteArrayInputStream response = null;
		List<Object[]> result = null;
		try {
			BenCallDetail notConnectedPhonelistDiffformatReport = InputMapper.gson().fromJson(request,
					BenCallDetail.class);
			Criteria c = new Criteria();
			Timestamp startDate = null;
			Timestamp endDate = null;
			if (notConnectedPhonelistDiffformatReport.getStartDate() != null) {
				startDate = getTimestampFromString(
						notConnectedPhonelistDiffformatReport.getStartDate().split("T")[0].concat("T00:00:00+05:30"));
				c.setStart_Date(startDate.toString());
			}
			if (notConnectedPhonelistDiffformatReport.getEndDate() != null) {
				endDate = getTimestampFromString(
						notConnectedPhonelistDiffformatReport.getEndDate().split("T")[0].concat("T23:59:59+05:30"));
				c.setEnd_Date(endDate.toString());
			}
			if (notConnectedPhonelistDiffformatReport.getAgentId() != null)
				c.setAgent_Id(notConnectedPhonelistDiffformatReport.getAgentId().toString());
			if (notConnectedPhonelistDiffformatReport.getRole() != null)
				c.setRole(notConnectedPhonelistDiffformatReport.getRole());
			result = ecdReportRepo.getNotConnectedPhonelistDiffformatReport(startDate, endDate,
					notConnectedPhonelistDiffformatReport.getAgentId(),
					notConnectedPhonelistDiffformatReport.getPsmId());

			if (result != null && !result.isEmpty())
				response = ExcelHelper.tutorialsToExcel(headers, result, c, criteriaColumns);
			else
				return null;
		} catch (ECDException e) {
			throw new ECDException(e.getMessage());
		}
		return response;
	}

	@Override
	public ByteArrayInputStream getJSYRelatedComplaintsReport(String request, String filename) throws Exception {
		String[] headers = { "SNO", CALL_DATE, RCH_ID, PHONE_NO, BENEFICIARY_NAME, "District Name",
				"Block Name", "LMP", "EDD", "Remarks" };
		String[] criteriaColumns = { "Start_Date", "End_Date", "Role", "Agent_ID" };
		ByteArrayInputStream response = null;
		List<Object[]> result = null;
		try {
			BenCallDetail jSYRelatedComplaintsReport = InputMapper.gson().fromJson(request, BenCallDetail.class);
			Criteria c = new Criteria();
			Timestamp startDate = null;
			Timestamp endDate = null;
			if (jSYRelatedComplaintsReport.getStartDate() != null) {
				startDate = getTimestampFromString(
						jSYRelatedComplaintsReport.getStartDate().split("T")[0].concat("T00:00:00+05:30"));
				c.setStart_Date(startDate.toString());
			}
			if (jSYRelatedComplaintsReport.getEndDate() != null) {
				endDate = getTimestampFromString(
						jSYRelatedComplaintsReport.getEndDate().split("T")[0].concat("T23:59:59+05:30"));
				c.setEnd_Date(endDate.toString());
			}
			if (jSYRelatedComplaintsReport.getAgentId() != null)
				c.setAgent_Id(jSYRelatedComplaintsReport.getAgentId().toString());
			if (jSYRelatedComplaintsReport.getRole() != null)
				c.setRole(jSYRelatedComplaintsReport.getRole());
			result = ecdReportRepo.getJSYRelatedComplaintsReport(startDate, endDate,
					jSYRelatedComplaintsReport.getAgentId(), jSYRelatedComplaintsReport.getPsmId());

			if (result != null && !result.isEmpty())
				response = ExcelHelper.tutorialsToExcel(headers, result, c, criteriaColumns);
			else
				return null;
		} catch (ECDException e) {
			throw new ECDException(e.getMessage());
		}
		return response;
	}

	@Override
	public ByteArrayInputStream getMiscarriageReport(String request, String filename) throws Exception {

		String[] headers = { "SNO", CALL_DATE, RCH_ID, PHONE_NO, BENEFICIARY_NAME, 
				"District Name", "Block Name", "LMP", "EDD", "Causes of Miscarriage", PERIOD_OF_OCCURENCE};

		String[] criteriaColumns = { "Start_Date", "End_Date", "Role", "Agent_ID" };
		ByteArrayInputStream response = null;
		List<Object[]> result = null;
		try {
			BenCallDetail miscarriageReport = InputMapper.gson().fromJson(request, BenCallDetail.class);
			Criteria c = new Criteria();
			Timestamp startDate = null;
			Timestamp endDate = null;
			if (miscarriageReport.getStartDate() != null) {
				startDate = getTimestampFromString(
						miscarriageReport.getStartDate().split("T")[0].concat("T00:00:00+05:30"));
				c.setStart_Date(startDate.toString());
			}
			if (miscarriageReport.getEndDate() != null) {
				endDate = getTimestampFromString(
						miscarriageReport.getEndDate().split("T")[0].concat("T23:59:59+05:30"));
				c.setEnd_Date(endDate.toString());
			}
			if (miscarriageReport.getAgentId() != null)
				c.setAgent_Id(miscarriageReport.getAgentId().toString());
			if (miscarriageReport.getRole() != null)
				c.setRole(miscarriageReport.getRole());
			
			result = ecdReportRepo.getMiscarriageReport(startDate, endDate, miscarriageReport.getAgentId(),
					miscarriageReport.getPsmId(), miscarriageReport.getRole());

			if (result != null && !result.isEmpty())
				response = ExcelHelper.tutorialsToExcel(headers, result, c, criteriaColumns);
			else
				return null;
		} catch (ECDException e) {
			throw new ECDException(e.getMessage());
		}
		return response;
	}

	private Timestamp getTimestampFromString(String date) throws ParseException {

		DateTimeFormatter ISO_DATE_TIME = DateTimeFormatter.ISO_DATE_TIME;
		LocalDateTime localDateTime = LocalDateTime.from(ISO_DATE_TIME.parse(date));

		Timestamp timestamp = Timestamp.valueOf(localDateTime);

		return timestamp;

	}

}
