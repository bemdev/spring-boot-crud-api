![Release](https://jitpack.io/v/bemdev/spring-boot-crud-api.svg)
(https://jitpack.io/v/bemdev/spring-boot-crud-api)

# Fast CRUD App on Java Spring Boot

Easy way to create backend service with CRUD routes (aka fastapi)

1. Create entities (User, Posts, Products, etc)
2. Create repositories by User, Posts, Products, etc
3. Registration you new CRUD controller on prepare to start App

Collect benifits! You make simple backend with auto documentation (Swagger) at 5 second!

---

## Project dependencies

Parent:

```yml
<artifactId>spring-boot-starter-parent</artifactId>
<version>3.4.1</version>
```

Dependencies:

```yml
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-starter-data-jdbc</artifactId>

<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-starter-data-jpa</artifactId>

<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-starter-web</artifactId>

<groupId>org.postgresql</groupId>
<artifactId>postgresql</artifactId>
<scope>runtime</scope>

<groupId>org.projectlombok</groupId>
<artifactId>lombok</artifactId>
<optional>true</optional>

<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-starter-test</artifactId>
<scope>test</scope>
```

---

## How to install

We use JitPack:

```
<!-- pom or settings.xml -->
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<!-- using library -->
<dependency>
    <groupId>com.github.bemdev</groupId>
    <artifactId>spring-boot-crud-api</artifactId>
    <version>v0.0.1-beta.1</version>
</dependency>
```

## How to use

After generate Spring Boot App at SpringTime or other ways, we can add app name and app version (spring.application.name, spring.application.version) in application.properties.

Finally we can go write api app:

1. For example use default entitie User from library. Add scan entities path.
   (u can use self-made entitie aka spring-boot entitie class, but important use @Component decorator)

```java
@EntityScan(basePackages = "com.mediaforge.crud.entities")
```

2. For example use default repository User from library. Add scan repositories path.
   (u can use self-made repository aka spring-boot repository class)

```java
@EnableJpaRepositories(basePackages = "com.mediaforge.crud.repositories")
```

3. Registration new CRUD controller with exclude methods

```java
public class DemoApplication<T> {

	private final GenericControllerConfig<T, ?> registration;

	 public DemoApplication(GenericControllerConfig<T, ?> registration) {
	 	this.registration = registration;
	 }

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	 @Bean
	 public CommandLineRunner CommandLineRunnerBean() {
		 registration.registerController("users", new String[] { "deleteAll", "patch" });
		 return (args) -> {
		 	System.out.println("Controllers generated. App Started.");
		 };
	 }
}
```

---

This is prototype library and very simple code release!
If u can help me - do it :D
