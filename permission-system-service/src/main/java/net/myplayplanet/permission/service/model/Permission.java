package net.myplayplanet.permission.service.model;


import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "permissions")
public class Permission {

    @Id
    @Column(name = "id")
    @Type(type = "uuid-char")
    private UUID uuid = UUID.randomUUID();

    private String name;

    private String description;

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
}
