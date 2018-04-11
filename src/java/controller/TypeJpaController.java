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
import model.Type;

/**
 *
 * @author gabri
 */
public class TypeJpaController implements Serializable {

    public TypeJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Type type) {
        if (type.getDocumentList() == null) {
            type.setDocumentList(new ArrayList<Document>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Document> attachedDocumentList = new ArrayList<Document>();
            for (Document documentListDocumentToAttach : type.getDocumentList()) {
                documentListDocumentToAttach = em.getReference(documentListDocumentToAttach.getClass(), documentListDocumentToAttach.getIdDocument());
                attachedDocumentList.add(documentListDocumentToAttach);
            }
            type.setDocumentList(attachedDocumentList);
            em.persist(type);
            for (Document documentListDocument : type.getDocumentList()) {
                Type oldTypeIdTypeOfDocumentListDocument = documentListDocument.getTypeIdType();
                documentListDocument.setTypeIdType(type);
                documentListDocument = em.merge(documentListDocument);
                if (oldTypeIdTypeOfDocumentListDocument != null) {
                    oldTypeIdTypeOfDocumentListDocument.getDocumentList().remove(documentListDocument);
                    oldTypeIdTypeOfDocumentListDocument = em.merge(oldTypeIdTypeOfDocumentListDocument);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Type type) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Type persistentType = em.find(Type.class, type.getIdType());
            List<Document> documentListOld = persistentType.getDocumentList();
            List<Document> documentListNew = type.getDocumentList();
            List<String> illegalOrphanMessages = null;
            for (Document documentListOldDocument : documentListOld) {
                if (!documentListNew.contains(documentListOldDocument)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Document " + documentListOldDocument + " since its typeIdType field is not nullable.");
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
            type.setDocumentList(documentListNew);
            type = em.merge(type);
            for (Document documentListNewDocument : documentListNew) {
                if (!documentListOld.contains(documentListNewDocument)) {
                    Type oldTypeIdTypeOfDocumentListNewDocument = documentListNewDocument.getTypeIdType();
                    documentListNewDocument.setTypeIdType(type);
                    documentListNewDocument = em.merge(documentListNewDocument);
                    if (oldTypeIdTypeOfDocumentListNewDocument != null && !oldTypeIdTypeOfDocumentListNewDocument.equals(type)) {
                        oldTypeIdTypeOfDocumentListNewDocument.getDocumentList().remove(documentListNewDocument);
                        oldTypeIdTypeOfDocumentListNewDocument = em.merge(oldTypeIdTypeOfDocumentListNewDocument);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = type.getIdType();
                if (findType(id) == null) {
                    throw new NonexistentEntityException("The type with id " + id + " no longer exists.");
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
            Type type;
            try {
                type = em.getReference(Type.class, id);
                type.getIdType();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The type with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Document> documentListOrphanCheck = type.getDocumentList();
            for (Document documentListOrphanCheckDocument : documentListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Type (" + type + ") cannot be destroyed since the Document " + documentListOrphanCheckDocument + " in its documentList field has a non-nullable typeIdType field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(type);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Type> findTypeEntities() {
        return findTypeEntities(true, -1, -1);
    }

    public List<Type> findTypeEntities(int maxResults, int firstResult) {
        return findTypeEntities(false, maxResults, firstResult);
    }

    private List<Type> findTypeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Type.class));
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

    public Type findType(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Type.class, id);
        } finally {
            em.close();
        }
    }

    public int getTypeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Type> rt = cq.from(Type.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
