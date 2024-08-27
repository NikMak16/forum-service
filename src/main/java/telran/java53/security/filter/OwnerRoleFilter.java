package telran.java53.security.filter;

import java.io.IOException;
import java.security.Principal;
import java.util.Base64;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import telran.java53.accounting.dao.UserAccountRepository;
import telran.java53.accounting.model.Role;
import telran.java53.accounting.model.UserAccount;

@Component
@Order(15)
@RequiredArgsConstructor
public class OwnerRoleFilter implements Filter {

	final UserAccountRepository userAccountRepository;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		System.out.println("OwnerVerif");
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		if (checkEndpointOwnerOnly(request.getMethod(), request.getServletPath())) {
			try {
				String[] credentials = getCredentials(request.getHeader("Authorization"));
				String login = request.getServletPath().split("/")[3];
				if (!credentials[0].equals(login)) {
					throw new RuntimeException();
				}
				
			} catch (Exception e) {
				response.sendError(401);
				return;
			}
		}
		else if(checkEndpointOwnerOrAdmin(request.getMethod(), request.getServletPath())) {
			try {
				String[] credentials = getCredentials(request.getHeader("Authorization"));
				String login = request.getServletPath().split("/")[3];

				UserAccount userAccount = userAccountRepository.findById(credentials[0]).orElseThrow(RuntimeException::new);
				if (!(userAccount.getRoles().contains(Role.ADMINISTRATOR) || credentials[0].equals(login))) {
					throw new RuntimeException();
				}
				request = new WrappedRequest(request, userAccount.getLogin());
			}catch(Exception e) {
				response.sendError(401);
				return;
			}
		}
		chain.doFilter(request, response);

	}
	

	private boolean checkEndpointOwnerOrAdmin(String method, String path) {
		return method.equalsIgnoreCase("Delete") && path.matches("/account/user/([a-zA-Z0-9]+)");
	}

	private String[] getCredentials(String header) {
		String token = header.split(" ")[1];
		String decode = new String(Base64.getDecoder().decode(token));
		return decode.split(":");
	}

	private boolean checkEndpointOwnerOnly(String method, String path) {
		return (method.equalsIgnoreCase("Put") && path.matches("/account/user/([a-zA-Z0-9]+)"));
	}

	private class WrappedRequest extends HttpServletRequestWrapper{
		private String login;
		
		public WrappedRequest(HttpServletRequest request, String login) {
			super(request);
			this.login = login;
		}
		@Override
		public Principal getUserPrincipal() {
			return () -> login;
		}
	}
}
