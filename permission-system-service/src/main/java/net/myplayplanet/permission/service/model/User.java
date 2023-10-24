package net.myplayplanet.permission.service.model;


import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "id")
    @Type(type = "uuid-char")
    private UUID uuid = UUID.randomUUID();

    @OneToMany
    private Set<Role> roles;

    @OneToMany
    private Set<Permission> granted;

    @OneToMany
    private Set<Permission> denied;

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("User [").append(uuid).append("]")
                .append("\n")
                .append("ROLES")
                .append("\n");

        for (Role r : roles) {
            stringBuilder.append(r.toString()).append("\n");
        }

        stringBuilder.append("USER SPECIFIC").append("\n");

        stringBuilder.append("GRANTED").append("\n");

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
