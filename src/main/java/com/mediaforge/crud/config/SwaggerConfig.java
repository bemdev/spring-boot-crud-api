package com.mediaforge.crud.config;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;

@Configuration
public class SwaggerConfig {

  private final String apiTitle;
  private final String apiVersion;

  private Paths paths = new Paths();
  private Components components = new Components();
  private final Map<Class<?>, Schema<?>> generatedSchemas = new HashMap<>();

  private final ApplicationContext applicationContext;

  @Autowired
  public SwaggerConfig(
      ApplicationContext applicationContext,
      @Value("${spring.application.name}") String apiTitle,
      @Value("${spring.application.version}") String apiVersion) {
    this.applicationContext = applicationContext;
    this.apiTitle = apiTitle;
    this.apiVersion = apiVersion;
  }

  public Class<?> getEntityClass(String entityName) {
    return applicationContext.getBean(entityName).getClass();
  }

  @Bean
  public OpenAPI initApi() {
    return new OpenAPI()
        .info(new Info().title(apiTitle).version(apiVersion))
        .paths(paths)
        .components(components);
  }

  public void addPath(String path, PathItem pathItem) {
    paths.addPathItem(path, pathItem);
  }

  public void addComponent(String entityName, Schema<?> schema) {
    // schema.$ref("#/components/schemas/" + entityName);
    // components.addSchemas(entityName, schema);
  }

  private Schema<?> generateSchemaFromEntity(Class<?> entityClass) {
    if (generatedSchemas.containsKey(entityClass)) {
      return generatedSchemas.get(entityClass);
    }
    Schema<?> schema = new Schema<>();
    schema.title(entityClass.getSimpleName());
    schema.setType("object");
    schema.setProperties(new HashMap<>());

    generatedSchemas.put(entityClass, schema);

    for (Field field : entityClass.getDeclaredFields()) {
      if (!Modifier.isStatic(field.getModifiers())) {
        JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
        Schema<?> propertySchema = new Schema<>();

        Class<?> fieldType = field.getType();
        if (fieldType.equals(Long.class) || fieldType.equals(long.class)) {
          propertySchema.setType("integer");
          propertySchema.setFormat("int64");
        } else if (fieldType.equals(Integer.class) || fieldType.equals(int.class)) {
          propertySchema.setType("integer");
          propertySchema.setFormat("int32");
        } else if (fieldType.equals(Boolean.class) || fieldType.equals(boolean.class)) {
          propertySchema.setType("boolean");
        } else if (fieldType.equals(Double.class) || fieldType.equals(double.class)) {
          propertySchema.setType("number");
          propertySchema.setFormat("double");
        } else if (fieldType.equals(Float.class) || fieldType.equals(float.class)) {
          propertySchema.setType("number");
          propertySchema.setFormat("float");
        } else if (fieldType.equals(String.class)) {
          propertySchema.setType("string");
        } else if (Iterable.class.isAssignableFrom(fieldType)) {
          propertySchema.setType("array");
        } else {
          JsonIdentityReference identityRef = field.getAnnotation(JsonIdentityReference.class);
          if (identityRef != null && identityRef.alwaysAsId()) {
            propertySchema.setType("integer");
            propertySchema.setFormat("int64");
          } else {
            propertySchema.setType("object");
            propertySchema.setProperties(generateSchemaFromEntity(fieldType).getProperties());
          }
        }

        schema.getProperties().put(jsonProperty.value(), propertySchema);
      }
    }

    return schema;
  }

  public PathItem generatePathItem(
      Method method, String path, String entityName, RequestMethod requestMethod) {
    PathItem pathItem = new PathItem();
    Operation operation = new Operation();

    Schema<?> schema = generateSchemaFromEntity(getEntityClass(entityName));

    operation.summary(method.getName());
    operation.tags(List.of(entityName));
    operation.responses(createAddApiResponses(schema));

    Parameter[] parameters = Arrays.stream(method.getParameters())
        .map(
            parameter -> {
              if (!parameter.getName().contains("entity")) {
                Parameter param = new Parameter();
                param.name(parameter.getName());
                if (parameter.getName().contains("body")) {
                  Content content = new Content()
                      .addMediaType("application/json", new MediaType().schema(schema));
                  operation.requestBody(new RequestBody().content(content));
                  return null;
                } else {
                  param.in("path");
                  param.schema(new Schema<>().type("string"));
                }
                param.required(true);
                return param;
              }
              return null;
            })
        .filter(Objects::nonNull)
        .toArray(Parameter[]::new);

    operation.parameters(Arrays.asList(parameters));

    return switchMethods(requestMethod, pathItem, operation, entityName);
  }

  public ApiResponses createAddApiResponses(Schema<?> schema) {
    ApiResponses apiResponses = new ApiResponses();

    ApiResponse response200 = new ApiResponse();
    response200.description("Успешная операция - ответ пустой массив или null значение");
    ApiResponse response404 = new ApiResponse();
    response404.description("Такой страницы не существует");
    ApiResponse response400 = new ApiResponse();
    response400.description("Неверные входные данные - проверьте данные на соответствие схемы");

    apiResponses.addApiResponse("200", response200);
    response200.content(
        new Content().addMediaType("application/json", new MediaType().schema(schema)));
    apiResponses.addApiResponse("404", response404);
    apiResponses.addApiResponse("400", response400);

    return apiResponses;
  }

  public PathItem switchMethods(
      RequestMethod requestMethod, PathItem pathItem, Operation operation, String entityName) {

    switch (requestMethod) {
      case GET:
        pathItem.get(operation);
        operation.description("Получение записей из " + entityName);
        break;
      case POST:
        pathItem.post(operation);
        operation.description("Добавление новой записи в " + entityName);
        break;
      case PUT:
        pathItem.put(operation);
        operation.description("Редактирование записи в " + entityName);
        break;
      case PATCH:
        pathItem.patch(operation);
        operation.description("Частичное редактирование записи в " + entityName);
        break;
      case TRACE:
        pathItem.trace(operation);
        break;
      case HEAD:
        pathItem.head(operation);
        break;
      case OPTIONS:
        pathItem.options(operation);
        break;
      case DELETE:
        pathItem.delete(operation);
        operation.description("Удаление записи из " + entityName);
        break;
    }
    return pathItem;
  }
}
