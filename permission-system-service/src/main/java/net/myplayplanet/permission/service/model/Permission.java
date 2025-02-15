package net.myplayplanet.permission.service.model;


import com.google.common.base.MoreObjects;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "permissions", uniqueConstraints = {
        @UniqueConstraint(name = "uc_permission_key", columnNames = {"permission_key"})
})
public class Permission {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "permission_key", nullable = false)
    private String key; // base for generating related translation keys ie for name, description etc, also used for grouping!

    @ManyToOne(fetch = FetchType.LAZY)
    private Permission parent;

    @OneToMany(mappedBy = "parent")
    private Set<Permission> children = new HashSet<>();


    public Permission(String key) {
        this.key = key;
    }

    // FIXME: 16.10.23 to prevent recursion overflow, equals and hashcode only rely on uuid, maybe there are some drawbacks?

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Permission that)) return false;
        return Objects.equals(getKey(), that.getKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("translationKeyBase", key)
                .add("parent", parent)
                .add("children", children)
                .toString();
    }

}
