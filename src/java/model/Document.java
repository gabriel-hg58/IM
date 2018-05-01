/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Garcia
 */
@Entity
@Table(name = "document")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Document.findAll", query = "SELECT d FROM Document d"),
    @NamedQuery(name = "Document.findByIdDocument", query = "SELECT d FROM Document d WHERE d.idDocument = :idDocument"),
    @NamedQuery(name = "Document.findByNumber", query = "SELECT d FROM Document d WHERE d.number = :number"),
    @NamedQuery(name = "Document.findByYear", query = "SELECT d FROM Document d WHERE d.year = :year"),
    @NamedQuery(name = "Document.findByPublicationDate", query = "SELECT d FROM Document d WHERE d.publicationDate = :publicationDate"),
    @NamedQuery(name = "Document.findBySituation", query = "SELECT d FROM Document d WHERE d.situation = :situation"),
    @NamedQuery(name = "Document.findByDocExtension", query = "SELECT d FROM Document d WHERE d.docExtension = :docExtension")})
public class Document implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_document")
    private Integer idDocument;
    @Basic(optional = false)
    @Column(name = "number")
    private int number;
    @Basic(optional = false)
    @Column(name = "year")
    private String year;
    @Basic(optional = false)
    @Column(name = "publication_date")
    @Temporal(TemporalType.DATE)
    private Date publicationDate;
    @Basic(optional = false)
    @Column(name = "situation")
    private int situation;
    @Basic(optional = false)
    @Lob
    @Column(name = "document")
    private byte[] document;
    @Basic(optional = false)
    @Column(name = "doc_extension")
    private String docExtension;
    @Lob
    @Column(name = "denial_explanation")
    private byte[] denialExplanation;
    @JoinColumn(name = "type_id_type", referencedColumnName = "id_type")
    @ManyToOne(optional = false)
    private Type typeIdType;
    @JoinColumn(name = "department_id_department", referencedColumnName = "id_department")
    @ManyToOne(optional = false)
    private Department departmentIdDepartment;
    @JoinColumn(name = "user_sender", referencedColumnName = "user")
    @ManyToOne(optional = false)
    private UserAccount userSender;
    @JoinColumn(name = "user_receiver", referencedColumnName = "user")
    @ManyToOne(optional = false)
    private UserAccount userReceiver;

    public Document() {
    }

    public Document(Integer idDocument) {
        this.idDocument = idDocument;
    }

    public Document(Integer idDocument, int number, String year, Date publicationDate, int situation, byte[] document, String docExtension) {
        this.idDocument = idDocument;
        this.number = number;
        this.year = year;
        this.publicationDate = publicationDate;
        this.situation = situation;
        this.document = document;
        this.docExtension = docExtension;
    }

    public Integer getIdDocument() {
        return idDocument;
    }

    public void setIdDocument(Integer idDocument) {
        this.idDocument = idDocument;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public int getSituation() {
        return situation;
    }

    public void setSituation(int situation) {
        this.situation = situation;
    }

    public byte[] getDocument() {
        return document;
    }

    public void setDocument(byte[] document) {
        this.document = document;
    }

    public String getDocExtension() {
        return docExtension;
    }

    public void setDocExtension(String docExtension) {
        this.docExtension = docExtension;
    }

    public byte[] getDenialExplanation() {
        return denialExplanation;
    }

    public void setDenialExplanation(byte[] denialExplanation) {
        this.denialExplanation = denialExplanation;
    }

    public Type getTypeIdType() {
        return typeIdType;
    }

    public void setTypeIdType(Type typeIdType) {
        this.typeIdType = typeIdType;
    }

    public Department getDepartmentIdDepartment() {
        return departmentIdDepartment;
    }

    public void setDepartmentIdDepartment(Department departmentIdDepartment) {
        this.departmentIdDepartment = departmentIdDepartment;
    }

    public UserAccount getUserSender() {
        return userSender;
    }

    public void setUserSender(UserAccount userSender) {
        this.userSender = userSender;
    }

    public UserAccount getUserReceiver() {
        return userReceiver;
    }

    public void setUserReceiver(UserAccount userReceiver) {
        this.userReceiver = userReceiver;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idDocument != null ? idDocument.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Document)) {
            return false;
        }
        Document other = (Document) object;
        if ((this.idDocument == null && other.idDocument != null) || (this.idDocument != null && !this.idDocument.equals(other.idDocument))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.Document[ idDocument=" + idDocument + " ]";
    }
    
}
