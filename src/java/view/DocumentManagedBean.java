package view;

import controller.DepartmentJpaController;
import controller.DocumentJpaController;
import controller.TypeJpaController;
import controller.UserAccountJpaController;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

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
    private String filterType;
    private Integer number;
    StreamedContent file;

    public DocumentManagedBean() {
    }

//  --------------------  Redirect Metods  --------------------    
    public String gotoSendDocument() {
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

    public void loadUsersByDepartment() {
        listOfUsers.clear();
        EntityManager em = EmProvider.getInstance().getEntityManagerFactory().createEntityManager();
        List<UserAccount> users = em.createQuery("SELECT A FROM UserAccount a GROUP BY a.departmentIdDepartment", UserAccount.class)
                .getResultList();
        listOfUsers = new ArrayList<>(users);
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
        getFileExtension(event.getFile().getFileName());
    }

    public String saveDocument() throws ParseException {
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
                return gotoDocumentsSent();
            } catch (Exception e) {
                FacesMessage message = new FacesMessage("Não foi possivel enviar o documento.");
                FacesContext.getCurrentInstance().addMessage(null, message);
                e.printStackTrace();
            }
        } else {
            FacesMessage message = new FacesMessage("Advertencia!", "O número do documento deve ser maior que 0.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        return "#";
    }

    public void loadDocuments() {
        listOfDocumentsReceived = new ArrayList(controlDocument.findDocumentEntities());
    }

    public void findDocumentSent() {
        listOfDocumentsSent.clear();
        actualUserAccount = controlUser.findUserAccount(ManageSessions.getUserId());
        EntityManager em = EmProvider.getInstance().getEntityManagerFactory().createEntityManager();
        List<Document> doc = em.createQuery("SELECT d FROM Document d WHERE d.userSender.user = :user ORDER BY d.number DESC", Document.class)
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

    public String generateDocName(int id) {
        String docName;
        docName = controlDocument.findDocument(id).getTypeIdType().getName();
        docName = docName + " - " + controlDocument.findDocument(id).getNumber() + " de "
                + controlDocument.findDocument(id).getYear();
        docName = docName + " por " + controlDocument.findDocument(id).getUserSender().getName()
                + controlDocument.findDocument(id).getDocExtension();
        return docName;
    }

    public StreamedContent downloadDocument(int id) {
        byte[] byteDoc = controlDocument.findDocument(id).getDocument();
        InputStream input = new ByteArrayInputStream(byteDoc);

        file = new DefaultStreamedContent(input, "document/pdf", generateDocName(id));
        return file;
    }

    public void getFileExtension(String nameFile) {
        String extension = nameFile;
        int pos = extension.indexOf(".");

        extension = extension.substring(pos, extension.length());

        actualDocument.setDocExtension(extension);
        System.out.println("extensão do documento: " + extension);
    }
    
    public void filterTypeByName() {
        listOfTypes.clear();
        EntityManager em = EmProvider.getInstance().getEntityManagerFactory().createEntityManager();
        List<Type> type = em.createQuery("SELECT a FROM Type a WHERE a.name LIKE :filterType", Type.class)
                .setParameter("filterType", "%" + filterType + "%")
                .getResultList();
        listOfTypes = new ArrayList<>(type);
    }

    //  --------------------  Administrator Metods  --------------------
    public String gotoAdmTypeList() {
        return "/private/manageTypes/typeList.xhtml?faces-redirect=true";
    }
    
    public String gotoAddTypeList() {
        return "/private/manageTypes/addType.xhtml?faces-redirect=true";
    }
    
    public String saveType(){
        try {
            controlType.create(actualDocumentType);
            return gotoAdmTypeList();
        } catch (Exception e) {
            FacesMessage message = new FacesMessage("Erro!", "Não foi possivel salvar este tipo de documento.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        return "#";
    }

    public String destryType(int id) {
        try {
            controlType.destroy(id);
            return gotoAdmTypeList();
        } catch (Exception e) {
            FacesMessage message = new FacesMessage("Erro!", "Não foi possivel excluir este tipo de documento.");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return "#";
        }
    }

    //  ----------------------  Auxiliary Metods  ---------------------
    public void clean() {
        fileName = "";
        number = null;
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

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }
}
