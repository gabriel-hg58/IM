package view;

import controller.DepartmentJpaController;
import controller.UserAccountJpaController;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;
import model.Department;
import model.UserAccount;
import org.apache.catalina.ha.ClusterSession;

@ManagedBean
@SessionScoped
public class AccountManagedBean {

    UserAccount actualUserAccount = new UserAccount();
    String password;
    String confirmPassword;
    //List
    private ArrayList<Department> listOfDepartments = new ArrayList<>();
    private ArrayList<UserAccount> listOfUsers = new ArrayList<>();
    //Control
    UserAccountJpaController ControlUserAccount = new UserAccountJpaController(EmProvider.getInstance().getEntityManagerFactory());
    DepartmentJpaController ControlDepartment = new DepartmentJpaController(EmProvider.getInstance().getEntityManagerFactory());
    //String
    String filterUser;
    //AdmVars
    boolean typeVisibility = false;
    
    public AccountManagedBean() {
    }
    
    public String gotoAddAccount(){
        return "/addAccount.xhtml?faces-redirect=true";
    }
    
    public String gotoHome(){
        return "/public/manageAccounts/home.xhtml?faces-redirect=true";
    }
    
    public String gotoUserList(){
        return "/public/manageAccounts/userList.xhtml?faces-redirect=true";
    }
    
    public String gotoLogin(){
        return "/login.xhtml?faces-redirect=true";
    }
    
    public void loadDepartments(){
        listOfDepartments = new ArrayList(ControlDepartment.findDepartmentEntities());
    }
    
    public void loadUser(){
        listOfUsers = new ArrayList(ControlUserAccount.findUserAccountEntities());                
    }
    
    public String saveAccount(){
        System.out.println("Cheguei");
        if (password != null && password.equals(confirmPassword)) {
            actualUserAccount.setAdministrator(false);
            actualUserAccount.setManager(false);
            actualUserAccount.setPassword(password);
            try {
                ControlUserAccount.create(actualUserAccount);
            } catch (Exception e) {
                e.printStackTrace();
            }
            validateUsernamePassword();
            return gotoHome();
        } else {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("Nome de usuario e/ou senha estão errados", "Erro"));
        }
        return "#";
    }
    
    public boolean validateUser(UserAccount comparar){
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
        if (validateUser(actualUserAccount)) {
            HttpSession session = ManageSessions.getSession();
            session.setAttribute("username", actualUserAccount.getName());
            session.setAttribute("userid", actualUserAccount.getUser());
            session.setAttribute("userCode", actualUserAccount.getUserCode());
        } else {
            return "#";
        }
        return gotoHome();
    }
    
    public void filterUsersByName() {
        listOfUsers.clear();
        EntityManager em = EmProvider.getInstance().getEntityManagerFactory().createEntityManager();
        List<UserAccount> users = em.createQuery("SELECT a FROM UserAccount a WHERE a.name LIKE :filterUser", UserAccount.class)
                .setParameter("filterUser", "%" + filterUser + "%")
                .getResultList();
        listOfUsers = new ArrayList<>(users);
    }
    
    public String logout(){
        HttpSession session = ManageSessions.getSession();
        session.invalidate();
         return gotoLogin();
    }
        
    public UserAccount getActualUserAccount() {
        return actualUserAccount;
    }

    public void setActualUserAccount(UserAccount actualUserAccount) {
        this.actualUserAccount = actualUserAccount;
    }

    public ArrayList<Department> getListOfDepartments() {
        return listOfDepartments;
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
    
    public boolean visibilityType(){
        if(typeVisibility == false){
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
       
}
