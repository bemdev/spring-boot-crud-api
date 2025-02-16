package com.mediaforge.crud.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.metamodel.EntityType;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public class GenericCrudRepository<T, ID> {

  private final ApplicationContext applicationContext;

  @PersistenceContext
  private EntityManager entityManager;

  @Autowired
  public GenericCrudRepository(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  public Class<?> getBodyFromEntity(String entityName) {
    Set<EntityType<?>> entities = entityManager.getMetamodel().getEntities();

    for (EntityType<?> entity : entities) {
      if (entity.toString().compareToIgnoreCase(entityName) == 0) {
        return applicationContext.getBean(entityName).getClass();
      }
    }

    return null;
  }

  @SuppressWarnings("unchecked")
  public JpaRepository<T, ID> getRepository(String entityName) {
    Set<EntityType<?>> entities = entityManager.getMetamodel().getEntities();

    for (EntityType<?> entity : entities) {
      if (entity.toString().equalsIgnoreCase(entityName)) {
        return (JpaRepository<T, ID>) applicationContext.getBean(entityName + "Repository");
      }
    }

    return null;
  }
}
