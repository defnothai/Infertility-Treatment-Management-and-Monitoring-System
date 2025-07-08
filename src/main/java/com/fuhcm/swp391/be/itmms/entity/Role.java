package com.fuhcm.swp391.be.itmms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fuhcm.swp391.be.itmms.constant.AccountRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "Role")
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;

    public Role(AccountRole roleName) {
        this.roleName = roleName;
    }

    @Column(name = "RoleName", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private AccountRole roleName;

    @ManyToMany(mappedBy = "roles")
    @JsonIgnore
    private List<Account> accounts;

    @Override
    public String getAuthority() {
        return this.roleName.name();
    }


}
