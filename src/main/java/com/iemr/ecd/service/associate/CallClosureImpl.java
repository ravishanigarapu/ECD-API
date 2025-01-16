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
package com.iemr.ecd.service.associate;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.iemr.ecd.dao.CallConfiguration;
import com.iemr.ecd.dao.associate.Bencall;
import com.iemr.ecd.dao.associate.ChildRecord;
import com.iemr.ecd.dao.associate.MotherRecord;
import com.iemr.ecd.dao.associate.OutboundCalls;
import com.iemr.ecd.dto.associate.CallClosureDTO;
import com.iemr.ecd.repo.call_conf_allocation.CallConfigurationRepo;
import com.iemr.ecd.repo.call_conf_allocation.ChildRecordRepo;
import com.iemr.ecd.repo.call_conf_allocation.MotherRecordRepo;
import com.iemr.ecd.repo.call_conf_allocation.OutboundCallsRepo;
import com.iemr.ecd.repository.ecd.BencallRepo;
import com.iemr.ecd.utils.advice.exception_handler.ECDException;
import com.iemr.ecd.utils.advice.exception_handler.InvalidRequestException;
import com.iemr.ecd.utils.constants.Constants;

import io.netty.util.internal.StringUtil;
import jakarta.transaction.Transactional;

@Service
public class CallClosureImpl {

	Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	private BencallRepo bencallRepo;

	@Autowired
	private OutboundCallsRepo outboundCallsRepo;

	@Autowired
	private CallConfigurationRepo callConfigurationRepo;

	@Autowired
	private MotherRecordRepo motherRecordRepo;

	@Autowired
	private ChildRecordRepo childRecordRepo;

