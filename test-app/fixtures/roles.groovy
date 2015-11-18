fixture {
	UserIvars(User, givenname: 'ivar', surname: 'ivar', idCode: '14212128025')
	Roles(Role, authority: 'ROLE_TEST')
	UserRoless(UserRole, UserIvars, Roles)
}
