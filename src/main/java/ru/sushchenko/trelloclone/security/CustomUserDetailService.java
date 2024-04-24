package ru.sushchenko.trelloclone.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.sushchenko.trelloclone.entity.User;
import ru.sushchenko.trelloclone.repo.UserRepo;
import ru.sushchenko.trelloclone.utils.exception.UserNotFoundException;

@Component
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepo userRepo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        return UserPrincipal.builder()
                .user(user)
                .build();
    }
}
