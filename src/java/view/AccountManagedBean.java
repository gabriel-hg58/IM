package view;

import controller.DepartmentJpaController;
import controller.UserAccountJpaController;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;
import model.UserAccount;

@ManagedBean
@SessionScoped
public class AccountManagedBean {

    //List
    private ArrayList<UserAccount> listOfUsers = new ArrayList<>();
    //Control
    UserAccountJpaController ControlUserAccount = new UserAccountJpaController(EmProvider.getInstance().getEntityManagerFactory());
    DepartmentJpaController ControlDepartment = new DepartmentJpaController(EmProvider.getInstance().getEntityManagerFactory());
    //String
    String filterUser;
    //AdmVars
    boolean typeVisibility = false;
    //Auxiliary
    UserAccount actualUserAccount = new UserAccount();
    String password;
    String confirmPassword;
    String actualPassword;
    DocumentManagedBean docManaged = new DocumentManagedBean();

    public AccountManagedBean() {
    }

    //  --------------------  Redirect Metods  --------------------
    public String gotoAddAccount() {
        return "/addAccount.xhtml?faces-redirect=true";
    }

    public String gotoUserList() {
        return "/public/manageAccounts/userList.xhtml?faces-redirect=true";
    }

    public String gotoLogin() {
        return "/login.xhtml?faces-redirect=true";
    }

    public String gotoEditAccount() {
        return "/public/manageAccounts/editAccount.xhtml?faces-redirect=true";
    }

    public String gotoEditPassword() {
        return "/public/manageAccounts/editPassword.xhtml?faces-redirect=true";
    }

    //  ---------------------  Public Metods  ---------------------    
    public void loadUsers() {
        listOfUsers = new ArrayList(ControlUserAccount.findUserAccountEntities());
    }

    public void loadActualUser() {
        actualUserAccount = ControlUserAccount.findUserAccount(ManageSessions.getUserId());
    }

    public String saveAccount() {
        if (password != null && password.equals(confirmPassword)) {
            actualUserAccount.setAdministrator(false);
            actualUserAccount.setPassword(password);
            try {
                ControlUserAccount.create(actualUserAccount);
            } catch (Exception e) {
                e.printStackTrace();
            }
            validateUsernamePassword();
            return docManaged.gotoDocumentsReceived();
        } else {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("Nome de usuario e/ou senha estão errados", "Erro"));
        }
        return "#";
    }

    public boolean validateUser(UserAccount comparar) {
        UserAccount validate = ControlUserAccount.findUserAccount(actualUserAccount.getUser());
        if (validate == null) {
            return false;
        }
        if (validate.getUser().equals(comparar.getUser())
                && validate.getPassword().equals(comparar.getPassword())) {
            actualUserAccount = validate;
            return true;
        }
        return false;
    }

    public String validateUsernamePassword() {
        HttpSession session = ManageSessions.getSession();
        if (validateUser(actualUserAccount)) {
            session.setAttribute("username", actualUserAccount.getName());
            session.setAttribute("userid", actualUserAccount.getUser());
            session.setAttribute("userCode", actualUserAccount.getUserCode());
        } else {
            return "#";
        }
        if (actualUserAccount.getAdministrator()) {
            session.setAttribute("administrator", actualUserAccount.getAdministrator());
        }
        return docManaged.gotoDocumentsReceived();
    }

    public String editUser() {
        try {
            ControlUserAccount.edit(actualUserAccount);
            FacesMessage message = new FacesMessage("Sucesso!", "Sua conta foi atualizada.");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return docManaged.gotoDocumentsReceived();
        } catch (Exception e) {
            FacesMessage message = new FacesMessage("Erro!", "Sua conta não pode ser atualizada. Tente novamente mais tarde.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        return "#";
    }

    public String editUserPassword() {
        if (actualPassword.equals(actualUserAccount.getPassword())) {
            if (password.equals(confirmPassword)) {
                actualUserAccount.setPassword(password);
                try {
                    ControlUserAccount.edit(actualUserAccount);
                    return "/public/manageDocuments/documentsReceived.xhtml?faces-redirect=true";
                } catch (Exception e) {
                    FacesMessage message = new FacesMessage("Erro!", "Sua senha não pode ser alterada. Tente novamente mais tarde.");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                }
            } else {
                FacesMessage message = new FacesMessage("Erro!", "Sua senha e confirmação de senha não coincidem.");
                FacesContext.getCurrentInstance().addMessage(null, message);
            }
        } else {
            FacesMessage message = new FacesMessage("Erro!", "Sua senha atual não confere.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        return "#";
    }

    public void filterUsersByName() {
        listOfUsers.clear();
        EntityManager em = EmProvider.getInstance().getEntityManagerFactory().createEntityManager();
        List<UserAccount> users = em.createQuery("SELECT a FROM UserAccount a WHERE a.name LIKE :filterUser", UserAccount.class)
                .setParameter("filterUser", "%" + filterUser + "%")
                .getResultList();
        listOfUsers = new ArrayList<>(users);
    }

    public String logout() {
        HttpSession session = ManageSessions.getSession();
        session.invalidate();
        return gotoLogin();
    }

    public void cleanPasswords() {
        password = "";
        confirmPassword = "";
        actualPassword = "";
    }

    //  ---------------------  Administrator Metods  --------------------- 
    public String gotoAdmUserList(){
        return "/private/manageAccounts/userList.xhtml?faces-redirect=true";
    }
    
    public String destroyUser(String user){
        try {
            ControlUserAccount.destroy(user);
            return gotoAdmUserList();
        } catch (Exception e) {
            return "#";
        }
    }
    
    public String turnUserAdmin(String user){
        actualUserAccount = ControlUserAccount.findUserAccount(user);
        actualUserAccount.setAdministrator(true);
        try {
            ControlUserAccount.edit(actualUserAccount);
            return gotoAdmUserList();
        } catch (Exception e) {
            return "#";
        }
    }
    
    public String turnOffUserAdmin(String user){
        actualUserAccount = ControlUserAccount.findUserAccount(user);
        actualUserAccount.setAdministrator(false);
        try {
            ControlUserAccount.edit(actualUserAccount);
            return gotoAdmUserList();
        } catch (Exception e) {
            return "#";
        }
    }
    
    //  ---------------------  Getters and Setters  --------------------- 
    public UserAccount getActualUserAccount() {
        return actualUserAccount;
    }

    public void setActualUserAccount(UserAccount actualUserAccount) {
        this.actualUserAccount = actualUserAccount;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getPassword() {
        return password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public ArrayList<UserAccount> getListOfUsers() {
        return listOfUsers;
    }

    public String getFilterUser() {
        return filterUser;
    }

    public void setFilterUser(String filterUser) {
        this.filterUser = filterUser;
    }

    // Administrator metods
    public boolean visibilityType() {
        if (typeVisibility == false) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isTypeVisibility() {
        return typeVisibility;
    }

    public void setTypeVisibility(boolean typeVisibility) {
        this.typeVisibility = typeVisibility;
    }

    public String getActualPassword() {
        return actualPassword;
    }

    public void setActualPassword(String actualPassword) {
        this.actualPassword = actualPassword;
    }
}
