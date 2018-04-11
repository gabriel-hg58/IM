/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import controller.exceptions.NonexistentEntityException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import model.Type;
import model.Department;
import model.Document;
import model.UserAccount;

/**
 *
 * @author gabri
 */
public class DocumentJpaController implements Serializable {

    public DocumentJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Document document) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Type typeIdType = document.getTypeIdType();
            if (typeIdType != null) {
                typeIdType = em.getReference(typeIdType.getClass(), typeIdType.getIdType());
                document.setTypeIdType(typeIdType);
            }
            Department departmentIdDepartment = document.getDepartmentIdDepartment();
            if (departmentIdDepartment != null) {
                departmentIdDepartment = em.getReference(departmentIdDepartment.getClass(), departmentIdDepartment.getIdDepartment());
                document.setDepartmentIdDepartment(departmentIdDepartment);
            }
            UserAccount userSender = document.getUserSender();
            if (userSender != null) {
                userSender = em.getReference(userSender.getClass(), userSender.getUser());
                document.setUserSender(userSender);
            }
            UserAccount userReceiver = document.getUserReceiver();
            if (userReceiver != null) {
                userReceiver = em.getReference(userReceiver.getClass(), userReceiver.getUser());
                document.setUserReceiver(userReceiver);
            }
            em.persist(document);
            if (typeIdType != null) {
                typeIdType.getDocumentList().add(document);
                typeIdType = em.merge(typeIdType);
            }
            if (departmentIdDepartment != null) {
                departmentIdDepartment.getDocumentList().add(document);
                departmentIdDepartment = em.merge(departmentIdDepartment);
            }
            if (userSender != null) {
                userSender.getDocumentList().add(document);
                userSender = em.merge(userSender);
            }
            if (userReceiver != null) {
                userReceiver.getDocumentList().add(document);
                userReceiver = em.merge(userReceiver);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Document document) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Document persistentDocument = em.find(Document.class, document.getIdDocument());
            Type typeIdTypeOld = persistentDocument.getTypeIdType();
            Type typeIdTypeNew = document.getTypeIdType();
            Department departmentIdDepartmentOld = persistentDocument.getDepartmentIdDepartment();
            Department departmentIdDepartmentNew = document.getDepartmentIdDepartment();
            UserAccount userSenderOld = persistentDocument.getUserSender();
            UserAccount userSenderNew = document.getUserSender();
            UserAccount userReceiverOld = persistentDocument.getUserReceiver();
            UserAccount userReceiverNew = document.getUserReceiver();
            if (typeIdTypeNew != null) {
                typeIdTypeNew = em.getReference(typeIdTypeNew.getClass(), typeIdTypeNew.getIdType());
                document.setTypeIdType(typeIdTypeNew);
            }
            if (departmentIdDepartmentNew != null) {
                departmentIdDepartmentNew = em.getReference(departmentIdDepartmentNew.getClass(), departmentIdDepartmentNew.getIdDepartment());
                document.setDepartmentIdDepartment(departmentIdDepartmentNew);
            }
            if (userSenderNew != null) {
                userSenderNew = em.getReference(userSenderNew.getClass(), userSenderNew.getUser());
                document.setUserSender(userSenderNew);
            }
            if (userReceiverNew != null) {
                userReceiverNew = em.getReference(userReceiverNew.getClass(), userReceiverNew.getUser());
                document.setUserReceiver(userReceiverNew);
            }
            document = em.merge(document);
            if (typeIdTypeOld != null && !typeIdTypeOld.equals(typeIdTypeNew)) {
                typeIdTypeOld.getDocumentList().remove(document);
                typeIdTypeOld = em.merge(typeIdTypeOld);
            }
            if (typeIdTypeNew != null && !typeIdTypeNew.equals(typeIdTypeOld)) {
                typeIdTypeNew.getDocumentList().add(document);
                typeIdTypeNew = em.merge(typeIdTypeNew);
            }
            if (departmentIdDepartmentOld != null && !departmentIdDepartmentOld.equals(departmentIdDepartmentNew)) {
                departmentIdDepartmentOld.getDocumentList().remove(document);
                departmentIdDepartmentOld = em.merge(departmentIdDepartmentOld);
            }
            if (departmentIdDepartmentNew != null && !departmentIdDepartmentNew.equals(departmentIdDepartmentOld)) {
                departmentIdDepartmentNew.getDocumentList().add(document);
                departmentIdDepartmentNew = em.merge(departmentIdDepartmentNew);
            }
            if (userSenderOld != null && !userSenderOld.equals(userSenderNew)) {
                userSenderOld.getDocumentList().remove(document);
                userSenderOld = em.merge(userSenderOld);
            }
            if (userSenderNew != null && !userSenderNew.equals(userSenderOld)) {
                userSenderNew.getDocumentList().add(document);
                userSenderNew = em.merge(userSenderNew);
            }
            if (userReceiverOld != null && !userReceiverOld.equals(userReceiverNew)) {
                userReceiverOld.getDocumentList().remove(document);
                userReceiverOld = em.merge(userReceiverOld);
            }
            if (userReceiverNew != null && !userReceiverNew.equals(userReceiverOld)) {
                userReceiverNew.getDocumentList().add(document);
                userReceiverNew = em.merge(userReceiverNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = document.getIdDocument();
                if (findDocument(id) == null) {
                    throw new NonexistentEntityException("The document with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Document document;
            try {
                document = em.getReference(Document.class, id);
                document.getIdDocument();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The document with id " + id + " no longer exists.", enfe);
            }
            Type typeIdType = document.getTypeIdType();
            if (typeIdType != null) {
                typeIdType.getDocumentList().remove(document);
                typeIdType = em.merge(typeIdType);
            }
            Department departmentIdDepartment = document.getDepartmentIdDepartment();
            if (departmentIdDepartment != null) {
                departmentIdDepartment.getDocumentList().remove(document);
                departmentIdDepartment = em.merge(departmentIdDepartment);
            }
            UserAccount userSender = document.getUserSender();
            if (userSender != null) {
                userSender.getDocumentList().remove(document);
                userSender = em.merge(userSender);
            }
            UserAccount userReceiver = document.getUserReceiver();
            if (userReceiver != null) {
                userReceiver.getDocumentList().remove(document);
                userReceiver = em.merge(userReceiver);
            }
            em.remove(document);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Document> findDocumentEntities() {
        return findDocumentEntities(true, -1, -1);
    }

    public List<Document> findDocumentEntities(int maxResults, int firstResult) {
        return findDocumentEntities(false, maxResults, firstResult);
    }

    private List<Document> findDocumentEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Document.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Document findDocument(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Document.class, id);
        } finally {
            em.close();
        }
    }

    public int getDocumentCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Document> rt = cq.from(Document.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
