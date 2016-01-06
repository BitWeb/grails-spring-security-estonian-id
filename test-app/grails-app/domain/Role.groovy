class Role {

    public static final String ROLE_CUSTOMER = 'ROLE_USER'
    public static final String ROLE_ADMIN = 'ROLE_ADMIN'

    String authority

    static mapping = {
        autoImport false
        table '`role`'
        version false
    }
    static constraints = {
        authority blank: false, unique: true
    }
}
