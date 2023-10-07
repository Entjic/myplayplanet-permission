package net.myplayplanet.permission.service.model;


import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
public class Role {

    @Id
    private Long id;

    private Integer weight;

    @OneToMany(mappedBy = "role")
    private Set<Permission> granted;

    @OneToMany(mappedBy = "role")
    private Set<Permission> denied;

}
