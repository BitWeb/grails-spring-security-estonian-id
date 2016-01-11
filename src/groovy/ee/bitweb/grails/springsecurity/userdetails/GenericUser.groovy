package ee.bitweb.grails.springsecurity.userdetails

import groovy.transform.EqualsAndHashCode

import org.springframework.security.core.GrantedAuthority

/**
 * @author ivar
 */
@EqualsAndHashCode(includes='id')
class GenericUser implements GenericUserDetails {
    final id
    final Set<GrantedAuthority> authorities

    GenericUser(Collection<? extends GrantedAuthority> authorities) {
        assert authorities != null, "Cannot pass a null GrantedAuthority collection"
        authorities.each { assert it, "GrantedAuthority list cannot contain any null elements" }
        this.authorities = (authorities.sort { it.authority } as Set).asImmutable()
    }

    GenericUser(Collection<? extends GrantedAuthority> authorities, id) {
        this(authorities)
        this.id = id
    }

    String toString() {
        StringBuilder sb = new StringBuilder()
        sb << super.toString() << ": "
        if (authorities) {
            sb << "Granted Authorities: " << authorities*.authority.join(',')
        } else {
            sb.append("Not granted any authorities")
        }

        sb
    }
}
