package telran.java53.post.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import telran.java53.post.dto.DatePeriodDto;
import telran.java53.post.dto.NewCommentDto;
import telran.java53.post.dto.NewPostDto;
import telran.java53.post.dto.PostDto;
import telran.java53.post.service.PostService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/forum")
public class PostController {

	final PostService postService;

	@PostMapping("/post/{author}")
	public PostDto addNewPost(@PathVariable String author, @RequestBody NewPostDto newPostDto) {
		return postService.addNewPost(author, newPostDto);
	}

	@GetMapping("/post/{id}")
	public PostDto findPostById(@PathVariable String id) {
		return postService.findPostById(id);
	}

	@DeleteMapping("/post/{id}")
	public PostDto removePost(@PathVariable String id) {
		return postService.removePost(id);
	}

	@PutMapping("/post/{id}")
	public PostDto updatePost(@PathVariable String id, @RequestBody NewPostDto newPostDto) {
		return postService.updatePost(id, newPostDto);
	}

	@PutMapping("/post/{id}/comment/{author}")
	public PostDto addComment(@PathVariable String id, @PathVariable String author,
			@RequestBody NewCommentDto newCommentDto) {
		return postService.addComment(id, author, newCommentDto);
	}

	@PutMapping("/post/{id}/like")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void addLike(@PathVariable String id) {
		postService.addLike(id);

	}

	@GetMapping("/posts/author/{author}")
	public Iterable<PostDto> findPostsByAuthor(@PathVariable String author) {
		return postService.findPostsByAuthor(author);
	}

	@GetMapping("/posts/tags")
	public Iterable<PostDto> findPostsByTags(@RequestParam List<String> values) {
		return postService.findPostsByTags(values);
	}

	@GetMapping("/posts/period")
	public Iterable<PostDto> findPostsByPeriod(@RequestParam LocalDate dateFrom, @RequestParam LocalDate dateTo) {
		return postService.findPostsByPeriod(new DatePeriodDto(dateFrom, dateTo));
	}

}
