package io.github.susimsek.springbootssodemo.bootstrap;

import io.github.susimsek.springbootssodemo.model.User;
import io.github.susimsek.springbootssodemo.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserLoader implements CommandLineRunner {

    final UserService userService;

    @Override
    public void run(String... args) {
        if (userService.count() == 0) {
            createInitialUsers();
        }
    }

    private void createInitialUsers() {
        IntStream.rangeClosed(1, 25)
                .forEach(i -> {
                    User user = User.builder()
                            .username("user" + i)
                            .email("display" + i + "@gmail.com")
                            .password("P4ssword")
                            .build();
                    userService.save(user);
                });
    }
}
