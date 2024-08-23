package telran.java53.accounting.model;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@EqualsAndHashCode(of = "login")
@Document(collection = "users")
public class UserAccount {
	@Id
	String login;
	@Setter
	String firstName;
	@Setter
	String lastName;

	@Setter
	String password;

	Set<String> roles = new HashSet<>();

	public UserAccount() {
		roles = new HashSet<>();
		roles.add("USER");
	}

	public UserAccount(String login, String firstName, String lastName, String password, Set<String> roles) {
		this();
		this.login = login;
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
		this.roles = roles;
	}

	public boolean addRole(String role) {
		return roles.add(role);
	}

	public boolean removeRole(String role) {
		return roles.remove(role);
	}

}
