import net.myplayplanet.permission.core.dto.EffectiveUserModelDto;
import net.myplayplanet.permission.service.model.Permission;
import net.myplayplanet.permission.service.model.Role;
import net.myplayplanet.permission.service.model.User;
import net.myplayplanet.permission.service.repository.UserRepository;
import net.myplayplanet.permission.service.service.RoleService;
import net.myplayplanet.permission.service.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Set;
import java.util.UUID;

public class EffectivePermissionModelTest {


    private static UserService userService;

    private final Permission permissionA = new Permission(UUID.randomUUID());
    private final Permission permissionB = new Permission(UUID.randomUUID());
    private final Permission permissionC = new Permission(UUID.randomUUID());


    @BeforeAll
    static void setUp(){
        userService = new UserService(Mockito.mock(UserRepository.class), new RoleService());
    }

    @Test
    public void collidingRolePermissionsTest(){

        User user = new User(UUID.randomUUID(),
                Set.of(generateRoleA(), generateRoleB()), Set.of(), Set.of());

        EffectiveUserModelDto effectiveUserModel = userService.getEffectiveUserModel(user);

        Assertions.assertEquals(user.getUuid(), effectiveUserModel.getUser());
    }

    private Role generateRoleA(){

        Set<Permission> granted = Set.of(this.permissionA);
        Set<Permission> denied = Set.of(this.permissionB);

        return new Role(1L, 100, granted, denied);
    }

    private Role generateRoleB(){
        Set<Permission> granted = Set.of(this.permissionB);
        Set<Permission> denied = Set.of(this.permissionA);

        return new Role(1L, 50, granted, denied);
    }

}
