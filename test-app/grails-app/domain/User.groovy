class User {
    transient springSecurityService

    enum Status
    {
        active, inactive
    }

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
        idCode2 blank: false, unique: true
        name blank: true, nullable: true
        //givenname blank: true, nullable: true
        //surname blank: true, nullable: true
    }

    Set<Role> getAuthorities() {
        UserRole.findAllByUser(this)*.role
    }

    def addRole(Role role) {
        new UserRole(user: this, role: role).save(flush: true)
    }
}