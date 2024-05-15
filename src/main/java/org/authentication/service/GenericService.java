package org.authentication.service;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.authentication.common.CommonUtils;
import org.authentication.model.BaseEntity;
import org.authentication.repository.JPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class GenericService<Entity> {
    @Autowired
    private JPA<Entity, Long> genericJPA;

    @Autowired
    private EntityManager entityManager;

    @Value("${PageRequest.page}")
    private Integer page;
    @Value("${PageRequest.size}")
    private Integer size;

    @Transactional
    public void insert(Entity entity, Long userId) throws Exception {
        Method m = entity.getClass().getMethod("setId", Long.class);
        m.invoke(entity, (Long) null);
        CommonUtils.setNull(entity);
        ((BaseEntity) entity).setInsertedUserId(userId);
        ((BaseEntity) entity).setInsertedDateTime(new Date());
        genericJPA.save(entity);
    }

    @Transactional
    public void update(Entity entity, Long userId, Class<Entity> aClass) throws Exception {
        Method m = entity.getClass().getMethod("getId");
        Long id = (Long) m.invoke(entity);
        if (CommonUtils.isNull(id))
            throw new RuntimeException("1006");
        if (CommonUtils.isNull(findOne(aClass, id)))
            throw new RuntimeException("1006");
        CommonUtils.setNull(entity);
        ((BaseEntity) entity).setUpdatedUserId(userId);
        ((BaseEntity) entity).setUpdatedDateTime(new Date());
        genericJPA.update(entity);
    }

    @Transactional
    public void delete(Entity entity) {
        genericJPA.remove(entity);
    }

    @Transactional
    public int delete(Long id, Class<Entity> aClass) {
        jakarta.persistence.Entity entity = aClass.getAnnotation(jakarta.persistence.Entity.class);
        int returnValue = entityManager.createQuery("delete  " + entity.name() + " o where o.id=:id").setParameter("id", id).executeUpdate();
        if (returnValue == 0) {
            throw new RuntimeException("1006");
        }
        return returnValue;
    }

    public Entity findOne(Class<Entity> aClass, Long id) {
        return genericJPA.findOne(aClass, id);
    }

    public List<Entity> findAll(Class<Entity> aClass) {
        return genericJPA.findAll(aClass);
    }

    public Page<Entity> findAll(Class<Entity> aClass, Integer page, Integer size) {
        if (CommonUtils.isNull(page) && CommonUtils.isNull(size)) {
            return genericJPA.findAllWithPaging(aClass);
        }
        PageRequest pageRequest = PageRequest.of(CommonUtils.isNull(page, this.page), CommonUtils.isNull(size, this.size));
        return genericJPA.findAllWithPaging(aClass, pageRequest);

    }

}
