package org.authentication.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "[permission]", schema = "authn")
@Entity(name = "permission")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Permission extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "NVARCHAR(50)")
    private String url;
    @Column(columnDefinition = "NVARCHAR(50)")
    private String name;
    @Column(name = "is_sensitive" , columnDefinition = "BIT")
    private Boolean isSensitive;
}
