package org.authentication.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "[user]", schema = "authn")
@Entity(name = "user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "NVARCHAR(50)", updatable = false)
    private String username;
    @Column(columnDefinition = "NVARCHAR(100)", updatable = false)
    @JsonIgnore
    private String password;
    @Column(columnDefinition = "BIT", name = "is_admin")
    private boolean isAdmin;
    @Column(columnDefinition = "BIT",name = "is_active")
    private boolean isActive;
    @Column(columnDefinition = "DECIMAL", name = "f_person_id")
    private Long personId;
}
