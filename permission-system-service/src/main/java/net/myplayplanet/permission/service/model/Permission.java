package net.myplayplanet.permission.service.model;


import com.google.common.base.MoreObjects;
import lombok.*;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "permissions")
public class Permission {

    @Id
    @Column(name = "uuid", nullable = false)
    private UUID uuid;

    @ManyToOne
    private Scope scope;

    @Column
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private Permission parent;

    @OneToMany(mappedBy = "parent")
    private Set<Permission> children = new HashSet<>();

    public Permission(UUID uuid) {
        this.uuid = uuid;
    }

    // FIXME: 16.10.23 to prevent recursion overflow, equals and hashcode only rely on uuid, maybe there are some drawbacks?

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Permission that)) return false;
        return Objects.equals(getUuid(), that.getUuid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUuid());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("uuid", uuid)
                .add("name", name)
                .add("parent", parent)
                .add("children", children)
                .toString();
    }
}
