package de.avalax.filmdb.domain.model;


import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Builder
public class Film {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String name;

    private String genre;

    private Integer rating;

    private Integer year;

    @Tolerate
    public Film() {
        // This constructor is used by hibernate
    }

    public FilmId getId() {
        return FilmId.builder().id(id).build();
    }
}
