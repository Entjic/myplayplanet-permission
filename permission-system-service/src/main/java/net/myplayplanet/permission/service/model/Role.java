package net.myplayplanet.permission.service.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "role", uniqueConstraints = {
        @UniqueConstraint(name = "uc_role_name_scope_id", columnNames = {"name", "scope_id"})
})
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    private Scope scope;

    @Column
    private String name;

    @Column
    private Integer weight; // Higher weight corresponds to overriding lower value permissions

    @OneToMany(fetch = FetchType.EAGER)
    private Set<Permission> granted;

    @OneToMany(fetch = FetchType.EAGER)
    private Set<Permission> denied;

    @Column(nullable = false)
    private Boolean editable = false; // If not editable, name is a translation key, otherwise its user input freeform

    @Column
    private String description;

    public Role(Scope scope, String name, Integer weight, Set<Permission> granted, Set<Permission> denied) {
        this(null, scope, name, weight, granted, denied, false, "");
    }

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

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Role role = (Role) o;
        return Objects.equals(getId(), role.getId()) && Objects.equals(getScope(), role.getScope()) && Objects.equals(getName(), role.getName()) && Objects.equals(getWeight(), role.getWeight()) && Objects.equals(getGranted(), role.getGranted()) && Objects.equals(getDenied(), role.getDenied()) && Objects.equals(getEditable(), role.getEditable());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getScope(), getName(), getWeight(), getGranted(), getDenied(), getEditable());
    }
}
