# Fast CRUD App on Java Spring Boot

Easy way to create backend service with CRUD routes (aka fastapi)

1. Create entities (User, Posts, Products, etc)
2. Create repositories by User, Posts, Products, etc
3. Registration you new CRUD controller on prepare to start App

Collect benifits! You make simple backend with auto documentation (Swagger) at 5 second!

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
    <version>master-SNAPSHOT</version>
</dependency>
```

## How to use

1. Create entitie

```java
package com.mediaforge.crud.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Entity
@Component
@NoArgsConstructor
@Table(name = "users")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Users {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonProperty("id")
  private Long id;

  @Column(nullable = false, unique = true)
  @JsonProperty("username")
  private String username;

  @Column(columnDefinition = "TEXT")
  @JsonProperty("firstname")
  private String firstname;

  @Column(nullable = false)
  @JsonProperty("is_active")
  private Boolean isActive;

  @OneToMany(mappedBy = "owner_id", fetch = FetchType.EAGER, orphanRemoval = true)
  @JsonProperty("posts")
  private List<Posts> posts = new ArrayList<>();
}
```

2. Create repository

```java
package com.mediaforge.crud.repositories;

import com.mediaforge.crud.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Long> {
}
```

3. Registration new CRUD controller with exclude methods

```java
@Bean
	public CommandLineRunner CommandLineRunnerBean() {
		registration.registerController("users", new String[] { "deleteAll", "patch" });
		return (args) -> {
			System.out.println("Controllers generated. App Started.");
		};
	}
```

---

This is prototype library and very simple code release!
If u can help me - do it :D
