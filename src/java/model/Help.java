/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Garcia
 */
@Entity
@Table(name = "help")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Help.findAll", query = "SELECT h FROM Help h"),
    @NamedQuery(name = "Help.findByIdHelp", query = "SELECT h FROM Help h WHERE h.idHelp = :idHelp"),
    @NamedQuery(name = "Help.findByTitleHelp", query = "SELECT h FROM Help h WHERE h.titleHelp = :titleHelp"),
    @NamedQuery(name = "Help.findByDescription", query = "SELECT h FROM Help h WHERE h.description = :description")})
public class Help implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_help")
    private Integer idHelp;
    @Basic(optional = false)
    @Column(name = "title_help")
    private String titleHelp;
    @Basic(optional = false)
    @Column(name = "description")
    private String description;
    @JoinColumn(name = "user_account_user", referencedColumnName = "user")
    @ManyToOne(optional = false)
    private UserAccount userAccountUser;

    public Help() {
    }

    public Help(Integer idHelp) {
        this.idHelp = idHelp;
    }

    public Help(Integer idHelp, String titleHelp, String description) {
        this.idHelp = idHelp;
        this.titleHelp = titleHelp;
        this.description = description;
    }

    public Integer getIdHelp() {
        return idHelp;
    }

    public void setIdHelp(Integer idHelp) {
        this.idHelp = idHelp;
    }

    public String getTitleHelp() {
        return titleHelp;
    }

    public void setTitleHelp(String titleHelp) {
        this.titleHelp = titleHelp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserAccount getUserAccountUser() {
        return userAccountUser;
    }

    public void setUserAccountUser(UserAccount userAccountUser) {
        this.userAccountUser = userAccountUser;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idHelp != null ? idHelp.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Help)) {
            return false;
        }
        Help other = (Help) object;
        if ((this.idHelp == null && other.idHelp != null) || (this.idHelp != null && !this.idHelp.equals(other.idHelp))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.Help[ idHelp=" + idHelp + " ]";
    }
    
}
