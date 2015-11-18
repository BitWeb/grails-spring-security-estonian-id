package ee.bitweb.grails.springsecurity.userdetails

import org.springframework.security.core.CredentialsContainer
import org.springframework.security.core.GrantedAuthority
import org.springframework.util.Assert

/**
 * Created by ivar on 16.11.15.
 */
class GenericUser implements GenericUserDetails{
    private final Object id
    private final Set<GrantedAuthority> authorities

    public GenericUser(Collection<? extends GrantedAuthority> authorities) {
        this.id = null
        this.authorities = Collections.unmodifiableSet(sortAuthorities(authorities))
    }

    public GenericUser(Collection<? extends GrantedAuthority> authorities, Object id) {
        this.id = id
        this.authorities = Collections.unmodifiableSet(sortAuthorities(authorities))
    }

    public Collection<GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    public Object getId() {
        return id;
    }

    private static SortedSet<GrantedAuthority> sortAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Assert.notNull(authorities, "Cannot pass a null GrantedAuthority collection");
        TreeSet sortedAuthorities = new TreeSet(new GenericUser.AuthorityComparator());
        Iterator i$ = authorities.iterator();

        while(i$.hasNext()) {
            GrantedAuthority grantedAuthority = (GrantedAuthority)i$.next();
            Assert.notNull(grantedAuthority, "GrantedAuthority list cannot contain any null elements");
            sortedAuthorities.add(grantedAuthority);
        }

        return sortedAuthorities;
    }

    public boolean equals(Object rhs) {
        return rhs instanceof GenericUser?this.id.equals(((GenericUser)rhs).id):false;
    }

    public int hashCode() {
        return this.idCode.hashCode();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append(": ");
        if(!this.authorities.isEmpty()) {
            sb.append("Granted Authorities: ");
            boolean first = true;
            Iterator i$ = this.authorities.iterator();

            while(i$.hasNext()) {
                GrantedAuthority auth = (GrantedAuthority)i$.next();
                if(!first) {
                    sb.append(",");
                }

                first = false;
                sb.append(auth);
            }
        } else {
            sb.append("Not granted any authorities");
        }

        return sb.toString();
    }

    private static class AuthorityComparator implements Comparator<GrantedAuthority>, Serializable {
        private static final long serialVersionUID = 320L;

        private AuthorityComparator() {
        }

        public int compare(GrantedAuthority g1, GrantedAuthority g2) {
            return g2.getAuthority() == null?-1:(g1.getAuthority() == null?1:g1.getAuthority().compareTo(g2.getAuthority()));
        }
    }
}
