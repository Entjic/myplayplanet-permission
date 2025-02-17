import net.myplayplanet.permission.service.PermissionSystemApplication;
import net.myplayplanet.permission.service.mapper.EntityMapper;
import net.myplayplanet.permission.service.model.Permission;
import net.myplayplanet.permission.service.model.Role;
import net.myplayplanet.permission.service.model.Scope;
import net.myplayplanet.permission.service.repository.PermissionRepository;
import net.myplayplanet.permission.service.repository.RoleRepository;
import net.myplayplanet.permission.service.service.RolePermissionFixService;
import net.myplayplanet.permission.service.service.RoleService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = PermissionSystemApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class RoleEditTest {

    @Autowired
    private EntityMapper entityMapper;

    private final Permission permissionA = new Permission("test.permissionA");
    private final Permission permissionB = new Permission("test.permissionB");
    private final Permission permissionC = new Permission("test.permissionC");

    private final Scope scope = new Scope(1L, "TestScope");


    @Test
    public void insertNewValidRoleTest() {
        RoleRepository roleRepository = mock();
        PermissionRepository permissionRepository = mock();
        RoleService roleService = new RoleService(entityMapper, roleRepository, new RolePermissionFixService(permissionRepository));
        Role role = validRole();
        assertDoesNotThrow(() -> roleService.save(role));
    }

    @Test
    public void insertNewInvalidRoleTest() {
        RoleRepository roleRepository = mock();
        PermissionRepository permissionRepository = mock();
        RoleService roleService = new RoleService(entityMapper, roleRepository, new RolePermissionFixService(permissionRepository));

        Role role = invalidRole();
        Assertions.assertThrows(ResponseStatusException.class, () -> roleService.save(role));
    }

    private Role validRole() {
        return new Role(scope, "Valid role", 10, Set.of(permissionA), Set.of(permissionB));
    }

    private Role invalidRole() {
        return new Role(scope, "Invalid role", 5, Set.of(permissionC), Set.of(permissionC));
    }


}
