package view;

import controller.DepartmentJpaController;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.persistence.EntityManager;
import model.Department;
import model.UserAccount;

@ManagedBean
@SessionScoped
public class DepartmentManagedBean {
    
    //To Handle
    private Department actualDepartment = new Department();
    //Control
    private DepartmentJpaController controlDepartment = new DepartmentJpaController(EmProvider.getInstance().getEntityManagerFactory());
    //List
    private ArrayList<Department> listOfDepartments = new ArrayList<>();
    //Auxiliary
    private String filterDepartment;
    
    public DepartmentManagedBean() {
    }
    
    //  --------------------  Redirect Metods  --------------------
    
    public String gotoDepartmentList(){
        return "/public/manageDepartments/departmentList.xhtml?faces-redirect=true";
    }
    
    public void filterUsersByName() {
        listOfDepartments.clear();
        EntityManager em = EmProvider.getInstance().getEntityManagerFactory().createEntityManager();
        List<Department> dep = em.createQuery("SELECT a FROM Department a WHERE a.name LIKE :filterDepartment", Department.class)
                .setParameter("filterDepartment", "%" + filterDepartment + "%")
                .getResultList();
        listOfDepartments = new ArrayList<>(dep);
    }
    
    //  ---------------------  Public Metods  ---------------------
    
    public void loadDepartments(){
        listOfDepartments = new ArrayList(controlDepartment.findDepartmentEntities());
    }
    
    //  ---------------------  Getters and Setters  ---------------------

    public Department getActualDepartment() {
        return actualDepartment;
    }

    public void setActualDepartment(Department actualDepartment) {
        this.actualDepartment = actualDepartment;
    }

    public DepartmentJpaController getControlDepartment() {
        return controlDepartment;
    }

    public void setControlDepartment(DepartmentJpaController controlDepartment) {
        this.controlDepartment = controlDepartment;
    }

    public ArrayList<Department> getListOfDepartments() {
        return listOfDepartments;
    }

    public void setListOfDepartments(ArrayList<Department> listOfDepartments) {
        this.listOfDepartments = listOfDepartments;
    }    

    public String getFilterDepartment() {
        return filterDepartment;
    }

    public void setFilterDepartment(String filterDepartment) {
        this.filterDepartment = filterDepartment;
    }
}
