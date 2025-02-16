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
