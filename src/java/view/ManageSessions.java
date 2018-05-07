package view;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class ManageSessions{

    public static HttpSession getSession(){
        return (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(false);
    }

    public static HttpServletRequest getRequest(){
        return (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();
    }

    public static String getUserName(){
        HttpSession session = getSession();
        if(session != null && session.getAttribute("username") != null) {
            return session.getAttribute("username").toString();
        }
        return null;
    }

    public static String getUserId(){
        HttpSession session = getSession();
        if (session != null) {
            return (String) session.getAttribute("userid");
        } else {
            return null;
        }
    }
    
    public static Integer getUserCode(){
        HttpSession session = getSession();
        if (session != null) {
            return (Integer) session.getAttribute("userCode");
        } else {
            return null;
        }
    }
    
    public static String getAdministrator(){
        HttpSession session = getSession();
        if(session != null && session.getAttribute("administrator") != null) {
            return session.getAttribute("administrator").toString();
        }
        return null;
    }
    
    public static String getLoggedAdmin(){
        HttpSession session = getSession();
        if (session != null) {
            return (String) session.getAttribute("adminid");
        } else {
            return null;
        }
    }
}