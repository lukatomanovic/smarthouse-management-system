package entities;

import entities.Alarm;
import entities.User;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2021-02-26T06:47:01")
@StaticMetamodel(Planner.class)
public class Planner_ { 

    public static volatile SingularAttribute<Planner, Integer> duration;
    public static volatile SingularAttribute<Planner, Alarm> alarmid;
    public static volatile SingularAttribute<Planner, String> description;
    public static volatile SingularAttribute<Planner, String> location;
    public static volatile SingularAttribute<Planner, Integer> id;
    public static volatile SingularAttribute<Planner, Date> time;
    public static volatile SingularAttribute<Planner, User> userid;

}