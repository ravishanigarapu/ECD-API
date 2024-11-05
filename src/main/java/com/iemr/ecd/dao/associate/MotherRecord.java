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
package com.iemr.ecd.dao.associate;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "t_mothervalidrecord")
@Entity
public class MotherRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MotherValidRecordID", insertable = false)
	private Long motherValidRecordID;

	@Column(name = "RowID")
	private Long rowID;

	@Column(name = "BeneficiaryRegID")
	private Long beneficiaryRegID;

	@Column(name = "Date_of_Entry")
	private Timestamp dateOfEntry;

	@Column(name = "Entry_type")
	private Integer entryTpe;

	@Column(name = "MCTSID_no", insertable = true, updatable = false)
	private Long ecdIdNo;

	@Column(name = "Name")
	private String motherName;

	@Column(name = "Husband_Name")
	private String husbandName;

	@Column(name = "PhoneNo_Of_Whom")
	private String whomPhoneNo;

	@Column(name = "Whom_PhoneNo")
	private String phoneNo;

	@Column(name = "State_ID")
	private Integer stateID;

	@Column(name = "State_Name")
	private String stateName;

	@Column(name = "District_ID")
	private Integer districtID;

	@Column(name = "District_Name")
	private String districtName;

	@Column(name = "Taluka_ID")
	private Integer talukaId;

	@Column(name = "Taluka_Name")
	private String talukaName;

	@Column(name = "Block_ID")
	private Integer blockID;

	@Column(name = "Block_Name")
	private String blockName;

	@Column(name = "SubCenter_ID")
	private Integer subCenterId;

	@Column(name = "SubCenter_Name")
	private String subCenterName;

	@Column(name = "Village_ID")
	private Integer villageID;

	@Column(name = "Village_Name")
	private String VillageName;

	@Column(name = "City_ID")
	private Integer CityID;

	@Column(name = "City_Name")
	private String cityName;

	@Column(name = "ANM_ID")
	private Integer anmId;

	@Column(name = "ANM_Name")
	private String anmName;

	@Column(name = "ANM_Ph")
	private String anmPh;

	@Column(name = "ASHA_ID")
	private Integer ashaId;

	@Column(name = "ASHA_Name")
	private String ashaName;

	@Column(name = "ASHA_Ph")
	private String ashaPh;

	@Column(name = "PHC_ID")
	private String PHCID;

	@Column(name = "PHC_Name")
	private String PHCName;

	@Column(name = "SUBPHC_ID")
	private String SUBPHCID;

	@Column(name = "SUBPHC_Name")
	private String SUBPHCName;

	@Column(name = "GP_Village")
	private String GPVillage;

	@Column(name = "Address")
	private String address;

	@Column(name = "LMP_Date")
	private Timestamp lmpDate;

	@Column(name = "EDD")
	private Timestamp edd;

	@Column(name = "Facility_Name")
	private String FacilityName;

	@Column(name = "Remarks")
	private String remarks;

	@Column(name = "No_of_Try")
	private Integer NoofTry;

	@Column(name = "Call_Answered")
	private Boolean CallAnswered;

	@Column(name = "Status")
	private String status;

	@Column(name = "High_Risk")
	private Boolean highRisk;

	@Column(name = "High_Risk_Reason")
	private String highRiskReason;

	@Column(name = "Call_Verified")
	private Boolean CallVerified;

	@Column(name = "Call_No")
	private String CallNo;

	@Column(name = "Questions_Asked")
	private String QuestionsAsked;

	@Column(name = "Asnwer_Given_by_Benificary")
	private String AsnwerGivenByBenificary;

	@Column(name = "NoCall_Reason")
	private String NoCallReason;

	@Column(name = "NoPhone_Reason")
	private String NoPhoneReason;

	@Column(name = "Is_Valid")
	private Boolean IsValid;

	@Column(name = "Is_Error")
	private Boolean IsError;

	@Column(name = "Error_Reason")
	private String ErrorReason;

	@Column(name = "FileID")
	private Integer fileID;

	@Column(name = "Deleted", insertable = false)
	private Boolean deleted;

	@Column(name = "CreatedBy")
	private String createdBy;

	@Column(name = "CreatedDate", insertable = false, updatable = false)
	private Timestamp createdDate;

	@Column(name = "ModifiedBy")
	private String modifiedBy;

	@Column(name = "LastModDate", insertable = false, updatable = false)
	private Timestamp lastModDate;

	@Column(name = "IsAllocated")
	private Boolean isAllocated;

	@Column(name = "Registration_no")
	private Long registrationNo;

	@Column(name = "Landline_no")
	private String landlineNo;

	@Column(name = "Caste")
	private String caste;

	@Column(name = "Mother_Weight")
	private Integer motherWeight;

	@Column(name = "Birth_Date")
	private Timestamp birthDate;

	@Column(name = "Age")
	private Integer age;

	@Column(name = "JSY_Beneficiary")
	private String jsyBeneficiary;

	@Column(name = "YR")
	private String motherRegistrationYear;

	@Column(name = "ANC1_Date")
	private Timestamp anc1Date;

	@Column(name = "ANC1_Symptoms_High_Risk")
	private String anc1SymptomsHighRisk;

	@Column(name = "ANC2_Date")
	private Timestamp anc2Date;

	@Column(name = "ANC2_Symptoms_High_Risk")
	private String anc2SymptomsHighRisk;

	@Column(name = "ANC3_Date")
	private Timestamp anc3Date;

	@Column(name = "ANC3_Symptoms_High_Risk")
	private String anc3SymptomsHighRisk;

	@Column(name = "ANC4_Date")
	private Timestamp anc4Date;

	@Column(name = "ANC4_Symptoms_High_Risk")
	private String anc4SymptomsHighRisk;

	@Column(name = "TT1_Date")
	private Timestamp tt1Date;

	@Column(name = "TT2_Date")
	private Timestamp tt2Date;

	@Column(name = "TTBooster_Date")
	private Timestamp ttBoosterDate;

	@Column(name = "Delivery_Date")
	private Timestamp deliveryDate;

	@Column(name = "Delivery_Place")
	private String deliveryPlace;

	@Column(name = "Delivery_Place_Name")
	private String deliveryPlaceName;

	@Column(name = "Delivery_type")
	private String deliveryType;

	@Column(name = "Delivery_Complications")
	private String deliveryComplications;
	
	@Column(name = "MotherID", insertable = true, updatable = false)
	private Long motherMctsId;
	
	@Column(name = "preferredLanguage")
	private String preferredLanguage;
}
