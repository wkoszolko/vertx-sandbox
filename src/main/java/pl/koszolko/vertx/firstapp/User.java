package pl.koszolko.vertx.firstapp;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
@EqualsAndHashCode
public class User {

    private static final AtomicInteger COUNTER = new AtomicInteger();

    private long id;
    private String login;

    public User() {
        setId(COUNTER.getAndIncrement());
    }

    public User(String login) {
        this();
        this.login = login;
    }
}
