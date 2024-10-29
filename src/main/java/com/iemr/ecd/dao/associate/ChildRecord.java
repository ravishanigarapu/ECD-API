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
@Table(name = "t_childvaliddata")
@Entity
public class ChildRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "RowID", insertable = false)
	private Long rowId;

	@Column(name = "BeneficiaryRegID")
	private Long beneficiaryRegId;

	@Column(name = "Date_of_Entry")
	private Timestamp dateOfEntry;

	@Column(name = "MCTSID_no_Child_ID")
	private Long ecdIdNoChildId;

	@Column(name = "Mother_Name")
	private String motherName;

	@Column(name = "Child_Name")
	private String childName;

	@Column(name = "Father_Name")
	private String fatherName;

	@Column(name = "Mother_ID")
	private Long motherId;

	@Column(name = "DOB")
	private Timestamp dob;

	@Column(name = "Place_of_Birth")
	private String placeOfBirth;

	@Column(name = "Gender")
	private String gender;

	@Column(name = "Phone_No_of")
	private String whomPhoneNo;

	@Column(name = "Phone_No")
	private String phoneNo;

	@Column(name = "State_ID")
	private Integer stateID;

	@Column(name = "State_Name")
	private String stateName;

	@Column(name = "Taluka_ID")
	private Integer talukaId;

	@Column(name = "Taluka_Name")
	private String talukaName;

	@Column(name = "District_ID")
	private Integer districtID;

	@Column(name = "District_Name")
	private String districtName;

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

	@Column(name = "CityID")
	private Long CityID;

	@Column(name = "City")
	private String cityName;

	@Column(name = "ANM_ID")
	private Integer anmId;

	@Column(name = "ANM_Name")
	private String anmName;

	@Column(name = "ANM_Phone_No")
	private String anmPh;

	@Column(name = "ASHA_ID")
	private Integer ashaId;

	@Column(name = "ASHA_Name")
	private String ashaName;

	@Column(name = "ASHA_Phone_No")
	private String ashaPh;

	@Column(name = "PHC_ID")
	private Integer PHCID;

	@Column(name = "PHC_Name")
	private String PHCName;

	@Column(name = "GP_Village")
	private String GPVillage;

	@Column(name = "Address")
	private String address;

	@Column(name = "Remarks")
	private String remarks;

	@Column(name = "Status")
	private String status;

	@Column(name = "Is_Valid")
	private Boolean IsValid;

	@Column(name = "Is_Error")
	private Boolean IsError;

	@Column(name = "Error_Reason")
	private String ErrorReason;

	@Column(name = "FileID")
	private Long fileID;

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

	@Column(name = "is_Upload")
	private Boolean isUpload;

	@Column(name = "IsHrni")
	private Boolean isHrni;

	@Column(name = "Hrni_Reason")
	private String hrni_Reason;

	@Column(name = "Child_RCH_ID_No")
	private Long childRchIdNo;

	@Column(name = "Registration_Date")
	private Timestamp registrationDate;

	@Column(name = "Mother_RCH_ID_No")
	private Long motherRchIdNo;

	@Column(name = "Weight_of_Child")
	private Double weightOfChild;

	@Column(name = "Entry_type")
	private Integer entryType;

	@Column(name = "entrytype")
	private String entryTypeStr;

	@Column(name = "BCG_Date")
	private Timestamp bcgDate;

	@Column(name = "OPV0_Date")
	private Timestamp opv0Date;

	@Column(name = "OPV2_Date")
	private Timestamp opv2Date;

	@Column(name = "OPV3_Date")
	private Timestamp opv3Date;

	@Column(name = "OPVBooster_Date")
	private Timestamp opvBoosterDate;

	@Column(name = "DPT1_Date")
	private Timestamp dpt1Date;

	@Column(name = "DPTBooster_Date")
	private Timestamp dptBoosterDate;

	@Column(name = "HepatitisB0_Dt")
	private Timestamp hepatitisB0Dt;

	@Column(name = "VitA_Dose1_Date")
	private Timestamp vitADose1Date;

	@Column(name = "VitA_Dose2_Date")
	private Timestamp vitADose2Date;

	@Column(name = "VitA_Dose3_Date")
	private Timestamp vitADose3Date;

	@Column(name = "VitA_Dose9_Date")
	private Timestamp vitADose9Date;

	@Column(name = "MR_Date")
	private Timestamp mrDate;

	@Column(name = "JE_Date")
	private Timestamp jeDate;

	@Column(name = "Caste")
	private String caste;
	
	@Column(name = "ChildID")
	private Long childMctsId;
	
	@Column(name = "MotherID")
	private Long motherMctsId;
	
	@Column(name = "preferredLanguage")
	private String preferredLanguage;
}
