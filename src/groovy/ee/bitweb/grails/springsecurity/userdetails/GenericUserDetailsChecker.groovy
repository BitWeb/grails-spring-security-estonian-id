package ee.bitweb.grails.springsecurity.userdetails

import org.springframework.security.core.userdetails.User
/**
 * Created by ivar on 18.11.15.
 */
public interface GenericUserDetailsChecker {
    void check(GenericUserDetails var1);
}