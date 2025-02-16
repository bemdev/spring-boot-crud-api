package com.mediaforge.crud.config;

import com.mediaforge.crud.controllers.GenericControllerFactory;
import com.mediaforge.crud.controllers.GenericCrudController;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

@Configuration
public class GenericControllerConfig<T, ID> {

  private final String prefixControllerName = "dynamicController_";
  private final ConfigurableApplicationContext context;
  private final GenericControllerFactory<T, ID> factory;
  private final SwaggerConfig swaggerConfig;

  @Autowired
  public GenericControllerConfig(
      ConfigurableApplicationContext context,
      GenericControllerFactory<T, ID> factory,
      SwaggerConfig swaggerConfig) {
    this.context = context;
    this.factory = factory;
    this.swaggerConfig = swaggerConfig;
  }

  @Bean
  public String Path() {
    return "";
  }

  public void registerController(String path, String[]... excludeMethods) {
    GenericCrudController<T, ID> controller = factory.createController(path);
    registerBean(controller, path, excludeMethods);
  }

  private void registerBean(
      GenericCrudController<T, ID> controller, String path, String[]... excludeMethods) {
    BeanDefinitionRegistry registry = (BeanDefinitionRegistry) context.getAutowireCapableBeanFactory();
    String beanName = prefixControllerName + path.replaceAll("/", "_");

    BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(GenericCrudController.class);
    builder.addConstructorArgValue(controller);
    registry.registerBeanDefinition(beanName, builder.getBeanDefinition());

    registerMappingsForController(controller, beanName, path, excludeMethods);
  }

  private void registerMappingsForController(
      Object controller, String beanName, String entityName, String[]... excludeMethods) {
    RequestMappingHandlerMapping handlerMapping = context.getBean(RequestMappingHandlerMapping.class);

    Map<Method, Map<RequestMethod, String[]>> mappingsMap = new HashMap<>();

    Method[] methods = controller.getClass().getDeclaredMethods();
    for (Method method : methods) {

      if (excludeMethods != null
          && excludeMethods.length > 0
          && excludeMethods[0].length > 0
          && Arrays.stream(excludeMethods[0])
              .anyMatch(exmethod -> exmethod.equals(method.getName()))) {
        continue;
      }

      if (method.isAnnotationPresent(GetMapping.class)) {
        GetMapping getMapping = method.getAnnotation(GetMapping.class);
        mappingsMap.put(method, Map.of(RequestMethod.GET, getMapping.value()));
      }

      if (method.isAnnotationPresent(PostMapping.class)) {
        PostMapping postMapping = method.getAnnotation(PostMapping.class);
        mappingsMap.put(method, Map.of(RequestMethod.POST, postMapping.value()));
      }

      if (method.isAnnotationPresent(PutMapping.class)) {
        PutMapping putMapping = method.getAnnotation(PutMapping.class);
        mappingsMap.put(method, Map.of(RequestMethod.PUT, putMapping.value()));
      }

      if (method.isAnnotationPresent(PatchMapping.class)) {
        PatchMapping patchMapping = method.getAnnotation(PatchMapping.class);
        mappingsMap.put(method, Map.of(RequestMethod.PATCH, patchMapping.value()));
      }

      if (method.isAnnotationPresent(DeleteMapping.class)) {
        DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
        mappingsMap.put(method, Map.of(RequestMethod.DELETE, deleteMapping.value()));
      }
    }
    for (Map.Entry<Method, Map<RequestMethod, String[]>> mapping : mappingsMap.entrySet()) {
      for (Map.Entry<RequestMethod, String[]> pathsWithHttpMethods : mapping.getValue().entrySet()) {
        for (String path : pathsWithHttpMethods.getValue()) {
          PathPattern pathPattern = PathPatternParser.defaultInstance.parse(path);
          RequestMappingInfo mappingInfo = RequestMappingInfo
              .paths(pathPattern.toString().replace("{entity}", entityName))
              .methods(pathsWithHttpMethods.getKey())
              .build();
          handlerMapping.registerMapping(mappingInfo, controller, mapping.getKey());
          swaggerConfig.addPath(
              path.replace("{entity}", entityName),
              swaggerConfig.generatePathItem(
                  mapping.getKey(), path, entityName, pathsWithHttpMethods.getKey()));
        }
      }
    }
  }
}
