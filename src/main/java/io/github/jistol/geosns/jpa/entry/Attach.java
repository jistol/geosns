package io.github.jistol.geosns.jpa.entry;

import com.fasterxml.jackson.annotation.JsonProperty;
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
}
