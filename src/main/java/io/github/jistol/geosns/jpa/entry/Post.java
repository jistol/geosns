package io.github.jistol.geosns.jpa.entry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue
    private long id;
    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;
    @Lob
    @NotEmpty
    @Column(nullable = false)
    private String message;
    @Column(nullable = false)
    private Double lat;
    @Column(nullable = false)
    private Double lng;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn
    private Collection<Attach> attaches;
}
