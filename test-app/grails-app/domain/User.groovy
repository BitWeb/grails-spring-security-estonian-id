class User {
    transient springSecurityService

    public enum Status
    {
        active, inactive;
    }

    Long id
    String idCode2
    String name
    //String givenname
    //String surname
    Status status = Status.active
    //Date timeCreated
    //Date timeUpdated

    static mapping = {
        autoImport false
        table '`user`'
        version false
    }
    static constraints = {
        idCode2 blank: false, nullable: false, unique: true
        name blank: true, nullable: true
        //givenname blank: true, nullable: true
        //surname blank: true, nullable: true
    }

    Set<Role> getAuthorities() {
        UserRole.findAllByUser(this).collect { it.role }
    }

    def addRole(Role role) {
        new UserRole(user: this, role: role).save(flush: true)
    }
}