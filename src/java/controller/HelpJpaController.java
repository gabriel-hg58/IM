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
import model.Help;
import model.UserAccount;

/**
 *
 * @author gabri
 */
public class HelpJpaController implements Serializable {

    public HelpJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Help help) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            UserAccount userAccountUserCode = help.getUserAccountUserCode();
            if (userAccountUserCode != null) {
                userAccountUserCode = em.getReference(userAccountUserCode.getClass(), userAccountUserCode.getUser());
                help.setUserAccountUserCode(userAccountUserCode);
            }
            em.persist(help);
            if (userAccountUserCode != null) {
                userAccountUserCode.getHelpList().add(help);
                userAccountUserCode = em.merge(userAccountUserCode);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Help help) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Help persistentHelp = em.find(Help.class, help.getIdHelp());
            UserAccount userAccountUserCodeOld = persistentHelp.getUserAccountUserCode();
            UserAccount userAccountUserCodeNew = help.getUserAccountUserCode();
            if (userAccountUserCodeNew != null) {
                userAccountUserCodeNew = em.getReference(userAccountUserCodeNew.getClass(), userAccountUserCodeNew.getUser());
                help.setUserAccountUserCode(userAccountUserCodeNew);
            }
            help = em.merge(help);
            if (userAccountUserCodeOld != null && !userAccountUserCodeOld.equals(userAccountUserCodeNew)) {
                userAccountUserCodeOld.getHelpList().remove(help);
                userAccountUserCodeOld = em.merge(userAccountUserCodeOld);
            }
            if (userAccountUserCodeNew != null && !userAccountUserCodeNew.equals(userAccountUserCodeOld)) {
                userAccountUserCodeNew.getHelpList().add(help);
                userAccountUserCodeNew = em.merge(userAccountUserCodeNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = help.getIdHelp();
                if (findHelp(id) == null) {
                    throw new NonexistentEntityException("The help with id " + id + " no longer exists.");
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
            Help help;
            try {
                help = em.getReference(Help.class, id);
                help.getIdHelp();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The help with id " + id + " no longer exists.", enfe);
            }
            UserAccount userAccountUserCode = help.getUserAccountUserCode();
            if (userAccountUserCode != null) {
                userAccountUserCode.getHelpList().remove(help);
                userAccountUserCode = em.merge(userAccountUserCode);
            }
            em.remove(help);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Help> findHelpEntities() {
        return findHelpEntities(true, -1, -1);
    }

    public List<Help> findHelpEntities(int maxResults, int firstResult) {
        return findHelpEntities(false, maxResults, firstResult);
    }

    private List<Help> findHelpEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Help.class));
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

    public Help findHelp(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Help.class, id);
        } finally {
            em.close();
        }
    }

    public int getHelpCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Help> rt = cq.from(Help.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
