class Role {

    public static String ROLE_CUSTOMER = 'ROLE_USER'
    public static String ROLE_ADMIN = 'ROLE_ADMIN'

    Long id
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