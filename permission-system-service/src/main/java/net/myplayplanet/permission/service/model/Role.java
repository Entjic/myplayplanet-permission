package net.myplayplanet.permission.service.model;


import com.google.common.base.MoreObjects;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permissions_id"))
    private Set<Permission> granted = new LinkedHashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "role_denied",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "denied_id"))
    private Set<Permission> denied = new LinkedHashSet<>();

    @Column(nullable = false)
    private Boolean editable = false; // If not editable, name is a translation key, otherwise its user input freeform

    @Column
    private String description;

    public Role(Scope scope, String name, Integer weight, Set<Permission> granted, Set<Permission> denied) {
        this(null, scope, name, weight, granted, denied, false, "");
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("scope", scope)
                .add("name", name)
                .add("weight", weight)
                .add("granted", granted)
                .add("denied", denied)
                .add("editable", editable)
                .add("description", description)
                .toString();
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
