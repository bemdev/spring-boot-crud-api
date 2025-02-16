package com.mediaforge.crud.controllers;

import com.mediaforge.crud.services.GenericCrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GenericControllerFactory<T, ID> {
  private final GenericCrudService<T, ID> genericCrudService;

  @Autowired
  public GenericControllerFactory(GenericCrudService<T, ID> genericCrudService) {
    this.genericCrudService = genericCrudService;
  }

  public GenericCrudController<T, ID> createController(String path) {
    return new GenericCrudController<T, ID>(genericCrudService, path);
  }
}
