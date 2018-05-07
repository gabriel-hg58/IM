package view;

import controller.HelpJpaController;
import controller.UserAccountJpaController;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import model.Help;
import model.UserAccount;
import utils.SendMail;

@ManagedBean
@SessionScoped
public class HelpManagedBean {
    
    //Control
    HelpJpaController controlHelp = new HelpJpaController(EmProvider.getInstance().getEntityManagerFactory());
    UserAccountJpaController controlUser = new UserAccountJpaController(EmProvider.getInstance().getEntityManagerFactory());
    //Auxiliary
    Help actualHelp = new Help();
    UserAccount actualUser = new UserAccount();
    SendMail send = new SendMail();
    
    public HelpManagedBean() {
    }
    
    //  --------------------  Redirect Metods  --------------------
    public String gotoHelp(){
        return "/public/manageAccounts/help.xhtml?faces-redirect=true";
    }
    
    //  ---------------------  Public Metods  --------------------- 
    public void clean(){
        actualHelp = new Help();
    }
    public String saveHelp(){
        actualUser = controlUser.findUserAccount(ManageSessions.getUserId());
        actualHelp.setUserAccountUser(actualUser);
        try {
            controlHelp.create(actualHelp);
            send.sendMailToSystem(actualHelp.getIdHelp(), actualUser.getUser());
            return "/public/manageDocuments/documentsReceived.xhtml?faces-redirect=true";
        } catch (Exception e) {
            FacesMessage message = new FacesMessage("Erro!", "Não foi enviar sua requisição.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        return "#";
    }
    
    //  ---------------------  Getters and Setters  ---------------------
    public HelpJpaController getControlHelp() {
        return controlHelp;
    }

    public void setControlHelp(HelpJpaController controlHelp) {
        this.controlHelp = controlHelp;
    }

    public Help getActualHelp() {
        return actualHelp;
    }

    public void setActualHelp(Help actualHelp) {
        this.actualHelp = actualHelp;
    }
    
}
