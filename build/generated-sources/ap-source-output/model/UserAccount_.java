package model;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.Department;
import model.Document;
import model.Help;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2018-05-06T23:57:31")
@StaticMetamodel(UserAccount.class)
public class UserAccount_ { 

    public static volatile SingularAttribute<UserAccount, String> password;
    public static volatile SingularAttribute<UserAccount, Boolean> administrator;
    public static volatile SingularAttribute<UserAccount, String> phone;
    public static volatile ListAttribute<UserAccount, Document> documentList;
    public static volatile ListAttribute<UserAccount, Help> helpList;
    public static volatile SingularAttribute<UserAccount, String> name;
    public static volatile SingularAttribute<UserAccount, Department> departmentIdDepartment;
    public static volatile ListAttribute<UserAccount, Document> documentList1;
    public static volatile SingularAttribute<UserAccount, String> user;
    public static volatile SingularAttribute<UserAccount, Integer> userCode;
    public static volatile SingularAttribute<UserAccount, String> email;

}