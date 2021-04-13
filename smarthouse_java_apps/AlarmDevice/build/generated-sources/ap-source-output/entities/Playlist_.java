package entities;

import entities.Songs;
import entities.User;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2021-02-26T06:22:57")
@StaticMetamodel(Playlist.class)
public class Playlist_ { 

    public static volatile SingularAttribute<Playlist, Integer> id;
    public static volatile SingularAttribute<Playlist, Songs> songid;
    public static volatile SingularAttribute<Playlist, User> userid;

}