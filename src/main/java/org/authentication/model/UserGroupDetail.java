package org.authentication.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "[user_group_detail]", schema = "authn")
@Entity(name = "userGroupDetail")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserGroupDetail extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "f_user_group_id")
    private UserGroup userGroup;
    @ManyToOne
    @JoinColumn(name = "f_user_id")
    private User user;
}
