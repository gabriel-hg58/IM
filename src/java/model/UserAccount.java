/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Garcia
 */
@Entity
@Table(name = "user_account")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UserAccount.findAll", query = "SELECT u FROM UserAccount u"),
    @NamedQuery(name = "UserAccount.findByUser", query = "SELECT u FROM UserAccount u WHERE u.user = :user"),
    @NamedQuery(name = "UserAccount.findByUserCode", query = "SELECT u FROM UserAccount u WHERE u.userCode = :userCode"),
    @NamedQuery(name = "UserAccount.findByPassword", query = "SELECT u FROM UserAccount u WHERE u.password = :password"),
    @NamedQuery(name = "UserAccount.findByName", query = "SELECT u FROM UserAccount u WHERE u.name = :name"),
    @NamedQuery(name = "UserAccount.findByPhone", query = "SELECT u FROM UserAccount u WHERE u.phone = :phone"),
    @NamedQuery(name = "UserAccount.findByEmail", query = "SELECT u FROM UserAccount u WHERE u.email = :email"),
    @NamedQuery(name = "UserAccount.findByAdministrator", query = "SELECT u FROM UserAccount u WHERE u.administrator = :administrator")})
public class UserAccount implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "user")
    private String user;
    @Basic(optional = false)
    @Column(name = "user_code")
    private int userCode;
    @Basic(optional = false)
    @Column(name = "password")
    private String password;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @Column(name = "phone")
    private String phone;
    @Basic(optional = false)
    @Column(name = "email")
    private String email;
    @Basic(optional = false)
    @Column(name = "administrator")
    private boolean administrator;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userAccountUser")
    private List<Help> helpList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userSender")
    private List<Document> documentList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userReceiver")
    private List<Document> documentList1;
    @JoinColumn(name = "department_id_department", referencedColumnName = "id_department")
    @ManyToOne(optional = false)
    private Department departmentIdDepartment;

    public UserAccount() {
    }

    public UserAccount(String user) {
        this.user = user;
    }

    public UserAccount(String user, int userCode, String password, String name, String phone, String email, boolean administrator) {
        this.user = user;
        this.userCode = userCode;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.administrator = administrator;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getUserCode() {
        return userCode;
    }

    public void setUserCode(int userCode) {
        this.userCode = userCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean getAdministrator() {
        return administrator;
    }

    public void setAdministrator(boolean administrator) {
        this.administrator = administrator;
    }

    @XmlTransient
    public List<Help> getHelpList() {
        return helpList;
    }

    public void setHelpList(List<Help> helpList) {
        this.helpList = helpList;
    }

    @XmlTransient
    public List<Document> getDocumentList() {
        return documentList;
    }

    public void setDocumentList(List<Document> documentList) {
        this.documentList = documentList;
    }

    @XmlTransient
    public List<Document> getDocumentList1() {
        return documentList1;
    }

    public void setDocumentList1(List<Document> documentList1) {
        this.documentList1 = documentList1;
    }

    public Department getDepartmentIdDepartment() {
        return departmentIdDepartment;
    }

    public void setDepartmentIdDepartment(Department departmentIdDepartment) {
        this.departmentIdDepartment = departmentIdDepartment;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (user != null ? user.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserAccount)) {
            return false;
        }
        UserAccount other = (UserAccount) object;
        if ((this.user == null && other.user != null) || (this.user != null && !this.user.equals(other.user))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.UserAccount[ user=" + user + " ]";
    }
    
}
