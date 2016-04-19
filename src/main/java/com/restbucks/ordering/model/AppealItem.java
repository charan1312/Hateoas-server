package com.restbucks.ordering.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;

import com.restbucks.ordering.representations.Representation;
import com.restbucks.ordering.representations.Representation1;

@XmlRootElement
public enum AppealItem {
    @XmlEnumValue(value="recount")
    RE_COUNT,
    @XmlEnumValue(value="reevaluate")
    RE_EVALUATE,
    @XmlEnumValue(value="addimage")
    ADD_IMAGE

}
/*    @XmlElement(namespace = Representation1.APPEALS_NAMESPACE)      //COUNTING MISTAKE, REEVALUATION , ADD IMAGES
    private final String RE_COUNT = "Appealing for a Re-Counting";
    @XmlElement(namespace = Representation1.APPEALS_NAMESPACE)
    private final String RE_EVALAUTE = "Appealing for a Re-evaluation";
    @XmlElement(namespace = Representation1.APPEALS_NAMESPACE)
    private final String ADD_IMAGES = "Adding Image with Highlighted answers supporting the appeal";
*/    
    /**
     * For JAXB :-(
     */

/*    
    public String toString() {
        return size + " " + milk + " " + drink;
    }
}
*/