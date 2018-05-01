package model;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.Department;
import model.Type;
import model.UserAccount;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2018-04-25T22:24:12")
@StaticMetamodel(Document.class)
public class Document_ { 

    public static volatile SingularAttribute<Document, UserAccount> userSender;
    public static volatile SingularAttribute<Document, Integer> number;
    public static volatile SingularAttribute<Document, String> year;
    public static volatile SingularAttribute<Document, Integer> idDocument;
    public static volatile SingularAttribute<Document, byte[]> document;
    public static volatile SingularAttribute<Document, Department> departmentIdDepartment;
    public static volatile SingularAttribute<Document, Type> typeIdType;
    public static volatile SingularAttribute<Document, UserAccount> userReceiver;
    public static volatile SingularAttribute<Document, Date> publicationDate;
    public static volatile SingularAttribute<Document, Integer> situation;
    public static volatile SingularAttribute<Document, byte[]> denialExplanation;

}