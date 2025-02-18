package net.myplayplanet.permission.service.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.type.SqlTypes;

import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "permission_user", uniqueConstraints = {
        @UniqueConstraint(name = "uc_user_uuid_scope_id", columnNames = {"uuid", "scope_id"})
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "uuid", nullable = false, columnDefinition = "UUID")
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID uuid;

    @ManyToOne
    private Scope scope;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "permission_user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "roles_id"))
    private Set<Role> roles = new LinkedHashSet<>();


    @OneToMany(fetch = FetchType.EAGER)
    private Set<Permission> granted = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER)
    private Set<Permission> denied = new HashSet<>();


    public User(UUID uuid, Scope scope) {
        this.uuid = uuid;
        this.scope = scope;
    }

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

    @Override
    public final boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        final User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
