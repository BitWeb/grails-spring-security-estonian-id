package ee.bitweb.grails.springsecurity.estonianid

import org.springframework.security.core.GrantedAuthority

/**
 * Created by ivar on 12.11.15.
 */
public interface EstonianIdAuthenticationDao<T> {

    /**
     * Try to find existing user for Twitter Token (search by screen name or user id)
     * @param token token for user that has been authenticated by plugin filter
     * @return existing user or null if plugin should create a new one
     */
    T findUser(EstonianIdAuthenticationToken token)

    /**
     * Create a new user
     *
     * @param token current Authentication Token
     * @return created used
     */
    T create(EstonianIdAuthenticationToken token)

    /**
     * Make sure that existing user has up-to-date details (like access token or screenname)
     * @param user current user
     * @param token fresh token
     */
    void updateIfNeeded(T user, EstonianIdAuthenticationToken token)

    /**
     * @param instance of App User related to specified Twitter User (it could be same object / same instance)
     * @return App User
     */
    Object getAppUser(T user)

    /**
     * Principal for Spring Security. Could be any object, but UserDetails instance is preferred
     * @param user
     * @return
     */
    Object getPrincipal(Object user)

    /**
     *
     * @param user current Twitter User
     * @return list of authorities
     */
    Collection<GrantedAuthority> getRoles(T user)
}