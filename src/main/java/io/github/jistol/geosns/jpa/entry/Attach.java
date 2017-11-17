package io.github.jistol.geosns.jpa.entry;

import io.github.jistol.geosns.model.Meta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.MediaType;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "attach")
public class Attach implements Serializable {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    private String ext;
    private MediaType type;
    private String path;
    private Long size;
    private Long deltaX;
    private Long deltaY;

    public Attach setup(Meta meta) {
        this.setDeltaX(meta.getDeltaX());
        this.setDeltaY(meta.getDeltaY());
        return this;
    }
}
