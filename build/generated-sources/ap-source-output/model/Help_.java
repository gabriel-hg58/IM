package model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.UserAccount;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2018-04-25T22:24:12")
@StaticMetamodel(Help.class)
public class Help_ { 

    public static volatile SingularAttribute<Help, Integer> notification;
    public static volatile SingularAttribute<Help, String> titleHelp;
    public static volatile SingularAttribute<Help, String> description;
    public static volatile SingularAttribute<Help, Integer> idHelp;
    public static volatile SingularAttribute<Help, UserAccount> userAccountUserCode;

}