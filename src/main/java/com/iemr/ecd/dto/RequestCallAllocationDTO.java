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
package com.iemr.ecd.dto;

import java.sql.Timestamp;

import org.springframework.stereotype.Component;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestCallAllocationDTO {

	private Integer[] fromUserIds;
	private Integer[] toUserIds;
	private Integer userId;
	private Integer noOfCalls;
	private Integer roleId;
	private String roleName;

	private String fDate;
	private String tDate;
	
	@Schema(hidden = true)
	private Timestamp fromDate;
	private Timestamp toDate;

	private String recordType;
	private String phoneNoType;

	private Integer providerServiceMapId;
	private Integer psmId;
	private String createdBy;

	private Boolean isIntroductory;
	private String preferredLanguage;

}
