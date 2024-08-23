package telran.java53.accounting.service;

import telran.java53.accounting.dto.RolesDto;
import telran.java53.accounting.dto.UserDto;
import telran.java53.accounting.dto.UserEditDto;
import telran.java53.accounting.dto.UserRegisterDto;

public interface UserAccountService {
	UserDto register(UserRegisterDto userRegisterDto);
	
	UserDto getUser(String login);
	
	UserDto removeUser(String login);
	
	UserDto updateUser(String login, UserEditDto userEditDto);
	
	RolesDto changeRolesList(String login, String role, boolean isAddRole);
	
	void changePassword(String login, String newPassword);
}
