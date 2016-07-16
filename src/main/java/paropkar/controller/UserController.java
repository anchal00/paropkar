package paropkar.controller;

import com.google.gson.Gson;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import paropkar.dao.DataAccessor;
import paropkar.dao.UserDAO;
import paropkar.model.User;

import java.sql.Timestamp;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@RestController
public class UserController {
    private final UserDAO userDAO;
    private final Random random;

    public UserController() {
        this.userDAO = new UserDAO(DataAccessor.getDataAccessor());
        random = new Random();
    }

    @RequestMapping("/register")
    public CompletableFuture<ResponseEntity<String>> register(@RequestBody final User user) {
        return userDAO.insert(getParams(user))
                .thenApply(count -> {
                    if (count > 0) {
                        return ResponseEntity.ok().body("{}");
                    } else {
                        return new ResponseEntity<>("{\"Error\": \"Failed to insert into database\"}",
                                HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }).exceptionally(throwable -> {
                    throwable.printStackTrace();
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                });
    }

    @RequestMapping("/getUser")
    public CompletableFuture<ResponseEntity<User>> getUser(@RequestBody final String id) {
        return userDAO.getObject(id)
                .thenApply(complaint -> ResponseEntity.ok().body(complaint))
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                });
    }

    @RequestMapping("/getAllUsers")
    public CompletableFuture<ResponseEntity<List<User>>> getAllUsers() {
        return userDAO.getAll()
                .thenApply(complaints -> ResponseEntity.ok().body(complaints))
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                });
    }

    private Object[] getParams(final User user) {
        return new Object[]{
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getAadhaarNumber(),
                user.getCity(),
                user.getAddress(),
                user.getPhoneNumber(),
                new Timestamp(new java.util.Date().getTime()),
                random.nextInt(),
                user.getTwitterHandle()
        };
    }

    public static void main(String[] args) {
        System.out.println(new Gson().toJson(new User("Gaurav Sen", "gauravsen92@gmail.com", "gaurav", "63413",
                "Mumbai",
                "Bandra", "+919920533241", "gkcs")));
    }
}
