package ee.bitweb.grails.springsecurity.userdetails

import org.springframework.security.core.GrantedAuthority

/**
 * Created by ivar on 18.11.15.
 */
public interface GenericUserDetails extends Serializable {
    Collection<? extends GrantedAuthority> getAuthorities();
}