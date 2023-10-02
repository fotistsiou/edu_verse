package unipi.fotistsiou.eduguide.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unipi.fotistsiou.eduguide.repository.RoleRepository;
import unipi.fotistsiou.eduguide.entity.Role;
import java.util.Optional;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    @Autowired
    public RoleService (
            RoleRepository roleRepository
    ){
        this.roleRepository = roleRepository;
    }

    public Optional<Role> findRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    public void saveRole(Role role) {
        roleRepository.save(role);
    }
}