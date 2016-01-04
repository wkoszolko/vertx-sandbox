package pl.koszolko.vertx.firstapp;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toConcurrentMap;

public class UserRepository {

    private static final Map<Long,User> USERS = new ConcurrentHashMap<>();

    public UserRepository() {
        addSomeData();
    }

    private void addSomeData() {
        USERS.putAll(
                Stream
                        .of("login69", "extra_user", "some_sample_login")
                        .map(User::new)
                        .collect(toConcurrentMap(User::getId, Function.<User>identity()))
        );
    }

    public Optional<User> get(long id) {
        return Optional.ofNullable(USERS.get(id));
    }

    public void add(User newUser) {
        USERS.put(newUser.getId(), newUser);
    }

    public boolean remove(long id) {
        return USERS.remove(id)!=null;
    }

    public Collection<User> getAll() {
        return Collections.unmodifiableCollection(USERS.values());
    }
}
