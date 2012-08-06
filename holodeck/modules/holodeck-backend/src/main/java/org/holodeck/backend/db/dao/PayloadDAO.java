/*
 * 
 */
package org.holodeck.backend.db.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.holodeck.backend.db.model.Payload;
import org.springframework.stereotype.Service;

/**
 * The Class PayloadDAO.
 */
@Service
public class PayloadDAO implements IPayloadDAO {
	
	/** The log. */
	private Logger log = Logger.getLogger(MessageDAO.class);

	//property constants
	/** The Constant FILE_NAME. */
	public static final String FILE_NAME = "fileName";

	/** The entity manager. */
	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * Gets the entity manager.
	 *
	 * @return the entity manager
	 */
	private EntityManager getEntityManager() {
		return entityManager;
	}

	/* (non-Javadoc)
	 * @see org.holodeck.backend.db.dao.IPayloadDAO#save(org.holodeck.backend.db.model.Payload)
	 */
	public void save(Payload entity) {
		log.debug("saving Payload instance");
		try {
			getEntityManager().persist(entity);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see org.holodeck.backend.db.dao.IPayloadDAO#delete(org.holodeck.backend.db.model.Payload)
	 */
	public void delete(Payload entity) {
		log.debug("deleting Payload instance");
		try {
			entity = getEntityManager().getReference(Payload.class, entity.getIdPayload());
			getEntityManager().remove(entity);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see org.holodeck.backend.db.dao.IPayloadDAO#update(org.holodeck.backend.db.model.Payload)
	 */
	public Payload update(Payload entity) {
		log.debug("updating Payload instance");
		try {
			Payload result = getEntityManager().merge(entity);
			log.debug("update successful");
			return result;
		} catch (RuntimeException re) {
			log.error("update failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see org.holodeck.backend.db.dao.IPayloadDAO#findById(java.lang.Integer)
	 */
	public Payload findById(Integer id) {
		log.debug("finding Payload instance with id: " + id);
		try {
			Payload instance = getEntityManager().find(Payload.class, id);
			return instance;
		} catch (RuntimeException re) {
			log.error("find failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see org.holodeck.backend.db.dao.IPayloadDAO#findByProperty(java.lang.String, java.lang.Object, int[])
	 */
	@SuppressWarnings("unchecked")
	public List<Payload> findByProperty(String propertyName, final Object value, final int... rowStartIdxAndCount) {
		log.debug("finding Payload instance with property: " + propertyName + ", value: " + value);
		try {
			final String queryString = "select model from Payload model where model." + propertyName
					+ "= :propertyValue";
			Query query = getEntityManager().createQuery(queryString);
			query.setParameter("propertyValue", value);
			if (rowStartIdxAndCount != null && rowStartIdxAndCount.length > 0) {
				int rowStartIdx = Math.max(0, rowStartIdxAndCount[0]);
				if (rowStartIdx > 0) {
					query.setFirstResult(rowStartIdx);
				}
				if (rowStartIdxAndCount.length > 1) {
					int rowCount = Math.max(0, rowStartIdxAndCount[1]);
					if (rowCount > 0) {
						query.setMaxResults(rowCount);
					}
				}
			}
			return query.getResultList();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see org.holodeck.backend.db.dao.IPayloadDAO#findByFileName(java.lang.Object, int[])
	 */
	public List<Payload> findByFileName(Object fileName, int... rowStartIdxAndCount) {
		return findByProperty(FILE_NAME, fileName, rowStartIdxAndCount);
	}

	/* (non-Javadoc)
	 * @see org.holodeck.backend.db.dao.IPayloadDAO#findAll(int[])
	 */
	@SuppressWarnings("unchecked")
	public List<Payload> findAll(final int... rowStartIdxAndCount) {
		log.debug("finding all Payload instances");
		try {
			final String queryString = "select model from Payload model";
			Query query = getEntityManager().createQuery(queryString);
			if (rowStartIdxAndCount != null && rowStartIdxAndCount.length > 0) {
				int rowStartIdx = Math.max(0, rowStartIdxAndCount[0]);
				if (rowStartIdx > 0) {
					query.setFirstResult(rowStartIdx);
				}
				if (rowStartIdxAndCount.length > 1) {
					int rowCount = Math.max(0, rowStartIdxAndCount[1]);
					if (rowCount > 0) {
						query.setMaxResults(rowCount);
					}
				}
			}
			return query.getResultList();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
}