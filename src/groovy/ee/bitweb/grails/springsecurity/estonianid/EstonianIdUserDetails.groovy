package ee.bitweb.grails.springsecurity.estonianid

import org.springframework.security.core.GrantedAuthority

import ee.bitweb.grails.springsecurity.userdetails.GenericUser

/**
 * Created by ivar on 16.11.15.
 */
class EstonianIdUserDetails extends GenericUser {
    private String idCode
    private String givenname
    private String surname
    private String screenName

    public EstonianIdUserDetails(String idCode, String givenname, String surname, String screenName, Collection<GrantedAuthority> authorities, Object id) {
        super(authorities, id)
        if(idCode != null && !"".equals(idCode)) {
            this.idCode = idCode
            this.givenname = givenname
            this.surname = surname
            this.screenName = screenName
        } else {
            throw new IllegalArgumentException("Cannot pass null or empty values to constructor");
        }
    }


    public EstonianIdUserDetails(String idCode, String givenname, String surname, String screenName, Collection<? extends GrantedAuthority> authorities) {
        super(authorities)
        if(idCode != null && !"".equals(idCode)) {
            this.idCode = idCode
            this.givenname = givenname
            this.surname = surname
            this.screenName = screenName
        } else {
            throw new IllegalArgumentException("Cannot pass null or empty values to constructor");
        }
    }

    public String getIdCode() {
        return this.idCode;
    }

    public String getGivenname() {
        return this.givenname;
    }

    public String getSurname() {
        return this.surname;
    }

    public String getScreenName() {
        return this.screenName;
    }

    public boolean equals(Object rhs) {
        return rhs instanceof EstonianIdUserDetails?this.idCode.equals(((EstonianIdUserDetails)rhs).idCode):false;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append(": ");
        sb.append("IdCode: ").append(this.idCode).append("; ");
        sb.append("Givenname: ").append(this.givenname).append("; ");
        sb.append("Surname: ").append(this.surname).append("; ");

        return sb.toString();
    }
}
