package com.danilo.roombooking.config.setup;

import com.danilo.roombooking.service.PrivilegeService;
import com.danilo.roombooking.service.role.RoleService;
import com.danilo.roombooking.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    boolean alreadySetup = false;

    private final UserService userService;
    private final RoleService roleService;
    private final PrivilegeService privilegeService;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (alreadySetup) return;

        privilegeService.populateDatabase();
        roleService.populateDatabase();
        userService.createDefaultAdminIfNotExists();

        alreadySetup = true;
    }
}
