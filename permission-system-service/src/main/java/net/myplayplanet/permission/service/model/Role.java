package net.myplayplanet.permission.service.model;


import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "roles")
public class Role {

    @Id
    private Long id;

    @ManyToOne
    private Scope scope;

    @Column
    private String name;

    @Column
    private Integer weight;

    @OneToMany
    private Set<Permission> granted;

    @OneToMany
    private Set<Permission> denied;

    @Override
    public String toString() {

        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Role [").append(id).append("]")
                .append("\n")
                .append("weight: ").append(weight)
                .append("\n").append("GRANTED").append("\n");

        for (Permission permission : granted) {
            stringBuilder.append(permission.toString()).append("\n");
        }

        stringBuilder.append("DENIED").append("\n");

        for (Permission permission : denied) {
            stringBuilder.append(permission.toString()).append("\n");
        }

        return stringBuilder.toString();
    }
}
