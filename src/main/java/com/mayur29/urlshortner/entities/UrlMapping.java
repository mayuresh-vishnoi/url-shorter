package com.mayur29.urlshortner.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "url_mapping")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UrlMapping {

    @Id
    private Long id;
    private String longUrl;
    private String code;
}
