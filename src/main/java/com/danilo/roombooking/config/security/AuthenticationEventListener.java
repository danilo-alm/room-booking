package com.danilo.roombooking.config.security;

import com.danilo.roombooking.domain.User;
import com.danilo.roombooking.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationEventListener {

    private final UserService userService;

    @EventListener
    @Transactional
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        User user = getCurrentUser(event);

        if (user.getFailedLoginAttempts() > 0)
            user.setFailedLoginAttempts(0);
    }

    @EventListener
    @Transactional
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
        User user = getCurrentUser(event);

        int failedLoginAttempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(failedLoginAttempts);

        if (failedLoginAttempts >= SecurityConstants.MAX_LOGIN_RETRIES) {
            user.setEnabled(false);
            log.info("User {} disabled due to too many login attempts", user.getUsername());
        }
    }

    private User getCurrentUser(AbstractAuthenticationEvent event) {
        String username = event.getAuthentication().getName();
        return userService.getByUsername(username);
    }

}
