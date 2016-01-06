package ee.bitweb.grails.springsecurity.userdetails

import org.springframework.security.core.GrantedAuthority

/**
 * @author ivar
 */
interface GenericUserDetails extends Serializable {
    Collection<? extends GrantedAuthority> getAuthorities()
}
