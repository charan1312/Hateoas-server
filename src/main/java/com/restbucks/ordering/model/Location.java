package com.restbucks.ordering.model;

import javax.xml.bind.annotation.XmlEnumValue;

public enum Location {
    @XmlEnumValue(value="takeaway")
    TAKEAWAY,
    @XmlEnumValue(value="inStore")
    IN_STORE
}