	@Transactional(rollbackOn = Exception.class)
	public String closeCall(CallClosureDTO request) {
		try {
			List<CallConfiguration> callConfigurationDetails = null;
			// Updating beneficiary call details
			Bencall obj = bencallRepo.findByBenCallId(request.getBenCallId());

			if (obj != null) {
				obj.setIsOutbound(request.getIsOutbound());
				if(null != request.getBeneficiaryRegId())
					obj.setBeneficiaryRegId(request.getBeneficiaryRegId());
				if (request.getIsFurtherCallRequired() != null)
					obj.setIsFurtherCallRequired(request.getIsFurtherCallRequired());

				if (request.getReasonForNoFurtherCalls() != null)
					obj.setReasonForNoFurtherCalls(request.getReasonForNoFurtherCalls());

				if (request.getIsCallVerified() != null) {
					obj.setIsCallVerified(request.getIsCallVerified());
				}
				
				obj.setIsCallAnswered(request.getIsCallAnswered());

				if (request.getReasonForCallNotAnswered() != null)
					obj.setReasonForCallNotAnswered(request.getReasonForCallNotAnswered());

				obj.setIsCallDisconnected(request.getIsCallDisconnected());

				if (request.getTypeOfComplaint() != null)
					obj.setTypeOfComplaint(request.getTypeOfComplaint());

				if (request.getComplaintRemarks() != null)
					obj.setComplaintRemarks(request.getComplaintRemarks());

				if (request.getCallRemarks() != null)
					obj.setCallRemarks(request.getCallRemarks());

				obj.setCallEndUserId(request.getUserId());

				if (request.getSendAdvice() != null)
					obj.setSmsAdvice(request.getSendAdvice());

				if (request.getAltPhoneNo() != null)
					obj.setSmsPhone(request.getAltPhoneNo());

				obj.setCreatedBy(request.getCreatedBy());
				obj.setModifiedBy(request.getModifiedBy());

				Timestamp callEndTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
				String callDurationValue = calculateCallDuration(obj.getCallTime(), callEndTime);
				obj.setCallEndTime(callEndTime);
				obj.setCallDuration(callDurationValue);

				if (request.getIsWrongNumber() != null)
					obj.setIsWrongNumber(request.getIsWrongNumber());
				if(null != request.getReasonForCallNotAnswered() && Constants.REASONFORCALLNOTANSWERED.contains(request.getReasonForCallNotAnswered())) {
					obj.setIsFurtherCallRequired(true);
					obj.setIsCallDisconnected(true);
				}

				bencallRepo.save(obj);
			}

			OutboundCalls callObj = outboundCallsRepo.findByObCallId(request.getObCallId());
			boolean isMaxcallsAttempted=false;
			if (callObj != null) {

				callConfigurationDetails = callConfigurationRepo.getCallConfiguration(request.getPsmId());
				CallConfiguration callConfigurationDetail = null;
				if (callConfigurationDetails != null) {
					callConfigurationDetail = callConfigurationDetails.get(0);
				}

				if (obj.getIsCallAnswered() != null && obj.getIsCallAnswered()) {
					callObj.setCallStatus("Completed");
				}

				if (obj.getIsFurtherCallRequired() != null && !obj.getIsFurtherCallRequired()) {
					callObj.setCallStatus("Completed");
				} else {
					if (obj.getIsCallDisconnected() != null && obj.getIsCallDisconnected()) {
						callObj.setCallStatus("Open");
					} else {
						callObj.setCallStatus("Completed");
						createEcdCallRecordsInOutboundCalls(request, callConfigurationDetails,
								callObj.getPhoneNumberType());
					}

				}
				
				if (request.getIsHrp() != null) {
					callObj.setIsHighRisk(request.getIsHrp());
					/*
					 * if (null != obj.getReceivedRoleName() &&
					 * (obj.getReceivedRoleName().equalsIgnoreCase(Constants.ANM) ||
					 * obj.getReceivedRoleName().equalsIgnoreCase(Constants.ASSOCIATE))) {
					 * callObj.setCallStatus(Constants.OPEN); }
					 */
				}

				if (request.getIsHrni() != null) {
					callObj.setIsHrni(request.getIsHrni());
				}

				if (request.getNextAttemptDate() != null) {
					Timestamp nextCallDate = getTimestampFromString(request.getNextAttemptDate());
					callObj.setNextCallDate(nextCallDate);
					callObj.setCallStatus(Constants.OPEN);

				} else {
					if ((callObj.getCallAttemptNo() + 1) >= callConfigurationDetail.getNoOfAttempts()) {
						isMaxcallsAttempted=true;
						callObj.setCallStatus(Constants.COMPLETED);
					}

				}
				if (obj.getReceivedRoleName() != null && obj.getReceivedRoleName().equalsIgnoreCase(Constants.ANM)
						&& request.getPreferredLanguage() != null && !request.getPreferredLanguage().isEmpty()) {
					callObj.setAllocationStatus(Constants.OPEN);
					callObj.setCallAttemptNo(0);
				} else {
					callObj.setCallAttemptNo(callObj.getCallAttemptNo() + 1);
				}
				if(null != request.getReasonForCallNotAnswered() && Constants.REASONFORCALLNOTANSWERED.contains(request.getReasonForCallNotAnswered()) && !isMaxcallsAttempted) {
					callObj.setCallStatus(Constants.OPEN);
				}

				outboundCallsRepo.save(callObj);
			} else
				throw new ECDException(
						"Outbound call record not found in DB for given ObCallId : " + request.getObCallId());

			// Updating HRP/HRNi for Upcoming Calls
			if (callObj.getEcdCallType() != null && !callObj.getEcdCallType().equalsIgnoreCase("introductory")) {
				if (callObj.getMotherId() != null && callObj.getChildId() != null && callObj.getIsHrni() != null) {
					// Child
					outboundCallsRepo.updateHRNIForUpcomingCall(callObj.getChildId(), callObj.getIsHrni());
				} else if (callObj.getMotherId() != null && callObj.getChildId() == null
						&& callObj.getIsHighRisk() != null) {
					// Mother
					outboundCallsRepo.updateHRPForUpcomingCall(callObj.getMotherId(), callObj.getIsHighRisk());
				}

			}
			if (null != obj.getIsFurtherCallRequired() && !obj.getIsFurtherCallRequired()) {
				if (callObj.getMotherId() != null && callObj.getChildId() != null) {
					try {
						outboundCallsRepo.updateIsFurtherCallRequiredForUpcomingCallForChild(callObj.getChildId(),
								obj.getIsFurtherCallRequired());
					} catch (Exception e) {
						logger.error("Failed to update isFurtherCallRequired for child: " + callObj.getChildId(), e);
						throw new ECDException("Failed to update isFurtherCallRequired upcoming calls for child" + e);
					}
				} else if (callObj.getMotherId() != null && callObj.getChildId() == null) {
					try {
						outboundCallsRepo.updateIsFurtherCallRequiredForUpcomingCallForMother(callObj.getMotherId(),
								obj.getIsFurtherCallRequired());
					} catch (Exception e) {
						logger.error("Failed to update isFurtherCallRequired for mother: " + callObj.getMotherId(), e);
						throw new ECDException("Failed to update isFurtherCallRequired upcoming calls for mother" + e);
					}
				}
			}
			
			// sticky agents
			if (request.getIsStickyAgentRequired() != null && request.getIsStickyAgentRequired()) {
				if (request.getChildId() != null)
					outboundCallsRepo.stickyChildAgentAllocation(request.getObCallId(), request.getChildId(),
							request.getUserId());
				else
					outboundCallsRepo.stickyMotherAgentAllocation(request.getObCallId(), request.getMotherId(),
							request.getUserId());
			}
			if (null != request.getPreferredLanguage()) {
				if (null != callObj.getMotherId() && callObj.getChildId() == null) {
					motherRecordRepo.updatePreferredLanguage(request.getPreferredLanguage(), callObj.getMotherId());
				}
				if (callObj.getMotherId() != null && callObj.getChildId() != null) {
					childRecordRepo.updatePreferredLanguage(request.getPreferredLanguage(), callObj.getChildId());
				}
			}
			if(!StringUtil.isNullOrEmpty(request.getCorrectPhoneNumber())) {
				if (null != callObj.getMotherId() && callObj.getChildId() == null) {
					motherRecordRepo.updateCorrectPhoneNumber(request.getCorrectPhoneNumber(), callObj.getMotherId());
				}
				if (callObj.getMotherId() != null && callObj.getChildId() != null) {
					childRecordRepo.updateCorrectPhoneNumber(request.getCorrectPhoneNumber(), callObj.getChildId());
				}
			}
			Map<String, Object> responseMap = new HashMap<>();
			responseMap.put("response", "Call closed successfully");
			return new Gson().toJson(responseMap);
		} catch (Exception e) {
			throw new ECDException(e);
		}

	}

