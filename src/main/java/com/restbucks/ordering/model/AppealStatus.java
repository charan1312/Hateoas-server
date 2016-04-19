package com.restbucks.ordering.model;

import javax.xml.bind.annotation.XmlEnumValue;


public enum AppealStatus {
    @XmlEnumValue(value="Submitted")
    SUBMITTED,
    @XmlEnumValue(value="Approved")
    APPROVED, 
    @XmlEnumValue(value="InProcess")
    INPROCESS, 
    @XmlEnumValue(value="Rejected")
    REJECTED,
    @XmlEnumValue(value="Deleted")
    DELETED,
    @XmlEnumValue(value="Created")
    CREATED
}
