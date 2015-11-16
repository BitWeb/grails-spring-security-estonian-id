/**
 * Created by ivar on 16.11.15.
 */
class User {
    transient springSecurityService

    public enum Status
    {
        active, inactive;
    }

    Long id
    String idCode
    String name
    String givenname
    String surname
    Status status = Status.active
    Date timeCreated
    Date timeUpdated

    boolean enabled = true
    boolean accountExpired
    boolean accountLocked
    boolean passwordExpired

    static mapping = {
        autoImport false
        table '`user`'
        version false
    }
    static constraints = {
        idCode blank: false, nullable: false, unique: true
        name blank: true, nullable: true
        givenname blank: true, nullable: true
        surname blank: true, nullable: true
        status blank: true, nullable: true
        timeCreated blank: true, nullable: true
        timeUpdated blank: true, nullable: true
    }

    Set<Role> getAuthorities() {
        UserRole.findAllByUser(this).collect { it.role }
    }

    def addRole(Role role) {
        new UserRole(user: this, role: role).save(flush: true)
    }
}