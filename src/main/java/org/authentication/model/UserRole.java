package org.authentication.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "[user_role]", schema = "authn")
@Entity(name = "userRole")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRole extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "f_user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "f_role_id")
    private Role role;
}
