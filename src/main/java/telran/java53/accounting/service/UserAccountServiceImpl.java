package telran.java53.accounting.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import telran.java53.accounting.dao.UserAccountRepository;
import telran.java53.accounting.dto.RolesDto;
import telran.java53.accounting.dto.UserDto;
import telran.java53.accounting.dto.UserEditDto;
import telran.java53.accounting.dto.UserRegisterDto;
import telran.java53.accounting.dto.exceptions.UserNotFoundException;
import telran.java53.accounting.model.User;

@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {
	
	final UserAccountRepository userAccountRepository;
	final ModelMapper modelMapper;
	
	@Override
	public UserDto register(UserRegisterDto userRegisterDto) {
		if(userAccountRepository.findById(userRegisterDto.getLogin()).isPresent()) {
			return null; //what to return/throw in case if login is already taken?
		}
		User user = modelMapper.map(userRegisterDto, User.class);
		userAccountRepository.save(user);
		return modelMapper.map(user, UserDto.class);
	}

	@Override
	public UserDto getUser(String login) {
		User user = userAccountRepository.findById(login).orElseThrow(UserNotFoundException::new);
		return modelMapper.map(user, UserDto.class);
	}

	@Override
	public UserDto removeUser(String login) {
		User user = userAccountRepository.findById(login).orElseThrow(UserNotFoundException::new);
		userAccountRepository.deleteById(login);
		return modelMapper.map(user, UserDto.class);
	}

	@Override
	public UserDto updateUser(String login, UserEditDto userEditDto) {
		User user = userAccountRepository.findById(login).orElseThrow(UserNotFoundException::new);
		if(userEditDto.getFirstName() != null) {
			user.setFirstName(userEditDto.getFirstName());
		}
		if(userEditDto.getLastName() != null) {
			user.setLastName(userEditDto.getLastName());
		}
		userAccountRepository.save(user);
		
		return modelMapper.map(user, UserDto.class);
	}

	@Override
	public RolesDto changeRolesList(String login, String role, boolean isAddRole) {
		User user = userAccountRepository.findById(login).orElseThrow(UserNotFoundException::new);
		if(isAddRole) {
			user.addRole(role);
		}else {
			user.removeRole(role);
		}
		userAccountRepository.save(user);
		return modelMapper.map(user, RolesDto.class);
	}

	@Override
	public void changePassword(String login, String newPassword) {
		User user = userAccountRepository.findById(login).orElseThrow(UserNotFoundException::new);
		user.setPassword(newPassword);
		userAccountRepository.save(user);
	}

}
