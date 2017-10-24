package io.github.jistol.geosns.jpa.entry;

import io.github.jistol.geosns.type.LoginType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class User<T extends Serializable> {
    @Id
    @GeneratedValue
    private long id;
    private String siteId;
    @Enumerated(EnumType.STRING)
    private LoginType loginType;
    @Transient
    private String token;
    @Transient
    private T data;
}
