package io.github.jistol.geosns.jpa.entry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.MediaType;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "attach")
public class Attach {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    private String ext;
    private MediaType type;
    private String path;
    private Long size;
}
