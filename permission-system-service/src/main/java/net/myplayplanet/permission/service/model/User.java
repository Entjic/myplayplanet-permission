package net.myplayplanet.permission.service.model;


import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
public class User {

    @Id
    private UUID uuid;

    @OneToMany(mappedBy = "user")
    private Set<Role> role;

    @OneToMany(mappedBy = "user")
    private Set<Permission> granted;

    @OneToMany(mappedBy = "user")
    private Set<Permission> denied;

}
