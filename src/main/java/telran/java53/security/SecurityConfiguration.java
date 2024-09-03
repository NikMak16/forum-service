package telran.java53.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

import lombok.RequiredArgsConstructor;
import telran.java53.accounting.dao.UserAccountRepository;
import telran.java53.accounting.model.Role;

@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {
	final UserAccountRepository userAccountRepository;
	final CustomWebSecurity webSecurity;

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.httpBasic(Customizer.withDefaults());
		http.csrf(csrf -> csrf.disable());
//		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS));

		http.addFilterAfter(new PasswordExpirationFilter(userAccountRepository, webSecurity), AuthorizationFilter.class)
		.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/account/register", "/forum/posts/**")
					.permitAll()
//				.requestMatchers("/account/user/**", "/forum/post/**")
//					.access((authentication, context) -> new AuthorizationDecision(webSecurity.isPasswordNotExpired(authentication.get().getName())))	
				.requestMatchers("/account/user/{login}/role/{role}")
					.hasRole(Role.ADMINISTRATOR.name())
				.requestMatchers(HttpMethod.PUT, "/account/user/{login}")
					.access(new WebExpressionAuthorizationManager("#login == authentication.name"))
				.requestMatchers(HttpMethod.DELETE, "/account/user/{login}")
					.access(new WebExpressionAuthorizationManager(
						"#login == authentication.name or hasRole('ADMINISTRATOR')"))
				.requestMatchers(HttpMethod.POST, "/forum/post/{author}")
					.access(new WebExpressionAuthorizationManager("#author == authentication.name"))
				.requestMatchers(HttpMethod.PUT, "/forum/post/{id}/comment/{author}")
					.access(new WebExpressionAuthorizationManager("#author == authentication.name"))
				.requestMatchers(HttpMethod.PUT, "/forum/post/{id}")
					.access((authentication, context) -> new AuthorizationDecision(
						webSecurity.checkPostAuthor(context.getVariables().get("id"), authentication.get().getName())))
				.requestMatchers(HttpMethod.DELETE, "/forum/post/{id}")
				.access((authentication, context) -> {
					boolean checkAuthor = webSecurity.checkPostAuthor(context.getVariables().get("id"),
							authentication.get().getName());
					System.out.println(checkAuthor);
					boolean checkModerator = context.getRequest().isUserInRole(Role.MODERATOR.name());
					return new AuthorizationDecision(checkAuthor || checkModerator);

				})
				.anyRequest()
					.authenticated());
		return http.build();
	}
}
