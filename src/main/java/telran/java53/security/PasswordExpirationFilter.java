package telran.java53.security;

import java.io.IOException;

import java.util.Base64;


import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;

import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import telran.java53.accounting.dao.UserAccountRepository;
import telran.java53.accounting.model.UserAccount;

@Component
@RequiredArgsConstructor
@Order(10)
public class PasswordExpirationFilter implements Filter {
	final UserAccountRepository repository;
	final CustomWebSecurity webSecurity;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		
		if (checkEndpoint(request.getMethod(), request.getServletPath())) {
			String[] credentials = getCredentials(request.getHeader("Authorization"));
			UserAccount userAccount = repository.findById(credentials[0]).orElseThrow(RuntimeException::new);
			if(!webSecurity.isPasswordNotExpired(userAccount.getLogin())) {
				response.sendError(403, "Your password is expired");
				return;
			}
		}
		
		chain.doFilter(request, response);

	}
	private boolean checkEndpoint(String method, String path) {
		return !((HttpMethod.POST.matches(method) && path.matches("/account/register"))
				|| (HttpMethod.GET.matches(method) && path.matches("/forum/posts.+"))
				||(HttpMethod.PUT.matches(method) && path.matches("/account/password")));
	}
	
	private String[] getCredentials(String header) {
		String token = header.split(" ")[1];
		String decode = new String(Base64.getDecoder().decode(token));
		return decode.split(":");
	}
	
	
}
