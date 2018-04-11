package converters;

import controller.DepartmentJpaController;
import view.EmProvider;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import model.Department;

@FacesConverter("ConvertDepartments")
public class ConvertDepartments implements Converter{
    
    DepartmentJpaController dep = new DepartmentJpaController(EmProvider.getInstance().getEntityManagerFactory());
    
    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        return dep.findDepartment(Integer.valueOf(string));
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        Department t = (Department)o;
        return String.valueOf(t.getIdDepartment());
    }
}