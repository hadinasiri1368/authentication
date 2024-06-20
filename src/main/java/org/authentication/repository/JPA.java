package org.authentication.repository;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import org.authentication.common.CommonUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class JPA<ENTITY, ID> {
    @PersistenceContext()
    private EntityManager entityManager;

    @Transactional
    public void save(ENTITY entity) throws Exception {
        CommonUtils.setNull(entity);
        entityManager.persist(entity);
    }

    @Transactional
    public void update(ENTITY entity) throws Exception {
        CommonUtils.setNull(entity);
        entityManager.merge(entity);
    }

    @Transactional
    public void remove(ENTITY entity) {
        entityManager.remove(entityManager.merge(entity));
    }

    public ENTITY findOne(Class<ENTITY> aClass, ID id) {
        return entityManager.find(aClass, id);
    }

    public List listByQuery(Query query, Map<String, Object> param) {
        if (!CommonUtils.isNull(param) && param.size() > 0)
            for (String key : param.keySet()) {
                query.setParameter(key, param.get(key));
            }
        return query.getResultList();
    }

    public int executeUpdate(Query query, Map<String, Object> param) {
        if (!CommonUtils.isNull(param) && param.size() > 0)
            for (String key : param.keySet()) {
                query.setParameter(key, param.get(key));
            }
        return query.executeUpdate();
    }

    public List listByQuery(Query query) {
        return listByQuery(query, null);
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

    public Page<ENTITY> findAllWithPaging(Class<ENTITY> aClass) {
        Entity entity = aClass.getAnnotation(Entity.class);
        Query query = entityManager.createQuery("select entity from " + entity.name() + " entity");
        List<ENTITY> fooList = query.getResultList();
        PageRequest pageRequest = PageRequest.of(0, fooList.isEmpty() ? 1 : fooList.size());
        return new PageImpl<ENTITY>(fooList, pageRequest, fooList.isEmpty() ? 1 :fooList.size());
    }

    public Page<ENTITY> findAllWithPaging(Class<ENTITY> aClass, PageRequest pageRequest) {
        Entity entity = aClass.getAnnotation(Entity.class);
        Query query = entityManager.createQuery("select entity from " + entity.name() + " entity");
        int pageNumber = pageRequest.getPageNumber();
        int pageSize = pageRequest.getPageSize();
        query.setFirstResult((pageNumber) * pageSize);
        query.setMaxResults(pageSize);
        List<ENTITY> fooList = query.getResultList();
        Query queryTotal = entityManager.createQuery("select count(entity.id) from " + entity.name() + " entity");
        Long countResult = (Long) queryTotal.getSingleResult();
        return new PageImpl<ENTITY>(fooList, pageRequest, countResult);
    }
}