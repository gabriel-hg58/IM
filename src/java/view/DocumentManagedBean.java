package view;

import controller.DepartmentJpaController;
import controller.DocumentJpaController;
import controller.TypeJpaController;
import controller.UserAccountJpaController;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import static java.util.Arrays.stream;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import model.Department;
import model.Document;
import model.Type;
import model.UserAccount;
import static org.apache.jasper.tagplugins.jstl.core.Out.output;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import static org.eclipse.persistence.internal.core.helper.CoreClassConstants.CALENDAR;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

@ManagedBean
@SessionScoped
public class DocumentManagedBean {

    //To Handle
    Type actualDocumentType = new Type();
    Document actualDocument = new Document();
    Department actualDepartment = new Department();
    UserAccount actualUserAccount = new UserAccount();
    //List
    private ArrayList<Type> listOfTypes = new ArrayList<>();
    private ArrayList<Document> listOfDocumentsReceived = new ArrayList<>();
    private ArrayList<Document> listOfDocumentsSent = new ArrayList<>();
    private ArrayList<Department> listOfDepartments = new ArrayList<>();
    private ArrayList<UserAccount> listOfUsers = new ArrayList<>();
    //Control
    TypeJpaController controlType = new TypeJpaController(EmProvider.getInstance().getEntityManagerFactory());
    DocumentJpaController controlDocument = new DocumentJpaController(EmProvider.getInstance().getEntityManagerFactory());
    DepartmentJpaController controlDepartment = new DepartmentJpaController(EmProvider.getInstance().getEntityManagerFactory());
    UserAccountJpaController controlUser = new UserAccountJpaController(EmProvider.getInstance().getEntityManagerFactory());
    //Auxiliary
    private String fileName;
    private int number;
    StreamedContent file;

    public DocumentManagedBean() {
    }

//  --------------------  Redirect Metods  --------------------    
    public String gotoSendDocument() {
        actualDocument.setNumber(0);
        return "/public/manageDocuments/sendDocument.xhtml?faces-redirect=true";
    }

    public String gotoDocumentsReceived() {
        return "/public/manageDocuments/documentsReceived.xhtml?faces-redirect=true";
    }

    public String gotoDocumentsSent() {
        return "/public/manageDocuments/documentsSent.xhtml?faces-redirect=true";
    }

//  ---------------------  Public Metods  ---------------------
    public void loadTypes() {
        listOfTypes = new ArrayList(controlType.findTypeEntities());
    }

    public void loadDepartments() {
        listOfDepartments = new ArrayList(controlDepartment.findDepartmentEntities());
    }

    public void loadUsers() {
        listOfUsers = new ArrayList(controlUser.findUserAccountEntities());
    }

    public void filterUserByDepartment() {
        listOfUsers.clear();
        EntityManager em = EmProvider.getInstance().getEntityManagerFactory().createEntityManager();
        List<UserAccount> users = em.createQuery("SELECT A FROM UserAccount a WHERE a.departmentIdDepartment = :filterUser", UserAccount.class)
                .setParameter("filterUser", actualDocument.getDepartmentIdDepartment())
                .getResultList();
        listOfUsers = new ArrayList<>(users);
    }

