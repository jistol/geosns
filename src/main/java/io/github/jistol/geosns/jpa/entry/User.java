package io.github.jistol.geosns.jpa.entry;

import io.github.jistol.geosns.type.LoginType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class User implements Serializable {
    @Id
    @GeneratedValue
    private long id;
    @NotNull
    private String siteId;
    @NotNull
    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    // optional
    private String nickname;
    private String email;
    private String profileImage;
    private String thumbnailImage;
}
