package com.iemr.ecd.utils.constants;

import java.util.List;

public class Constants {
public static final String ALLOCATED = "allocated";
public static final String UNALLOCATED = "unallocated";
public static final String MOTHER = "Mother";
public static final String CHILD = "Child";
public static final String ANM = "ANM";
public static final String ASSOCIATE = "ASSOCIATE";
public static final String OPEN = "open";
public static final String COMPLETED = "Completed";
public static final String TIME_FORMAT_START_TIME = "T00:00:00+05:30";
public static final String TIME_FORMAT_END_TIME = "T23:59:59+05:30";
public static final String T = "T";
public static final String FROM_DATE_TO_DATE_IS_NULL = "from date / to date is null";
public static final List<String> REASONFORCALLNOTANSWERED = List.of("Invalid number","Out of service","Out of Reach","Switched off","No reply","Number busy","Call not connected");

private Constants() {}
}
