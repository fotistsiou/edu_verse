package unipi.fotistsiou.eduverse.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import unipi.fotistsiou.eduverse.entity.Role;
import unipi.fotistsiou.eduverse.entity.User;
import unipi.fotistsiou.eduverse.service.RoleService;
import unipi.fotistsiou.eduverse.service.UserService;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class SeedDataConfig implements CommandLineRunner {
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public SeedDataConfig (
        UserService userService,
        RoleService roleService
    ){
        this.userService = userService;
        this.roleService = roleService;
    }

    @Override
    public void run(String... args) throws Exception {
        Optional<User> optionalUser = userService.findUserByEmail("t.alepis@eduguide.gr");
        if (optionalUser.isEmpty()) {
            Role professor = new Role();
            professor.setName("ROLE_PROFESSOR");
            roleService.saveRole(professor);

            Role student = new Role();
            student.setName("ROLE_STUDENT");
            roleService.saveRole(student);

            User user1 = new User();
            User user2 = new User();

            user1.setEmail("t.alepis@eduguide.gr");
            user1.setPassword("1234qwer");
            user1.setFirstName("Ευθύμιος");
            user1.setLastName("Αλέπης");
            user1.setTelephone("2101245789");
            Set<Role> roles1 = new HashSet<>();
            roleService.findRoleByName("ROLE_PROFESSOR").ifPresent(roles1::add);
            user1.setRoles(roles1);

            user2.setEmail("f.tsioumas@eduguide.gr");
            user2.setPassword("1234qwer");
            user2.setFirstName("Φώτιος");
            user2.setLastName("Τσιούμας");
            Set<Role> roles2 = new HashSet<>();
            roleService.findRoleByName("ROLE_STUDENT").ifPresent(roles2::add);
            user2.setRoles(roles2);

            userService.saveUser(user1, user1.getRoles().toString());
            userService.saveUser(user2, user1.getRoles().toString());

            userService.addAm(user1, user1.getRoles().toString());
            userService.addAm(user2, user2.getRoles().toString());
        }
    }
}