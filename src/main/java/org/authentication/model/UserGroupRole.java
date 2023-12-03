package org.authentication.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "[user_group_role]", schema = "authn")
@Entity(name = "userGroupRole")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserGroupRole extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "f_user_group_id")
    private UserGroup userGroup;
    @ManyToOne
    @JoinColumn(name = "f_role_id")
    private Role role;
}
