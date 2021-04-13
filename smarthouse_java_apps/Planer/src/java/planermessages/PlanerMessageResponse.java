/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planermessages;

import entities.Planner;
import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author LT
 */

@XmlRootElement(name="planner")
public class PlanerMessageResponse implements Serializable{
    
    List<Planner> plannerListItems;

    public PlanerMessageResponse() {
    }

    public PlanerMessageResponse(List<Planner> plannerListItems) {
        this.plannerListItems = plannerListItems;
    }

    @XmlElementWrapper(name="plannerList")
    @XmlElement(name="plannerItem")
    public List<Planner> getPlannerListItems() {
        return plannerListItems;
    }

    public void setPlannerListItems(List<Planner> plannerListItems) {
        this.plannerListItems = plannerListItems;
    }


    
    
    
    
}
