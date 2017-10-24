package io.github.jistol.geosns.jpa.entry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "posting")
public class Posting {
    @Id
    @GeneratedValue
    private long id;
    /*@NotNull
    @Column(insertable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private User user;*/
    @Lob
    private String message;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Collection<Attach> attaches;
}
