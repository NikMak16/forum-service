package telran.java53.security;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import telran.java53.accounting.dao.UserAccountRepository;
import telran.java53.accounting.model.UserAccount;
import telran.java53.post.dao.PostRepository;
import telran.java53.post.model.Post;

@Service
@RequiredArgsConstructor
public class CustomWebSecurity {
	final UserAccountRepository userRepository;
	final PostRepository postRepository;
	
	public boolean checkPostAuthor(String postId, String userName) {
		Post post = postRepository.findById(postId).orElse(null);
		return post != null && post.getAuthor().equalsIgnoreCase(userName);
		
	}
	
	public boolean isPasswordNotExpired(String userId) {
		UserAccount userAccount = userRepository.findById(userId).orElse(null);
		
//		Tried to check if user have no passwExpirationDate and then set it to LocalDate.now()
		if (userAccount != null) {
			Optional<LocalDate> expirationDate= Optional.ofNullable(userAccount.getPasswExpirationDate());
			if(expirationDate.isEmpty()) {
				userAccount.setPasswExpirationDate(LocalDate.now());
				userRepository.save(userAccount);
			}
		}
		return userAccount != null && LocalDate.now().isBefore(userAccount.getPasswExpirationDate());
	}
}