    public void handleFileUpload(FileUploadEvent event) {
        fileName = "Arquivo enviado: " + event.getFile().getFileName();
        actualDocument.setDocument(event.getFile().getContents());
        FacesMessage message = new FacesMessage("Sucesso!", event.getFile().getFileName() + " foi carregado.");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public StreamedContent downloadDocument(int id) {
        byte[] byteDoc = controlDocument.findDocument(id).getDocument();
        InputStream input = new ByteArrayInputStream(byteDoc);
        file = new DefaultStreamedContent(input, "document/docx", generateDocName(id));
        return file;
    }

    public void saveDocument() throws ParseException {
        if (number > 0) {
            UserAccount add = controlUser.findUserAccount(ManageSessions.getUserId());

            String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));

            Calendar cal = new GregorianCalendar();

            actualDocument.setNumber(number);
            actualDocument.setPublicationDate(cal.getTime());
            actualDocument.setSituation(0);
            actualDocument.setUserSender(add);
            actualDocument.setYear(year);

            try {
                controlDocument.create(actualDocument);
                FacesMessage message = new FacesMessage("Sucesso!", "O oficio de número " + actualDocument.getNumber() + " foi enviado.");
                FacesContext.getCurrentInstance().addMessage(null, message);
            } catch (Exception e) {
                e.printStackTrace();
                FacesMessage message = new FacesMessage("Não foi possivel enviar o documento.");
                FacesContext.getCurrentInstance().addMessage(null, message);
            }
        } else {
            FacesMessage message = new FacesMessage("Advertencia!", "O número do documento deve ser maior que 0.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void loadDocuments() {
        listOfDocumentsReceived = new ArrayList(controlDocument.findDocumentEntities());
    }

    public void findDocumentSent() {
        listOfDocumentsSent.clear();
        actualUserAccount = controlUser.findUserAccount(ManageSessions.getUserId());
        EntityManager em = EmProvider.getInstance().getEntityManagerFactory().createEntityManager();
        List<Document> doc = em.createQuery("SELECT d FROM Document d WHERE d.userSender.user = :user ORDER BY d.publicationDate DESC", Document.class)
                .setParameter("user", actualUserAccount.getUser())
                .getResultList();
        listOfDocumentsSent = new ArrayList<>(doc);
    }

    public void findDocumentReceived() {
        listOfDocumentsReceived.clear();
        actualUserAccount = controlUser.findUserAccount(ManageSessions.getUserId());
        EntityManager em = EmProvider.getInstance().getEntityManagerFactory().createEntityManager();
        List<Document> doc = em.createQuery("SELECT d FROM Document d WHERE d.userReceiver.user = :user ORDER BY d.publicationDate DESC", Document.class)
                .setParameter("user", actualUserAccount.getUser())
                .getResultList();
        listOfDocumentsReceived = new ArrayList<>(doc);
    }
    
    public String generateDocName(int id){
        String docName;
        docName = controlDocument.findDocument(id).getTypeIdType().getName();
        docName = docName + " - " + controlDocument.findDocument(id).getNumber() + " de "
                + controlDocument.findDocument(id).getYear();
        docName = docName + " por " + controlDocument.findDocument(id).getUserSender().getName();
        return docName;
    }

    //  --------------------  Administrator Metods  --------------------
    public String gotoLists() {
        return "/private/admLists.xhtml?faces-redirect=true";
    }

    public String gotoAdds() {
        return "/private/admAdds.xhtml?faces-redirect=true";
    }

    public String saveDepartment() {
        controlDepartment.create(actualDepartment);
        return gotoLists();
    }

    public String saveDocumentType() {
        controlType.create(actualDocumentType);
        return gotoLists();
    }

    //  ----------------------  Auxiliary Metods  ---------------------
    public void clean() {
        fileName = "";
    }

    //  --------------------  Getters and Setters  --------------------
    public Type getActualDocumentType() {
        return actualDocumentType;
    }

    public void setActualDocumentType(Type actualDocumentType) {
        this.actualDocumentType = actualDocumentType;
    }

    public ArrayList<Type> getListOfTypes() {
        return listOfTypes;
    }

    public void setListOfTypes(ArrayList<Type> listOfTypes) {
        this.listOfTypes = listOfTypes;
    }

    public TypeJpaController getControlType() {
        return controlType;
    }

    public void setControlType(TypeJpaController ControlType) {
        this.controlType = ControlType;
    }

    public Document getActualDocument() {
        return actualDocument;
    }

    public void setActualDocument(Document actualDocument) {
        this.actualDocument = actualDocument;
    }

    public Department getActualDepartment() {
        return actualDepartment;
    }

    public void setActualDepartment(Department actualDepartment) {
        this.actualDepartment = actualDepartment;
    }

    public ArrayList<Document> getListOfDocumentsReceived() {
        return listOfDocumentsReceived;
    }

    public void setListOfDocumentsReceived(ArrayList<Document> listOfDocuments) {
        this.listOfDocumentsReceived = listOfDocuments;
    }

    public ArrayList<Department> getListOfDepartments() {
        return listOfDepartments;
    }

    public void setListOfDepartments(ArrayList<Department> listOfDepartment) {
        this.listOfDepartments = listOfDepartment;
    }

    public DocumentJpaController getControlDocument() {
        return controlDocument;
    }

    public void setControlDocument(DocumentJpaController controlDocument) {
        this.controlDocument = controlDocument;
    }

    public DepartmentJpaController getControlDepartment() {
        return controlDepartment;
    }

    public void setControlDepartment(DepartmentJpaController controlDepartment) {
        this.controlDepartment = controlDepartment;
    }

    public ArrayList<UserAccount> getListOfUsers() {
        return listOfUsers;
    }

    public void setListOfUsers(ArrayList<UserAccount> listOfUsers) {
        this.listOfUsers = listOfUsers;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public UserAccount getActualUserAccount() {
        return actualUserAccount;
    }

    public void setActualUserAccount(UserAccount actualUserAccount) {
        this.actualUserAccount = actualUserAccount;
    }

    public ArrayList<Document> getListOfDocumentsSent() {
        return listOfDocumentsSent;
    }

    public void setListOfDocumentsSent(ArrayList<Document> listOfDocumentsSent) {
        this.listOfDocumentsSent = listOfDocumentsSent;
    }

    public UserAccountJpaController getControlUser() {
        return controlUser;
    }

    public void setControlUser(UserAccountJpaController controlUser) {
        this.controlUser = controlUser;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
