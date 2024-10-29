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
package com.iemr.ecd.dto.associate;

import java.sql.Date;
import java.sql.Timestamp;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class CallClosureDTO {
	
	private Long benCallId;
	private Long obCallId;
	private Long motherId;
	private Long childId;
	private Integer userId;
	private Integer agentId;
	private String role;
	private String phoneNo;
	private Integer psmId;
	private String ecdCallType;
	private String callId;
	private Boolean isOutbound;
	private Long beneficiaryRegId;
	private Boolean isFurtherCallRequired;
	private Integer reasonForNoFurtherCallsId;
	private String reasonForNoFurtherCalls;
	private Boolean isCallVerified;
	private Boolean isCallAnswered;
	private Integer reasonForCallNotAnsweredId;
	private String reasonForCallNotAnswered;
	private Boolean isCallDisconnected;
	private Integer ComplaintId;
	private String typeOfComplaint;
	private String complaintRemarks;
	private String nextAttemptDate;
	private String callRemarks;
	private String sendAdvice;
	private String altPhoneNo;
	private Boolean isStickyAgentRequired;
	private Boolean isHrp;
	private Boolean isHrni;
	private Boolean deleted;
	private String createdBy;
	private Timestamp createdDate;
	private String modifiedBy;
	private Date lastModDate;
	
	private Timestamp nextCallDate;
	private Boolean isWrongNumber;
    private String preferredLanguage;
    private String correctPhoneNumber;

}
