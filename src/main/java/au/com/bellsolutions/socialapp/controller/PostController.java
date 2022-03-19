package au.com.bellsolutions.socialapp.controller;

import au.com.bellsolutions.socialapp.model.Post;
import au.com.bellsolutions.socialapp.model.User;
import au.com.bellsolutions.socialapp.model.Vote;
import au.com.bellsolutions.socialapp.repository.PostRepository;
import au.com.bellsolutions.socialapp.repository.UserRepository;
import au.com.bellsolutions.socialapp.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class PostController {

    @Autowired
    PostRepository repository;

    @Autowired
    VoteRepository voteRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/api/posts")
    public List<Post> getAllPosts() {
        List<Post> postList = repository.findAll();
        for (Post p : postList) {
            p.setVoteCount(voteRepository.countVotesByPostId(p.getId()));
        }
        return postList;
    }


    @GetMapping("/api/posts/{id}")
    public Post getPost(@PathVariable Integer id) {
        Post returnPost = repository.getOne(id);
        returnPost.setVoteCount(voteRepository.countVotesByPostId(returnPost.getId()));

        return returnPost;
    }


    @PostMapping("/api/posts")
    @ResponseStatus(HttpStatus.CREATED)
    public Post addPost(@RequestBody Post post) {
        repository.save(post);
        return post;
    }


    @PutMapping("/api/posts/{id}")
    public Post updatePost(@PathVariable int id, @RequestBody Post post) {
        Post tempPost = repository.getOne(id);
        tempPost.setTitle(post.getTitle());
        return repository.save(tempPost);
    }


    @PutMapping("/api/posts/upvote")
    public String addVote(@RequestBody Vote vote, HttpServletRequest request) {
        String returnValue = "";

        if(request.getSession(false) != null) {
            Post returnPost = null;

            User sessionUser = (User) request.getSession().getAttribute("SESSION_USER");
            vote.setUserId(sessionUser.getId());
            voteRepository.save(vote);

            returnPost = repository.getOne(vote.getPostId());
            returnPost.setVoteCount(voteRepository.countVotesByPostId(vote.getPostId()));

            returnValue = "";
        } else {
            returnValue = "login";
        }

        return returnValue;
    }


    @DeleteMapping("/api/posts/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(@PathVariable int id) {
        repository.deleteById(id);
    }
}
