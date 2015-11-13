package ee.bitweb

class TestUser implements Serializable {

	private static final long serialVersionUID = 1

	transient springSecurityService

    String userIdCode
	String username
	String password
	boolean enabled = true
	boolean accountExpired
	boolean accountLocked
	boolean passwordExpired

	@Override
	int hashCode() {
		username?.hashCode() ?: 0
	}

	@Override
	boolean equals(other) {
		is(other) || (other instanceof TestUser && other.username == username)
	}

	@Override
	String toString() {
		username
	}

	Set<TestRole> getAuthorities() {
		TestUserTestRole.findAllByTestUser(this)*.testRole
	}

	def beforeInsert() {
		encodePassword()
	}

	def beforeUpdate() {
		if (isDirty('password')) {
			encodePassword()
		}
	}

	protected void encodePassword() {
		password = springSecurityService?.passwordEncoder ? springSecurityService.encodePassword(password) : password
	}

	static transients = ['springSecurityService']

	static constraints = {
		username blank: false, unique: true
		password blank: false
	}

	static mapping = {
		password column: '`password`'
	}
}
