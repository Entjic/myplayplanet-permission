import net.myplayplanet.permission.service.PermissionSystemApplication;
import net.myplayplanet.permission.service.dto.PermissionDto;
import net.myplayplanet.permission.service.dto.effective.EffectiveUserModelDto;
import net.myplayplanet.permission.service.dto.effective.ExtensiveEffectiveUserModelDto;
import net.myplayplanet.permission.service.dto.effective.ExtensivePermissionDto;
import net.myplayplanet.permission.service.dto.enums.PermissionOrigin;
import net.myplayplanet.permission.service.dto.enums.PermissionValue;
import net.myplayplanet.permission.service.mapper.EntityMapper;
import net.myplayplanet.permission.service.model.Permission;
import net.myplayplanet.permission.service.model.Role;
import net.myplayplanet.permission.service.model.Scope;
import net.myplayplanet.permission.service.model.User;
import net.myplayplanet.permission.service.repository.RoleRepository;
import net.myplayplanet.permission.service.repository.UserRepository;
import net.myplayplanet.permission.service.service.RoleService;
import net.myplayplanet.permission.service.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = PermissionSystemApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class EffectivePermissionModelTest {


    @Autowired
    private EntityMapper entityMapper;
    private final Scope scope = new Scope(1L, "TestScope");

    private final Permission permissionA = new Permission(UUID.randomUUID(), scope, "A", null, null);
    private final Permission permissionB = new Permission(UUID.randomUUID(), scope, "B", null, null);
    private final Permission permissionC = new Permission(UUID.randomUUID(), scope, "C", null, null);


    private UserService mockUserService() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        RoleRepository roleRepository = Mockito.mock(RoleRepository.class);
        return new UserService(userRepository, entityMapper, new RoleService(entityMapper, roleRepository));
    }

    @Test
    public void collidingRolePermissionsTest() {
        User user = new User(1L, UUID.randomUUID(), scope,
                Set.of(generateRoleA(), generateRoleB()), Set.of(), Set.of());

        EffectiveUserModelDto effectiveUserModel = mockUserService().getEffectiveUserModel(user);

        Assertions.assertEquals(user.getUuid(), effectiveUserModel.getUser());
        Assertions.assertEquals(generateRoleA().getGranted(), entityMapper.permissionDtosToPermissions(
                filterPermissions(effectiveUserModel.getPermissions(), PermissionValue.GRANTED)));

        Assertions.assertEquals(generateRoleA().getDenied(), entityMapper.permissionDtosToPermissions(
                filterPermissions(effectiveUserModel.getPermissions(), PermissionValue.DENIED)));

    }

    @Test
    public void collidingRoleAndUserSpecificPermissionsTest() {
        User user = new User(1L, UUID.randomUUID(), scope,
                Set.of(generateRoleA(), generateRoleB()), Set.of(), Set.of(this.permissionC));

        EffectiveUserModelDto effectiveUserModel = mockUserService().getEffectiveUserModel(user);

        Assertions.assertEquals(user.getUuid(), effectiveUserModel.getUser());
        Assertions.assertEquals(Set.of(this.permissionA), entityMapper.permissionDtosToPermissions(
                filterPermissions(effectiveUserModel.getPermissions(), PermissionValue.GRANTED)));

        Assertions.assertEquals(Set.of(this.permissionB, this.permissionC), entityMapper.permissionDtosToPermissions(
                filterPermissions(effectiveUserModel.getPermissions(), PermissionValue.DENIED)));

    }

    @Test
    public void simpleExtensiveRolePermissionsTest() {
        User user = new User(1L, UUID.randomUUID(), scope,
                Set.of(generateRoleA(), generateRoleB()), Set.of(), Set.of());

        ExtensiveEffectiveUserModelDto model = mockUserService().getExtensiveEffectiveUserModel(user);

        Assertions.assertEquals(Set.of(this.permissionA, this.permissionC), entityMapper.permissionDtosToPermissions(
                filterPermissions(filterExtensivePermissions(model.getPermissionDtos(), PermissionOrigin.ROLE),
                        PermissionValue.GRANTED)));

        Assertions.assertEquals(Set.of(this.permissionB), entityMapper.permissionDtosToPermissions(
                filterPermissions(filterExtensivePermissions(model.getPermissionDtos(), PermissionOrigin.ROLE),
                        PermissionValue.DENIED)));
    }

    @Test
    public void complexExtensiveRolePermissionsTest() {
        User user = new User(1L, UUID.randomUUID(), scope,
                Set.of(generateRoleA(), generateRoleB()), Set.of(), Set.of(this.permissionC));

        ExtensiveEffectiveUserModelDto model = mockUserService().getExtensiveEffectiveUserModel(user);

        Assertions.assertEquals(Set.of(this.permissionA), entityMapper.permissionDtosToPermissions(
                filterPermissions(filterExtensivePermissions(model.getPermissionDtos(), PermissionOrigin.ROLE),
                        PermissionValue.GRANTED)));

        Assertions.assertEquals(Set.of(this.permissionB), entityMapper.permissionDtosToPermissions(
                filterPermissions(filterExtensivePermissions(model.getPermissionDtos(), PermissionOrigin.ROLE),
                        PermissionValue.DENIED)));

        Assertions.assertEquals(Set.of(this.permissionC), entityMapper.permissionDtosToPermissions(
                filterPermissions(filterExtensivePermissions(model.getPermissionDtos(), PermissionOrigin.SPECIFIC),
                        PermissionValue.DENIED)));

    }

    private Set<PermissionDto> filterExtensivePermissions(Set<ExtensivePermissionDto> set, PermissionOrigin filter) {
        Set<PermissionDto> permissionDtos = new HashSet<>();

        for (ExtensivePermissionDto extensivePermissionDto : set) {
            if (extensivePermissionDto.getPermissionOrigin().equals(filter)) {
                permissionDtos.add(extensivePermissionDto.getPermissionDto());
            }
        }
        return permissionDtos;
    }

    private Set<PermissionDto> filterPermissions(Set<PermissionDto> set, PermissionValue filter) {
        Set<PermissionDto> permissionDtos = new HashSet<>();

        for (PermissionDto permissionDto : set) {
            if (permissionDto.getPermissionValue().equals(filter)) {
                permissionDtos.add(permissionDto);
            }
        }
        return permissionDtos;
    }

    private Role generateRoleA() {

        Set<Permission> granted = Set.of(this.permissionA, this.permissionC);
        Set<Permission> denied = Set.of(this.permissionB);

        return new Role(1L, scope, "rolle", 100, granted, denied, false);
    }

    private Role generateRoleB() {
        Set<Permission> granted = Set.of(this.permissionB, this.permissionC);
        Set<Permission> denied = Set.of(this.permissionA);

        return new Role(2L, scope, "andere rolle", 50, granted, denied, false);
    }

}
