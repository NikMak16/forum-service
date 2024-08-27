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
import telran.java53.post.dao.PostRepository;
import telran.java53.post.model.Post;
@Component
@RequiredArgsConstructor
@Order(17)
public class AuthorFilter implements Filter {
	
	final UserAccountRepository userAccountRepository;
	final PostRepository postRepository;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		if(checkCommentEndpoint(request.getMethod(), request.getServletPath())) {
			try {
				String[] credentials = getCredentials(request.getHeader("Authorization"));
				UserAccount userAccount = userAccountRepository.findById(credentials[0]).orElseThrow(RuntimeException::new);
				if(!userAccount.getLogin().equals(request.getServletPath().split("/")[5])) {
					throw new RuntimeException();
				}
			} catch (Exception e) {
				response.sendError(401);
				return;
			}
		}
		
		if(checkEndpointAuthorOnly(request.getMethod(), request.getServletPath())) {
			try {
				String[] credentials = getCredentials(request.getHeader("Authorization"));
				String postId = request.getServletPath().split("/")[3];
				UserAccount userAccount = userAccountRepository.findById(credentials[0]).orElseThrow(RuntimeException::new);
				Post post = postRepository.findById(postId).orElseThrow(RuntimeException::new);
				if(!(userAccount.getLogin().equals(post.getAuthor()))) {
					throw new RuntimeException();
				}
			}catch(Exception e) {
				response.sendError(401);
				return;
			}
			
		}else if(checkEndpointAuthorOrModerator(request.getMethod(), request.getServletPath())) {
			try {
				String[] credentials = getCredentials(request.getHeader("Authorization"));
				String postId = request.getServletPath().split("/")[3];
				UserAccount userAccount = userAccountRepository.findById(credentials[0]).orElseThrow(RuntimeException::new);
				Post post = postRepository.findById(postId).orElseThrow(RuntimeException::new);
				if(!(userAccount.getLogin().equals(post.getAuthor()) || userAccount.getRoles().contains(Role.MODERATOR))) {
					throw new RuntimeException();
				}
			}catch(Exception e) {
				response.sendError(401);
				return;
		}
		}
		chain.doFilter(request, response);
		
	}

	
	private boolean checkCommentEndpoint(String method, String path) {
		return(method.equalsIgnoreCase("Delete") && path.matches("/forum/post/([a-zA-Z0-9]+)/comment/([a-zA-Z0-9]+)"));
	}


	private String[] getCredentials(String header) {
		String token = header.split(" ")[1];
		String decode = new String(Base64.getDecoder().decode(token));
		return decode.split(":");
	}

	private boolean checkEndpointAuthorOnly(String method, String path) {
		return 	(method.equalsIgnoreCase("Post") && path.matches("/forum/post/([a-zA-Z0-9]+)"))
				|| (method.equalsIgnoreCase("Put") && path.matches("/forum/post/([a-zA-Z0-9]+)"));
	}
	
	private boolean checkEndpointAuthorOrModerator(String method, String path) {
		return method.equalsIgnoreCase("Delete") && path.matches("/forum/post/([a-zA-Z0-9]+)");
	}

	
}
