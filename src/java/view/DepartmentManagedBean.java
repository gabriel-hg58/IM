package view;

import controller.DepartmentJpaController;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.persistence.EntityManager;
import javax.faces.context.FacesContext;
import model.Department;

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
    public String gotoDepartmentList() {
        return "/public/manageDepartments/departmentList.xhtml?faces-redirect=true";
    }

    //  ---------------------  Public Metods  ---------------------
    public void loadDepartments() {
        listOfDepartments = new ArrayList(controlDepartment.findDepartmentEntities());
        actualDepartment = listOfDepartments.get(0);
    }
    
    public void filterUsersByName() {
        listOfDepartments.clear();
        EntityManager em = EmProvider.getInstance().getEntityManagerFactory().createEntityManager();
        List<Department> dep = em.createQuery("SELECT a FROM Department a WHERE a.name LIKE :filterDepartment", Department.class)
                .setParameter("filterDepartment", "%" + filterDepartment + "%")
                .getResultList();
        listOfDepartments = new ArrayList<>(dep);
    }

    //  ---------------------  Administrator Metods  ---------------------
    public String gotoAdmDepartmentList() {
        return "/private/manageDepartments/departmentList.xhtml?faces-redirect=true";
    }
    
    public String gotoAddDepartment() {
        return "/private/manageDepartments/addDepartment.xhtml?faces-redirect=true";
    }
    
    public void clean(){
        actualDepartment = new Department();
        actualDepartment.setIdDepartment(null);
    }
    
    public String saveDepartment(){
        try {
            controlDepartment.create(actualDepartment);
            return gotoAdmDepartmentList();
        } catch (Exception e) {
            FacesMessage message = new FacesMessage("Erro!", "Não foi possivel salvar o departamento");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        return "#";
    }
    
    public String destryDepartment(int id) {
        try {
            controlDepartment.destroy(id);
            return gotoAdmDepartmentList();
        } catch (Exception e) {
            FacesMessage message = new FacesMessage("Erro!", "Não foi possivel excluir o departamento");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        return "#";
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
