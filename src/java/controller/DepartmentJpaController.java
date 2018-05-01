/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import controller.exceptions.IllegalOrphanException;
import controller.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import model.Document;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import model.Department;
import model.UserAccount;

/**
 *
 * @author Garcia
 */
public class DepartmentJpaController implements Serializable {

    public DepartmentJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Department department) {
        if (department.getDocumentList() == null) {
            department.setDocumentList(new ArrayList<Document>());
        }
        if (department.getUserAccountList() == null) {
            department.setUserAccountList(new ArrayList<UserAccount>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Document> attachedDocumentList = new ArrayList<Document>();
            for (Document documentListDocumentToAttach : department.getDocumentList()) {
                documentListDocumentToAttach = em.getReference(documentListDocumentToAttach.getClass(), documentListDocumentToAttach.getIdDocument());
                attachedDocumentList.add(documentListDocumentToAttach);
            }
            department.setDocumentList(attachedDocumentList);
            List<UserAccount> attachedUserAccountList = new ArrayList<UserAccount>();
            for (UserAccount userAccountListUserAccountToAttach : department.getUserAccountList()) {
                userAccountListUserAccountToAttach = em.getReference(userAccountListUserAccountToAttach.getClass(), userAccountListUserAccountToAttach.getUser());
                attachedUserAccountList.add(userAccountListUserAccountToAttach);
            }
            department.setUserAccountList(attachedUserAccountList);
            em.persist(department);
            for (Document documentListDocument : department.getDocumentList()) {
                Department oldDepartmentIdDepartmentOfDocumentListDocument = documentListDocument.getDepartmentIdDepartment();
                documentListDocument.setDepartmentIdDepartment(department);
                documentListDocument = em.merge(documentListDocument);
                if (oldDepartmentIdDepartmentOfDocumentListDocument != null) {
                    oldDepartmentIdDepartmentOfDocumentListDocument.getDocumentList().remove(documentListDocument);
                    oldDepartmentIdDepartmentOfDocumentListDocument = em.merge(oldDepartmentIdDepartmentOfDocumentListDocument);
                }
            }
            for (UserAccount userAccountListUserAccount : department.getUserAccountList()) {
                Department oldDepartmentIdDepartmentOfUserAccountListUserAccount = userAccountListUserAccount.getDepartmentIdDepartment();
                userAccountListUserAccount.setDepartmentIdDepartment(department);
                userAccountListUserAccount = em.merge(userAccountListUserAccount);
                if (oldDepartmentIdDepartmentOfUserAccountListUserAccount != null) {
                    oldDepartmentIdDepartmentOfUserAccountListUserAccount.getUserAccountList().remove(userAccountListUserAccount);
                    oldDepartmentIdDepartmentOfUserAccountListUserAccount = em.merge(oldDepartmentIdDepartmentOfUserAccountListUserAccount);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Department department) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Department persistentDepartment = em.find(Department.class, department.getIdDepartment());
            List<Document> documentListOld = persistentDepartment.getDocumentList();
            List<Document> documentListNew = department.getDocumentList();
            List<UserAccount> userAccountListOld = persistentDepartment.getUserAccountList();
            List<UserAccount> userAccountListNew = department.getUserAccountList();
            List<String> illegalOrphanMessages = null;
            for (Document documentListOldDocument : documentListOld) {
                if (!documentListNew.contains(documentListOldDocument)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Document " + documentListOldDocument + " since its departmentIdDepartment field is not nullable.");
                }
            }
            for (UserAccount userAccountListOldUserAccount : userAccountListOld) {
                if (!userAccountListNew.contains(userAccountListOldUserAccount)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain UserAccount " + userAccountListOldUserAccount + " since its departmentIdDepartment field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Document> attachedDocumentListNew = new ArrayList<Document>();
            for (Document documentListNewDocumentToAttach : documentListNew) {
                documentListNewDocumentToAttach = em.getReference(documentListNewDocumentToAttach.getClass(), documentListNewDocumentToAttach.getIdDocument());
                attachedDocumentListNew.add(documentListNewDocumentToAttach);
            }
            documentListNew = attachedDocumentListNew;
            department.setDocumentList(documentListNew);
            List<UserAccount> attachedUserAccountListNew = new ArrayList<UserAccount>();
            for (UserAccount userAccountListNewUserAccountToAttach : userAccountListNew) {
                userAccountListNewUserAccountToAttach = em.getReference(userAccountListNewUserAccountToAttach.getClass(), userAccountListNewUserAccountToAttach.getUser());
                attachedUserAccountListNew.add(userAccountListNewUserAccountToAttach);
            }
            userAccountListNew = attachedUserAccountListNew;
            department.setUserAccountList(userAccountListNew);
            department = em.merge(department);
            for (Document documentListNewDocument : documentListNew) {
                if (!documentListOld.contains(documentListNewDocument)) {
                    Department oldDepartmentIdDepartmentOfDocumentListNewDocument = documentListNewDocument.getDepartmentIdDepartment();
                    documentListNewDocument.setDepartmentIdDepartment(department);
                    documentListNewDocument = em.merge(documentListNewDocument);
                    if (oldDepartmentIdDepartmentOfDocumentListNewDocument != null && !oldDepartmentIdDepartmentOfDocumentListNewDocument.equals(department)) {
                        oldDepartmentIdDepartmentOfDocumentListNewDocument.getDocumentList().remove(documentListNewDocument);
                        oldDepartmentIdDepartmentOfDocumentListNewDocument = em.merge(oldDepartmentIdDepartmentOfDocumentListNewDocument);
                    }
                }
            }
            for (UserAccount userAccountListNewUserAccount : userAccountListNew) {
                if (!userAccountListOld.contains(userAccountListNewUserAccount)) {
                    Department oldDepartmentIdDepartmentOfUserAccountListNewUserAccount = userAccountListNewUserAccount.getDepartmentIdDepartment();
                    userAccountListNewUserAccount.setDepartmentIdDepartment(department);
                    userAccountListNewUserAccount = em.merge(userAccountListNewUserAccount);
                    if (oldDepartmentIdDepartmentOfUserAccountListNewUserAccount != null && !oldDepartmentIdDepartmentOfUserAccountListNewUserAccount.equals(department)) {
                        oldDepartmentIdDepartmentOfUserAccountListNewUserAccount.getUserAccountList().remove(userAccountListNewUserAccount);
                        oldDepartmentIdDepartmentOfUserAccountListNewUserAccount = em.merge(oldDepartmentIdDepartmentOfUserAccountListNewUserAccount);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = department.getIdDepartment();
                if (findDepartment(id) == null) {
                    throw new NonexistentEntityException("The department with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Department department;
            try {
                department = em.getReference(Department.class, id);
                department.getIdDepartment();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The department with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Document> documentListOrphanCheck = department.getDocumentList();
            for (Document documentListOrphanCheckDocument : documentListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Department (" + department + ") cannot be destroyed since the Document " + documentListOrphanCheckDocument + " in its documentList field has a non-nullable departmentIdDepartment field.");
            }
            List<UserAccount> userAccountListOrphanCheck = department.getUserAccountList();
            for (UserAccount userAccountListOrphanCheckUserAccount : userAccountListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Department (" + department + ") cannot be destroyed since the UserAccount " + userAccountListOrphanCheckUserAccount + " in its userAccountList field has a non-nullable departmentIdDepartment field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(department);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Department> findDepartmentEntities() {
        return findDepartmentEntities(true, -1, -1);
    }

    public List<Department> findDepartmentEntities(int maxResults, int firstResult) {
        return findDepartmentEntities(false, maxResults, firstResult);
    }

    private List<Department> findDepartmentEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Department.class));
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

    public Department findDepartment(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Department.class, id);
        } finally {
            em.close();
        }
    }

    public int getDepartmentCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Department> rt = cq.from(Department.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
