/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import controller.exceptions.IllegalOrphanException;
import controller.exceptions.NonexistentEntityException;
import controller.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import model.Department;
import model.Help;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import model.Document;
import model.UserAccount;

/**
 *
 * @author Garcia
 */
public class UserAccountJpaController implements Serializable {

    public UserAccountJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(UserAccount userAccount) throws PreexistingEntityException, Exception {
        if (userAccount.getHelpList() == null) {
            userAccount.setHelpList(new ArrayList<Help>());
        }
        if (userAccount.getDocumentList() == null) {
            userAccount.setDocumentList(new ArrayList<Document>());
        }
        if (userAccount.getDocumentList1() == null) {
            userAccount.setDocumentList1(new ArrayList<Document>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Department departmentIdDepartment = userAccount.getDepartmentIdDepartment();
            if (departmentIdDepartment != null) {
                departmentIdDepartment = em.getReference(departmentIdDepartment.getClass(), departmentIdDepartment.getIdDepartment());
                userAccount.setDepartmentIdDepartment(departmentIdDepartment);
            }
            List<Help> attachedHelpList = new ArrayList<Help>();
            for (Help helpListHelpToAttach : userAccount.getHelpList()) {
                helpListHelpToAttach = em.getReference(helpListHelpToAttach.getClass(), helpListHelpToAttach.getIdHelp());
                attachedHelpList.add(helpListHelpToAttach);
            }
            userAccount.setHelpList(attachedHelpList);
            List<Document> attachedDocumentList = new ArrayList<Document>();
            for (Document documentListDocumentToAttach : userAccount.getDocumentList()) {
                documentListDocumentToAttach = em.getReference(documentListDocumentToAttach.getClass(), documentListDocumentToAttach.getIdDocument());
                attachedDocumentList.add(documentListDocumentToAttach);
            }
            userAccount.setDocumentList(attachedDocumentList);
            List<Document> attachedDocumentList1 = new ArrayList<Document>();
            for (Document documentList1DocumentToAttach : userAccount.getDocumentList1()) {
                documentList1DocumentToAttach = em.getReference(documentList1DocumentToAttach.getClass(), documentList1DocumentToAttach.getIdDocument());
                attachedDocumentList1.add(documentList1DocumentToAttach);
            }
            userAccount.setDocumentList1(attachedDocumentList1);
            em.persist(userAccount);
            if (departmentIdDepartment != null) {
                departmentIdDepartment.getUserAccountList().add(userAccount);
                departmentIdDepartment = em.merge(departmentIdDepartment);
            }
            for (Help helpListHelp : userAccount.getHelpList()) {
                UserAccount oldUserAccountUserOfHelpListHelp = helpListHelp.getUserAccountUser();
                helpListHelp.setUserAccountUser(userAccount);
                helpListHelp = em.merge(helpListHelp);
                if (oldUserAccountUserOfHelpListHelp != null) {
                    oldUserAccountUserOfHelpListHelp.getHelpList().remove(helpListHelp);
                    oldUserAccountUserOfHelpListHelp = em.merge(oldUserAccountUserOfHelpListHelp);
                }
            }
            for (Document documentListDocument : userAccount.getDocumentList()) {
                UserAccount oldUserSenderOfDocumentListDocument = documentListDocument.getUserSender();
                documentListDocument.setUserSender(userAccount);
                documentListDocument = em.merge(documentListDocument);
                if (oldUserSenderOfDocumentListDocument != null) {
                    oldUserSenderOfDocumentListDocument.getDocumentList().remove(documentListDocument);
                    oldUserSenderOfDocumentListDocument = em.merge(oldUserSenderOfDocumentListDocument);
                }
            }
            for (Document documentList1Document : userAccount.getDocumentList1()) {
                UserAccount oldUserReceiverOfDocumentList1Document = documentList1Document.getUserReceiver();
                documentList1Document.setUserReceiver(userAccount);
                documentList1Document = em.merge(documentList1Document);
                if (oldUserReceiverOfDocumentList1Document != null) {
                    oldUserReceiverOfDocumentList1Document.getDocumentList1().remove(documentList1Document);
                    oldUserReceiverOfDocumentList1Document = em.merge(oldUserReceiverOfDocumentList1Document);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findUserAccount(userAccount.getUser()) != null) {
                throw new PreexistingEntityException("UserAccount " + userAccount + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(UserAccount userAccount) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            UserAccount persistentUserAccount = em.find(UserAccount.class, userAccount.getUser());
            Department departmentIdDepartmentOld = persistentUserAccount.getDepartmentIdDepartment();
            Department departmentIdDepartmentNew = userAccount.getDepartmentIdDepartment();
            List<Help> helpListOld = persistentUserAccount.getHelpList();
            List<Help> helpListNew = userAccount.getHelpList();
            List<Document> documentListOld = persistentUserAccount.getDocumentList();
            List<Document> documentListNew = userAccount.getDocumentList();
            List<Document> documentList1Old = persistentUserAccount.getDocumentList1();
            List<Document> documentList1New = userAccount.getDocumentList1();
            List<String> illegalOrphanMessages = null;
            for (Help helpListOldHelp : helpListOld) {
                if (!helpListNew.contains(helpListOldHelp)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Help " + helpListOldHelp + " since its userAccountUser field is not nullable.");
                }
            }
            for (Document documentListOldDocument : documentListOld) {
                if (!documentListNew.contains(documentListOldDocument)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Document " + documentListOldDocument + " since its userSender field is not nullable.");
                }
            }
            for (Document documentList1OldDocument : documentList1Old) {
                if (!documentList1New.contains(documentList1OldDocument)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Document " + documentList1OldDocument + " since its userReceiver field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (departmentIdDepartmentNew != null) {
                departmentIdDepartmentNew = em.getReference(departmentIdDepartmentNew.getClass(), departmentIdDepartmentNew.getIdDepartment());
                userAccount.setDepartmentIdDepartment(departmentIdDepartmentNew);
            }
            List<Help> attachedHelpListNew = new ArrayList<Help>();
            for (Help helpListNewHelpToAttach : helpListNew) {
                helpListNewHelpToAttach = em.getReference(helpListNewHelpToAttach.getClass(), helpListNewHelpToAttach.getIdHelp());
                attachedHelpListNew.add(helpListNewHelpToAttach);
            }
            helpListNew = attachedHelpListNew;
            userAccount.setHelpList(helpListNew);
            List<Document> attachedDocumentListNew = new ArrayList<Document>();
            for (Document documentListNewDocumentToAttach : documentListNew) {
                documentListNewDocumentToAttach = em.getReference(documentListNewDocumentToAttach.getClass(), documentListNewDocumentToAttach.getIdDocument());
                attachedDocumentListNew.add(documentListNewDocumentToAttach);
            }
            documentListNew = attachedDocumentListNew;
            userAccount.setDocumentList(documentListNew);
            List<Document> attachedDocumentList1New = new ArrayList<Document>();
            for (Document documentList1NewDocumentToAttach : documentList1New) {
                documentList1NewDocumentToAttach = em.getReference(documentList1NewDocumentToAttach.getClass(), documentList1NewDocumentToAttach.getIdDocument());
                attachedDocumentList1New.add(documentList1NewDocumentToAttach);
            }
            documentList1New = attachedDocumentList1New;
            userAccount.setDocumentList1(documentList1New);
            userAccount = em.merge(userAccount);
            if (departmentIdDepartmentOld != null && !departmentIdDepartmentOld.equals(departmentIdDepartmentNew)) {
                departmentIdDepartmentOld.getUserAccountList().remove(userAccount);
                departmentIdDepartmentOld = em.merge(departmentIdDepartmentOld);
            }
            if (departmentIdDepartmentNew != null && !departmentIdDepartmentNew.equals(departmentIdDepartmentOld)) {
                departmentIdDepartmentNew.getUserAccountList().add(userAccount);
                departmentIdDepartmentNew = em.merge(departmentIdDepartmentNew);
            }
            for (Help helpListNewHelp : helpListNew) {
                if (!helpListOld.contains(helpListNewHelp)) {
                    UserAccount oldUserAccountUserOfHelpListNewHelp = helpListNewHelp.getUserAccountUser();
                    helpListNewHelp.setUserAccountUser(userAccount);
                    helpListNewHelp = em.merge(helpListNewHelp);
                    if (oldUserAccountUserOfHelpListNewHelp != null && !oldUserAccountUserOfHelpListNewHelp.equals(userAccount)) {
                        oldUserAccountUserOfHelpListNewHelp.getHelpList().remove(helpListNewHelp);
                        oldUserAccountUserOfHelpListNewHelp = em.merge(oldUserAccountUserOfHelpListNewHelp);
                    }
                }
            }
            for (Document documentListNewDocument : documentListNew) {
                if (!documentListOld.contains(documentListNewDocument)) {
                    UserAccount oldUserSenderOfDocumentListNewDocument = documentListNewDocument.getUserSender();
                    documentListNewDocument.setUserSender(userAccount);
                    documentListNewDocument = em.merge(documentListNewDocument);
                    if (oldUserSenderOfDocumentListNewDocument != null && !oldUserSenderOfDocumentListNewDocument.equals(userAccount)) {
                        oldUserSenderOfDocumentListNewDocument.getDocumentList().remove(documentListNewDocument);
                        oldUserSenderOfDocumentListNewDocument = em.merge(oldUserSenderOfDocumentListNewDocument);
                    }
                }
            }
            for (Document documentList1NewDocument : documentList1New) {
                if (!documentList1Old.contains(documentList1NewDocument)) {
                    UserAccount oldUserReceiverOfDocumentList1NewDocument = documentList1NewDocument.getUserReceiver();
                    documentList1NewDocument.setUserReceiver(userAccount);
                    documentList1NewDocument = em.merge(documentList1NewDocument);
                    if (oldUserReceiverOfDocumentList1NewDocument != null && !oldUserReceiverOfDocumentList1NewDocument.equals(userAccount)) {
                        oldUserReceiverOfDocumentList1NewDocument.getDocumentList1().remove(documentList1NewDocument);
                        oldUserReceiverOfDocumentList1NewDocument = em.merge(oldUserReceiverOfDocumentList1NewDocument);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = userAccount.getUser();
                if (findUserAccount(id) == null) {
                    throw new NonexistentEntityException("The userAccount with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            UserAccount userAccount;
            try {
                userAccount = em.getReference(UserAccount.class, id);
                userAccount.getUser();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The userAccount with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Help> helpListOrphanCheck = userAccount.getHelpList();
            for (Help helpListOrphanCheckHelp : helpListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This UserAccount (" + userAccount + ") cannot be destroyed since the Help " + helpListOrphanCheckHelp + " in its helpList field has a non-nullable userAccountUser field.");
            }
            List<Document> documentListOrphanCheck = userAccount.getDocumentList();
            for (Document documentListOrphanCheckDocument : documentListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This UserAccount (" + userAccount + ") cannot be destroyed since the Document " + documentListOrphanCheckDocument + " in its documentList field has a non-nullable userSender field.");
            }
            List<Document> documentList1OrphanCheck = userAccount.getDocumentList1();
            for (Document documentList1OrphanCheckDocument : documentList1OrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This UserAccount (" + userAccount + ") cannot be destroyed since the Document " + documentList1OrphanCheckDocument + " in its documentList1 field has a non-nullable userReceiver field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Department departmentIdDepartment = userAccount.getDepartmentIdDepartment();
            if (departmentIdDepartment != null) {
                departmentIdDepartment.getUserAccountList().remove(userAccount);
                departmentIdDepartment = em.merge(departmentIdDepartment);
            }
            em.remove(userAccount);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<UserAccount> findUserAccountEntities() {
        return findUserAccountEntities(true, -1, -1);
    }

    public List<UserAccount> findUserAccountEntities(int maxResults, int firstResult) {
        return findUserAccountEntities(false, maxResults, firstResult);
    }

    private List<UserAccount> findUserAccountEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(UserAccount.class));
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

    public UserAccount findUserAccount(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(UserAccount.class, id);
        } finally {
            em.close();
        }
    }

    public int getUserAccountCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<UserAccount> rt = cq.from(UserAccount.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
