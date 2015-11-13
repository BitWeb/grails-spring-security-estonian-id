import ee.bitweb.TestUser
import ee.bitweb.TestRole
import ee.bitweb.TestUserTestRole

fixture {
	TestUserIvar(TestUser, username: 'ivar', password: 'ivar', userIdCode: '14212128025')
	TestRoleTest(TestRole, authority: 'ROLE_TEST')
	TestUserTestRole(TestUserTestRole, TestUserIvar, TestRoleTest)
}
