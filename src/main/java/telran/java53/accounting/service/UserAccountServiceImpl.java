package telran.java53.accounting.service;

import org.mindrot.jbcrypt.BCrypt;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import telran.java53.accounting.dao.UserAccountRepository;
import telran.java53.accounting.dto.RolesDto;
import telran.java53.accounting.dto.UserDto;
import telran.java53.accounting.dto.UserEditDto;
import telran.java53.accounting.dto.UserRegisterDto;
import telran.java53.accounting.dto.exceptions.UserAlreadyExistsException;
import telran.java53.accounting.dto.exceptions.UserNotFoundException;
import telran.java53.accounting.model.UserAccount;

@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {
	
	final UserAccountRepository userAccountRepository;
	final ModelMapper modelMapper;
	
	@Override
	public UserDto register(UserRegisterDto userRegisterDto) {
		if(userAccountRepository.findById(userRegisterDto.getLogin()).isPresent()) {
			throw new UserAlreadyExistsException(); 
		}
		UserAccount user = modelMapper.map(userRegisterDto, UserAccount.class);
		String password = BCrypt.hashpw(userRegisterDto.getPassword(), BCrypt.gensalt());
		user.setPassword(password);
		userAccountRepository.save(user);
		return modelMapper.map(user, UserDto.class);
	}

	@Override
	public UserDto getUser(String login) {
		UserAccount user = userAccountRepository.findById(login).orElseThrow(UserNotFoundException::new);
		return modelMapper.map(user, UserDto.class);
	}

	@Override
	public UserDto removeUser(String login) {
		UserAccount user = userAccountRepository.findById(login).orElseThrow(UserNotFoundException::new);
		userAccountRepository.deleteById(login);
		return modelMapper.map(user, UserDto.class);
	}

	@Override
	public UserDto updateUser(String login, UserEditDto userEditDto) {
		UserAccount user = userAccountRepository.findById(login).orElseThrow(UserNotFoundException::new);
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
		UserAccount user = userAccountRepository.findById(login).orElseThrow(UserNotFoundException::new);
		boolean res;
		if(isAddRole) {
			res = user.addRole(role);
		}else {
			res = user.removeRole(role);
		}
		if(res) {userAccountRepository.save(user);}
		return modelMapper.map(user, RolesDto.class);
	}

	@Override
	public void changePassword(String login, String newPassword) {
		UserAccount user = userAccountRepository.findById(login).orElseThrow(UserNotFoundException::new);
		String password = BCrypt.hashpw(newPassword, BCrypt.gensalt());
		user.setPassword(password);
		userAccountRepository.save(user);
	}

}
