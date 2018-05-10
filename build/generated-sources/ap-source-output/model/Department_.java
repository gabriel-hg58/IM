package model;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.Document;
import model.UserAccount;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2018-05-10T09:56:33")
@StaticMetamodel(Department.class)
public class Department_ { 

    public static volatile ListAttribute<Department, Document> documentList;
    public static volatile SingularAttribute<Department, String> name;
    public static volatile ListAttribute<Department, UserAccount> userAccountList;
    public static volatile SingularAttribute<Department, String> email;
    public static volatile SingularAttribute<Department, Integer> idDepartment;

}