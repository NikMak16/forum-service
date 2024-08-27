package telran.java53.security.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.core.annotation.Order;
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
			System.out.println("Endpoint checked");
			try {
				System.out.println("try block");
				String[] credentials = getCredentials(request.getHeader("Authorization"));
				System.out.println("checking login");
				System.out.println(request.getServletPath());
				String login = request.getServletPath().split("/")[3];
				System.out.println("Login parameter: " + login);
				if (!credentials[0].equals(login)) {
					throw new RuntimeException();
				}
				System.out.println("login checked");
			} catch (Exception e) {
				response.sendError(401);
				return;
			}
		}
		else if(checkEndpointOwnerOrAdmin(request.getMethod(), request.getServletPath())) {
			try {
				String[] credentials = getCredentials(request.getHeader("Authorization"));
				System.out.println("checking login");
				System.out.println(request.getServletPath());
				String login = request.getServletPath().split("/")[3];
				System.out.println("Login parameter: " + login);

				UserAccount userAccount = userAccountRepository.findById(credentials[0]).orElseThrow(RuntimeException::new);
				if (!(userAccount.getRoles().contains(Role.ADMINISTRATOR) || credentials[0].equals(login))) {
					throw new RuntimeException();
				}
				System.out.println("login or admin checked");
			}catch(Exception e) {
				response.sendError(401);
				return;
			}
		}
		System.out.println("try block ended");
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
		return (method.equalsIgnoreCase("Put") && path.matches("/account/user/([a-zA-Z0-9]+)"))
				|| (method.equalsIgnoreCase("Put") && path.matches("/account/password"));
	}

}
