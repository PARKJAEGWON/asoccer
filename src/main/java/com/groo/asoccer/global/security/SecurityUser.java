package com.groo.asoccer.global.security;

import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
//시큐리티 User를 가공함
public class SecurityUser extends User{

    @Getter
    private long memberId;

    public SecurityUser(long memberId, String memberLoginId, String password, Collection<? extends GrantedAuthority> authorities) {
        super(memberLoginId, password, authorities);
        this.memberId = memberId;
    }


    public Authentication genAuthentication() {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                this,
                this.getPassword(),
                this.getAuthorities()
        );
        return auth;
    }
}