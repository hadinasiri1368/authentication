package org.authentication.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "[user_permission]", schema = "authn")
@Entity(name = "userPermission")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserPermission extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "f_user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "f_permission_id")
    private Permission permission;
}
