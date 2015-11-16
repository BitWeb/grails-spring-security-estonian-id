import ee.bitweb.TestUser
import ee.bitweb.TestRole
import ee.bitweb.TestUserTestRole

import ee.bitweb.User
import ee.bitweb.Role
import ee.bitweb.UserRole

fixture {
	TestUserIvar(TestUser, username: 'ivar', password: 'ivar', idCode: '14212128025')
	TestRoleTest(TestRole, authority: 'ROLE_TEST')
	TestUserTestRole(TestUserTestRole, TestUserIvar, TestRoleTest)
	
	UserIvars(User, givenname: 'ivar', surname: 'ivar', idCode: '14212128025')
	Roles(Role, authority: 'ROLE_TEST')
	UserRoless(UserRole, UserIvars, Roles)
}
