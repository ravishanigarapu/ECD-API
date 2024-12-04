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
package com.iemr.ecd.repository.masters;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iemr.ecd.dao.masters.AgentsViewMaster;

@Repository
public interface AgentsViewMasterRepo extends CrudRepository<AgentsViewMaster, Integer> {
	
	@Query("SELECT DISTINCT a FROM AgentsViewMaster a WHERE a.roleId = :roleId")
	List<AgentsViewMaster> findByRoleId(@Param("roleId") Integer roleId);

	@Query(value = "SELECT v from AgentsViewMaster AS v WHERE v.roleId = :roleId AND v.preferredLanguage = :preferredLanguage")
	List<AgentsViewMaster> findByRoleIdAndLanguage(@Param("roleId") Integer roleId, @Param("preferredLanguage") String preferredLanguage);

}