	private String calculateCallDuration(Timestamp CallStartTime, Timestamp CallEndTime) throws Exception {

		long diff = CallEndTime.getTime() - CallStartTime.getTime();
		long durationInSeconds = diff / 1000;

		long SECONDS_IN_A_MINUTE = 60;
		long MINUTES_IN_AN_HOUR = 60;
		long HOURS_IN_A_DAY = 24;

		long sec = (durationInSeconds >= SECONDS_IN_A_MINUTE) ? durationInSeconds % SECONDS_IN_A_MINUTE
				: durationInSeconds;
		long min = (durationInSeconds /= SECONDS_IN_A_MINUTE) >= MINUTES_IN_AN_HOUR
				? durationInSeconds % MINUTES_IN_AN_HOUR
				: durationInSeconds;
		long hrs = (durationInSeconds /= MINUTES_IN_AN_HOUR) >= HOURS_IN_A_DAY ? durationInSeconds % HOURS_IN_A_DAY
				: durationInSeconds;

		StringBuffer sb = new StringBuffer();
		String EMPTY_STRING = "";
		sb.append(hrs > 0 ? hrs + (hrs > 1 ? " hours " : " hour ") : EMPTY_STRING);
		sb.append(min > 0 ? min + (min > 1 ? " mins " : " min ") : EMPTY_STRING);
		sb.append(sec > 0 ? sec + (sec > 1 ? " secs " : " sec ") : EMPTY_STRING);

		String callDuration = sb.toString();

		return callDuration;

	}

