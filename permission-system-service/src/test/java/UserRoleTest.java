import net.myplayplanet.permission.service.PermissionSystemApplication;
import net.myplayplanet.permission.service.controller.RoleController;
import net.myplayplanet.permission.service.controller.ScopeController;
import net.myplayplanet.permission.service.controller.UserController;
import net.myplayplanet.permission.service.dto.RoleDto;
import net.myplayplanet.permission.service.dto.ScopeDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Set;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = PermissionSystemApplication.class) // Starts a minimal server
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class UserRoleTest {


    @Autowired
    private UserController userController;
    @Autowired
    private ScopeController scopeController;
    @Autowired
    private RoleController roleController;

    @Test
    public void twoUsersToSameRoleTest() {

        ScopeDto scopeDto = this.scopeController.create("testScope");
        RoleDto leader = this.roleController.createRole(scopeDto.getId(), new RoleDto(null, "Leader", "", 10, Set.of(), false));
        UUID userOne = UUID.randomUUID();
        UUID userTwo = UUID.randomUUID();
        this.userController.addRole(scopeDto.getId(), userOne, leader.getId());
        this.userController.addRole(scopeDto.getId(), userTwo, leader.getId());
    }

}
