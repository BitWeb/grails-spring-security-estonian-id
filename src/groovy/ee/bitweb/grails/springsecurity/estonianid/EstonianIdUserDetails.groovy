package ee.bitweb.grails.springsecurity.estonianid

import org.springframework.security.core.GrantedAuthority

import ee.bitweb.grails.springsecurity.userdetails.GenericUser
import groovy.transform.EqualsAndHashCode

/**
 * @author ivar
 */
@EqualsAndHashCode(includes='idCode')
class EstonianIdUserDetails extends GenericUser {
    final String idCode
    final String givenname
    final String surname
    final String screenName

    EstonianIdUserDetails(String idCode, String givenname, String surname, String screenName, Collection<GrantedAuthority> authorities, id = null) {
        super(authorities, id)
        if (!idCode) {
            throw new IllegalArgumentException("Cannot pass null or empty values to constructor")
        }

        this.idCode = idCode
        this.givenname = givenname
        this.surname = surname
        this.screenName = screenName
    }

    String toString() {
        super.toString() + ": IdCode: " + idCode + "; Givenname: " + givenname + "; Surname: " + surname
    }
}