	private Timestamp getTimestampFromString(String date) throws Exception {

		DateTimeFormatter ISO_DATE_TIME = DateTimeFormatter.ISO_DATE_TIME;
		LocalDateTime localDateTime = LocalDateTime.from(ISO_DATE_TIME.parse(date));

		Timestamp timestamp = Timestamp.valueOf(localDateTime);

		return timestamp;

	}

	private String createEcdCallRecordsInOutboundCalls(CallClosureDTO request,
			List<CallConfiguration> callConfigurationDetails, String phoneNoType) {

		try {
			if (request != null && request.getEcdCallType() != null
					&& request.getEcdCallType().equalsIgnoreCase("introductory")) {
				if (callConfigurationDetails != null && callConfigurationDetails.size() > 0) {

					ChildRecord childRecord = null;
					MotherRecord motherRecord = null;
					if (request.getChildId() != null) {
						childRecord = childRecordRepo.findByEcdIdNoChildId(request.getChildId());

					} else {
						if (request.getMotherId() != null) {
							motherRecord = motherRecordRepo.findByEcdIdNo(request.getMotherId());
						} else {
							throw new InvalidRequestException("Missing RCH Id");
						}
					}

					OutboundCalls outboundCalls;
					List<OutboundCalls> outboundCallsList = new ArrayList<>();
					Timestamp callStartDate = null;

					for (CallConfiguration callConfiguration : callConfigurationDetails) {
						outboundCalls = new OutboundCalls();

						// set object values based on logic
						if (motherRecord != null && motherRecord.getLmpDate() != null) {
							if (callConfiguration.getBaseLine().equalsIgnoreCase("DOB"))
								continue;

							Timestamp callEndDate = null;
							if (callStartDate == null)
								callStartDate = motherRecord.getLmpDate();
							outboundCalls.setCallDateFrom(callStartDate);

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
								outboundCalls.setCallDateTo(callEndDate);

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
							outboundCalls.setCallDateFrom(callStartDate);

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
								outboundCalls.setCallDateTo(callEndDate);

							Calendar cal = Calendar.getInstance();
							cal.setTime(callEndDate);
							cal.add(Calendar.DAY_OF_WEEK, 1);
							callStartDate = new Timestamp(cal.getTime().getTime());
						}

						outboundCalls.setPhoneNumberType(phoneNoType);

						// from request
						if (request.getBeneficiaryRegId() != null)
							outboundCalls.setBeneficiaryRegId(request.getBeneficiaryRegId());
						if (request.getMotherId() != null)
							outboundCalls.setMotherId(request.getMotherId());
						if (request.getChildId() != null)
							outboundCalls.setChildId(request.getChildId());
						if (request.getPsmId() != null)
							outboundCalls.setPsmId(request.getPsmId());
						if (request.getCreatedBy() != null)
							outboundCalls.setCreatedBy(request.getCreatedBy());
						if (request.getIsHrp() != null)
							outboundCalls.setIsHighRisk(request.getIsHrp());
						if (request.getIsHrni() != null)
							outboundCalls.setIsHrni(request.getIsHrni());

						outboundCalls.setCallAttemptNo(0);
						outboundCalls.setCallStatus("open");
						outboundCalls.setAllocationStatus("unallocated");

						// from conf
						if (callConfiguration.getCallType() != null)
							outboundCalls.setEcdCallType(callConfiguration.getCallType());
						if (callConfiguration.getDisplayName() != null)
							outboundCalls.setDisplayEcdCallType(callConfiguration.getDisplayName());

						outboundCallsList.add(outboundCalls);
					}
					outboundCallsRepo.saveAll(outboundCallsList);

				}

			}

			return "created";
		} catch (Exception e) {
			throw new ECDException(e);
		}
	}

}
