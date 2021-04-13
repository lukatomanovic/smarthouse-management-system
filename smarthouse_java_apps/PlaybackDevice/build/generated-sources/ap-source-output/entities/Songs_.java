package entities;

import entities.Alarm;
import entities.Playlist;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2021-02-26T06:23:10")
@StaticMetamodel(Songs.class)
public class Songs_ { 

    public static volatile ListAttribute<Songs, Playlist> playlistList;
    public static volatile ListAttribute<Songs, Alarm> alarmList;
    public static volatile SingularAttribute<Songs, Integer> id;
    public static volatile SingularAttribute<Songs, String> title;
    public static volatile SingularAttribute<Songs, String> url;

}