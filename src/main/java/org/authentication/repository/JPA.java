package org.authentication.repository;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import org.authentication.common.CommonUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class JPA<ENTITY, ID> {
    @PersistenceContext()
    private EntityManager entityManager;

    @Transactional
    public void save(ENTITY entity) {
        entityManager.persist(entity);
    }

    @Transactional
    public void update(ENTITY entity) {
        entityManager.merge(entity);
    }

    @Transactional
    public void remove(ENTITY entity) {
        entityManager.remove(entityManager.merge(entity));
    }

    public ENTITY findOne(Class<ENTITY> aClass, ID id) {
        return entityManager.find(aClass, id);
    }

    public List listByQuery(Query query) {
        return query.getResultList();
    }

    public List<ENTITY> findAll(Class<ENTITY> aClass) {
        Entity entity = aClass.getAnnotation(Entity.class);
        Query query = entityManager.createQuery("select entity from " + entity.name() + " entity");
        return query.getResultList();
    }

    public List<ENTITY> findAll(Class<ENTITY> aClass, Map<String, Object> param) {
        Entity entity = aClass.getAnnotation(Entity.class);
        String strQuery = CommonUtils.getStringQuery("select entity from " + entity.name() + " entity", param);
        Query query = entityManager.createQuery(strQuery);
        for (String key : param.keySet()) {
            query.setParameter(key, param.get(key));
        }
        return query.getResultList();
    }
}