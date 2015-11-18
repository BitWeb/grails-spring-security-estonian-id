fixture {
	UserIvars(User, givenname: 'ivar', surname: 'ivar', idCode: '14212128025')
	Roles(Role, authority: 'ROLE_TEST')
	Roles2(Role, authority: 'ROLE_DEFAULT')
	UserRoless(UserRole, UserIvars, Roles)
}
