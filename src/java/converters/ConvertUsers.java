package converters;

import controller.UserAccountJpaController;
import view.EmProvider;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import model.UserAccount;

@FacesConverter("ConvertUsers")
public class ConvertUsers implements Converter{
    
    UserAccountJpaController user = new UserAccountJpaController(EmProvider.getInstance().getEntityManagerFactory());
    
    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        return user.findUserAccount(string.valueOf(string));
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        UserAccount t = (UserAccount)o;
        return String.valueOf(t.getUser());
    }
}