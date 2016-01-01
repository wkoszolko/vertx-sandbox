package pl.koszolko.vertx.firstapp;

import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

public class UserRepository {

    private static final List<User> USERS = new ArrayList<>();

    public UserRepository() {
        addSomeData();
    }

    private void addSomeData() {
        USERS.addAll(
                Stream
                        .of("login69", "extra_user", "some_sample_login")
                        .map(User::new)
                        .collect(toSet())
        );
    }

    public Optional<User> get(long id) {
        return USERS
                .stream()
                .filter(user -> user.getId() == id)
                .findFirst();
    }

    public boolean add(User newUser) {
        return USERS.add(newUser);
    }

    public boolean remove(long id) {
        return USERS.removeIf(user -> user.getId() == id);
    }

    public List<User> getAll() {
        return Collections.unmodifiableList(USERS);
    }
}
