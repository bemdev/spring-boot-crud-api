package com.mediaforge.crud.entities;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Entity
@Component
@NoArgsConstructor
@Table(name = "posts")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Posts {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonProperty("id")
  private Long id;

  @Column(nullable = false)
  @JsonProperty("author")
  private String author;

  @Column(columnDefinition = "TEXT")
  @JsonProperty("title")
  private String title;

  @Column(columnDefinition = "TEXT")
  @JsonProperty("description")
  private String description;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "owner_id", referencedColumnName = "id")
  @JsonIdentityReference(alwaysAsId = true)
  @JsonProperty("owner_id")
  private Users owner_id;
}
