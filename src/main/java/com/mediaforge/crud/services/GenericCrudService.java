package com.mediaforge.crud.services;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediaforge.crud.repositories.GenericCrudRepository;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class GenericCrudService<T, ID> {

  private final GenericCrudRepository<T, ID> repositoryFactory;

  @Autowired
  public GenericCrudService(GenericCrudRepository<T, ID> repositoryFactory) {
    this.repositoryFactory = repositoryFactory;
  }

  @SuppressWarnings("unchecked")
  private T linkedMapToEntityClass(T body, String entity) {
    Class<?> entityClass = repositoryFactory.getBodyFromEntity(entity);
    LinkedHashMap linkedBody = (LinkedHashMap) body;
    String relationColumnName = "owner_id";

    try {
      Field ownerField = entityClass.getDeclaredField(relationColumnName);
      JsonIdentityReference identityRef = ownerField.getAnnotation(JsonIdentityReference.class);
      if (identityRef != null && identityRef.alwaysAsId()) {
        Map<String, Integer> ownerObject = new LinkedHashMap<>();
        ownerObject.put("id", Integer.parseInt(linkedBody.get(relationColumnName).toString()));
        linkedBody.put(relationColumnName, ownerObject);
      }
    } catch (Exception e) {
      System.err.println(e);
    }

    ObjectMapper mapper = new ObjectMapper();
    Object data = mapper.convertValue(linkedBody, entityClass);
    return (T) data;
  }

  public Optional<T> readById(String entity, ID id) {
    JpaRepository<T, ID> repository = repositoryFactory.getRepository(entity);
    return repository.findById(id);
  }

  public List<T> read(String entity) {
    JpaRepository<T, ?> repository = repositoryFactory.getRepository(entity);
    return repository.findAll();
  }

  public T create(String entity, T body) {
    JpaRepository<T, ?> repository = repositoryFactory.getRepository(entity);
    return repository.save(linkedMapToEntityClass(body, entity));
  }

  public T update(String entity, T body) {
    JpaRepository<T, ?> repository = repositoryFactory.getRepository(entity);
    return repository.save(linkedMapToEntityClass(body, entity));
  }

  public T patch(String entity, T body) {
    JpaRepository<T, ?> repository = repositoryFactory.getRepository(entity);
    return repository.save(linkedMapToEntityClass(body, entity));
  }

  public void deleteById(String entity, ID id) {
    JpaRepository<T, ID> repository = repositoryFactory.getRepository(entity);
    repository.deleteById(id);
  }

  public void deleteByAll(String entity) {
    JpaRepository<T, ID> repository = repositoryFactory.getRepository(entity);
    repository.deleteAll();
  }

  public boolean existsById(String entity, ID id) {
    JpaRepository<T, ID> repository = repositoryFactory.getRepository(entity);
    return repository.existsById(id);
  }

  public long count(String entity) {
    JpaRepository<T, ?> repository = repositoryFactory.getRepository(entity);
    return repository.count();
  }
}
