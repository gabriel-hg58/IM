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
 * @author Garcia
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
            UserAccount userAccountUser = help.getUserAccountUser();
            if (userAccountUser != null) {
                userAccountUser = em.getReference(userAccountUser.getClass(), userAccountUser.getUser());
                help.setUserAccountUser(userAccountUser);
            }
            em.persist(help);
            if (userAccountUser != null) {
                userAccountUser.getHelpList().add(help);
                userAccountUser = em.merge(userAccountUser);
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
            UserAccount userAccountUserOld = persistentHelp.getUserAccountUser();
            UserAccount userAccountUserNew = help.getUserAccountUser();
            if (userAccountUserNew != null) {
                userAccountUserNew = em.getReference(userAccountUserNew.getClass(), userAccountUserNew.getUser());
                help.setUserAccountUser(userAccountUserNew);
            }
            help = em.merge(help);
            if (userAccountUserOld != null && !userAccountUserOld.equals(userAccountUserNew)) {
                userAccountUserOld.getHelpList().remove(help);
                userAccountUserOld = em.merge(userAccountUserOld);
            }
            if (userAccountUserNew != null && !userAccountUserNew.equals(userAccountUserOld)) {
                userAccountUserNew.getHelpList().add(help);
                userAccountUserNew = em.merge(userAccountUserNew);
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
            UserAccount userAccountUser = help.getUserAccountUser();
            if (userAccountUser != null) {
                userAccountUser.getHelpList().remove(help);
                userAccountUser = em.merge(userAccountUser);
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
