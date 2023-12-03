package org.authentication.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "[user_group]", schema = "authn")
@Entity(name = "userGroup")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserGroup extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "NVARCHAR(50)")
    private String name;
}
