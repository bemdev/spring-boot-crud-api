package com.mediaforge.crud.controllers;

import com.mediaforge.crud.services.GenericCrudService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@Component
public class GenericCrudController<T, ID> {

  private final String requestMapping = "/api";
  private final GenericCrudService<T, ID> genericCrudService;
  private final @Qualifier("Path") String path;

  @Autowired
  public GenericCrudController(
      GenericCrudService<T, ID> genericCrudService, @Qualifier("Path") String path) {
    this.path = path;
    this.genericCrudService = genericCrudService;
  }

  @PostMapping(requestMapping + "/{entity}/create")
  @ResponseBody
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<T> create(@RequestBody T body) {
    return ResponseEntity.ok(genericCrudService.create(path, body));
  }

  @GetMapping(requestMapping + "/{entity}")
  @ResponseBody
  public ResponseEntity<List<T>> read() {
    List<T> data = genericCrudService.read(path);
    if (data == null) {
      return ResponseEntity.notFound().build();
    } else {
      return ResponseEntity.ok(genericCrudService.read(path));
    }
  }

  @GetMapping(requestMapping + "/{entity}/{id}")
  @ResponseBody
  public Optional<T> readById(@PathVariable ID id) {
    return genericCrudService.readById(path, id);
  }

  @PutMapping(requestMapping + "/{entity}/update")
  @ResponseBody
  public T update(@RequestBody T body) {
    return genericCrudService.update(path, body);
  }

  @PatchMapping(requestMapping + "/{entity}/patch")
  @ResponseBody
  public T patch(@RequestBody T body) {
    return genericCrudService.patch(path, body);
  }

  @DeleteMapping(requestMapping + "/{entity}/delete/{id}")
  @ResponseBody
  public void delete(@PathVariable ID id) {
    genericCrudService.deleteById(path, id);
  }

  @DeleteMapping(requestMapping + "/{entity}/delete")
  @ResponseBody
  public void deleteAll() {
    genericCrudService.deleteByAll(path);
  }
}
