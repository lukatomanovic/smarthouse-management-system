package entities;

import entities.Songs;
import entities.User;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2021-02-26T06:23:10")
@StaticMetamodel(Alarm.class)
public class Alarm_ { 

    public static volatile SingularAttribute<Alarm, Integer> period;
    public static volatile SingularAttribute<Alarm, Integer> repetitioncount;
    public static volatile SingularAttribute<Alarm, Integer> totalrepetitionnumber;
    public static volatile SingularAttribute<Alarm, Integer> id;
    public static volatile SingularAttribute<Alarm, Date> time;
    public static volatile SingularAttribute<Alarm, Songs> songid;
    public static volatile SingularAttribute<Alarm, User> userid;
    public static volatile SingularAttribute<Alarm, String> status;

}