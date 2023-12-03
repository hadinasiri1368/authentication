package org.authentication.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "[role_permission]", schema = "authn")
@Entity(name = "rolePermission")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RolePermission extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "f_role_id")
    private Role role;
    @ManyToOne
    @JoinColumn(name = "f_permission_id")
    private Permission permission;
}
