package io.github.jistol.geosns.jpa.entry;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.jistol.geosns.model.Meta;
import io.github.jistol.geosns.type.Scope;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "post")
public class Post implements Serializable {
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
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Scope scope;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn
    private List<Attach> attaches;
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;

    @Transient
    private Collection<Map<String, Object>> attachInfo;

    @Transient
    private Collection<Long> attachIds;

    @Transient
    private Collection<Meta> metas;

    @Transient
    private boolean owner;

    @Transient
    private String encId; // encrypted id

    public String getSubject() {
        return message.substring(0, message.length() > 24 ? 24 : message.length());
    }
}
