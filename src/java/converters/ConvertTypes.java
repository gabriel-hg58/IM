package converters;

import controller.TypeJpaController;
import view.EmProvider;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import model.Type;

@FacesConverter("ConvertTypes")
public class ConvertTypes implements Converter{
    
    TypeJpaController type = new TypeJpaController(EmProvider.getInstance().getEntityManagerFactory());
    
    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        return type.findType(Integer.valueOf(string));
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        Type t = (Type)o;
        return String.valueOf(t.getIdType());
    }
}