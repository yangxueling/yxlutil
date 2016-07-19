package com.yxlisv.util.hibernate;

import java.io.Serializable;
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;

import com.yxlisv.util.reflect.AnnotationUtil;

/**
 * <p>Hibernate UUID生成器</p>
 * <p>如果实体类中存在ID，则不生成新的ID</p>
 * @author 杨雪令
 * @time 2016年7月12日下午1:25:22
 * @version 1.0
 */
public class UUIDGenerator implements IdentifierGenerator {

    @Override
    public Serializable generate(SessionImplementor session, Object object) throws HibernateException {
    	Object id = AnnotationUtil.getMethodValue(object, javax.persistence.Id.class);
    	if(id != null) return (Serializable) id;
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}