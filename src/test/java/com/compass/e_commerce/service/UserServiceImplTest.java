package com.compass.e_commerce.service;

import com.compass.e_commerce.dto.user.AddressDataDto;
import com.compass.e_commerce.dto.user.UserUpdateDto;
import com.compass.e_commerce.exception.personalized.DeletionNotAllowedException;
import com.compass.e_commerce.exception.personalized.UserInactiveException;
import com.compass.e_commerce.model.*;
import com.compass.e_commerce.model.enums.GenderEnum;
import com.compass.e_commerce.model.enums.PlatformEnum;
import com.compass.e_commerce.model.enums.RoleNameEnum;
import com.compass.e_commerce.model.pk.CartGameItemPK;
import com.compass.e_commerce.repository.RoleRepository;
import com.compass.e_commerce.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationServiceImpl authenticationServices;

    User commonUser;
    User adminUser;
    User inactiveUser;

    @BeforeEach
    public void setUp() {
        Set<Role> setRole1 = new HashSet<>();
        Set<Role> setRole2 = new HashSet<>();
        Set<Order> setOrder = new HashSet<>();

        Role roleUser = new Role(1L, RoleNameEnum.USER, null);
        Role roleAdmin = new Role(2L, RoleNameEnum.ADMIN, null);


        setRole1.add(roleUser);
        setRole2.add(roleAdmin);
        setRole2.add(roleUser);

        commonUser = new User(1L, "Diogo", "123456789a", "diogodev@gmail.com", "67676666777",
                "(00) 90000-2900", new Address(), null, setOrder, setRole1, true);


        adminUser = new User(2L, "Daniela", "123456789b", "daniela@gmail.com", "66677666777",
                "(00) 90220-2900", new Address(), null, setOrder, setRole2, true);

        inactiveUser = new User(3L, "Pedro", "123456789c", "pedro@gmail.com", "66677776777",
                "(00) 92220-2920", new Address(), null, setOrder, setRole1, false);

    }

    //TESTES PARA registerUser
    @Test
    void checkIfTheUserIsSuccessfullyRegistered() {
        Mockito.when(passwordEncoder.encode("123456789a")).thenReturn("$2a$12$ENzos8xA1j1lQ..XFfQQIuIgVn1MWL2.SyEEXkskyZRJIwsiFOAfS");
        Mockito.when(roleRepository.findByName(RoleNameEnum.USER)).thenReturn(Optional.of(new Role(2L, RoleNameEnum.USER, Collections.singleton(commonUser))));
        Mockito.when(userRepository.save(commonUser)).thenReturn(commonUser);

        User userSalvo = userService.registerUser(commonUser);

        Mockito.verify(userRepository, times(1)).save(Mockito.any(User.class));
        Mockito.verify(roleRepository, times(1)).findByName(Mockito.any(RoleNameEnum.class));

        assertEquals("Diogo", userSalvo.getUsername());
        assertNotEquals("123456789a", userSalvo.getPassword());
    }

    @Test
    void checksIfEntityNotFoundExceptionIsThrownInRegisterUser() {
        Mockito.when(roleRepository.findByName(RoleNameEnum.USER)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.registerUser(commonUser));
    }

    //TESTES PARA registerAdmin
    @Test
    void checkIfTheAdminIsSuccessfullyRegistered() {
        Mockito.when(passwordEncoder.encode("123456789a")).thenReturn("$2a$12$ENzos8xA1j1lQ..XFfQQIuIgVn1MWL2.SyEEXkskyZRJIwsiFOAfS");
        Mockito.when(roleRepository.findAll()).thenReturn(Collections.singletonList(new Role(2L, RoleNameEnum.USER, Collections.singleton(commonUser))));
        Mockito.when(userRepository.save(commonUser)).thenReturn(commonUser);

        User userSalvo = userService.registerUserAdmin(commonUser);

        Mockito.verify(userRepository, times(1)).save(Mockito.any(User.class));
        Mockito.verify(roleRepository, times(1)).findAll();

        assertEquals("Diogo", userSalvo.getUsername());
        assertNotEquals("123456789a", userSalvo.getPassword());
    }

    @Test
    void checksIfEntityNotFoundExceptionIsThrownInRegisterAdmin() {
        Mockito.when(roleRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(EntityNotFoundException.class, () -> userService.registerUserAdmin(commonUser));
    }

    //TESTES PARA getById()
    @Test
    void shouldReturnUserWhenIdExists() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(commonUser));

        User result = userService.getById(1L);

        Mockito.verify(userRepository, times(1)).findById(1L);

        assertNotNull(result);
        assertEquals("Diogo", result.getUsername());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenUserDoesNotExist() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.getById(1L)
        );

        Mockito.verify(userRepository, times(1)).findById(1L);

        assertEquals("User não encontrado id: 1", exception.getMessage());
    }

    //TESTE PARA getAll()
    @Test
    void shouldReturnListOfUsersWhenUsersAreActive() {
        List<User> users = List.of(commonUser, commonUser);
        Mockito.when(userRepository.findByActiveTrue()).thenReturn(users);

        List<User> result = userService.getAll();

        Mockito.verify(userRepository, times(1)).findByActiveTrue();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    //TESTES PARA findByEmail(), findByUsername(), findByCpf()
    @ParameterizedTest
    @CsvSource({
            "findByEmail, diogodev@gmail.com, User não encontrado username: diogodev@gmail.com",
            "findByUsername, Diogo, Usuario ou senha invalidos ou incorretos",
            "findByCpf, 67676666777, Usuario não encontrado cpf : 67676666777"
    })
    void shouldThrowExceptionWhenUserNotFound(String method, String parameter, String expectedMessage) {
        Exception exception = assertThrows(
                Exception.class,
                () -> {
                    switch (method) {
                        case "findByEmail":
                            Mockito.when(userRepository.findByEmail(parameter)).thenReturn(Optional.empty());
                            userService.findByEmail(parameter);
                            break;
                        case "findByUsername":
                            Mockito.when(userRepository.findByUsername(parameter)).thenReturn(Optional.empty());
                            userService.findByUsername(parameter);
                            break;
                        case "findByCpf":
                            Mockito.when(userRepository.findByCpf(parameter)).thenReturn(Optional.empty());
                            userService.findByCpf(parameter);
                            break;
                        default:
                            throw new IllegalArgumentException("Invalid method");
                    }
                }
        );
        assertEquals(expectedMessage, exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
            "findByEmail, diogodev@gmail.com",
            "findByUsername, Diogo",
            "findByCpf, 67676666777"
    })
    void shouldReturnUserWhenDataExists(String method, String parameter) {
        User result = null;

        switch (method) {
            case "findByEmail":
                Mockito.when(userRepository.findByEmail(parameter)).thenReturn(Optional.of(commonUser));
                result = userService.findByEmail(parameter);
                Mockito.verify(userRepository).findByEmail(parameter);
                break;
            case "findByUsername":
                Mockito.when(userRepository.findByUsername(parameter)).thenReturn(Optional.of(commonUser));
                result = userService.findByUsername(parameter);
                Mockito.verify(userRepository).findByUsername(parameter);
                break;
            case "findByCpf":
                Mockito.when(userRepository.findByCpf(parameter)).thenReturn(Optional.of(commonUser));
                result = userService.findByCpf(parameter);
                Mockito.verify(userRepository).findByCpf(parameter);
                break;
            default:
                throw new IllegalArgumentException("Invalid method");
        }
        assertNotNull(result);
        assertEquals(commonUser, result);
    }

    //TESTE PARA changePassword()
    @Test
    void ChangeUserPasswordSuccessfully() {
        Mockito.when(passwordEncoder.encode("123456789aB")).thenReturn("$2a$12$amenETO.QdEeJEkxMmG.meWVTnzlIc7HS4aGXrO5piMwbcEcD0lJ.");
        Mockito.when(userRepository.save(commonUser)).thenReturn(commonUser);

        User userUpdated = userService.changePassword(commonUser, "123456789aB");

        assertEquals("$2a$12$amenETO.QdEeJEkxMmG.meWVTnzlIc7HS4aGXrO5piMwbcEcD0lJ.", userUpdated.getPassword());
    }

    //TESTES PARA update()
    @Test
    void userAdminUpdatesHimselfSuccessfully() {
        var userUpdateDto = new UserUpdateDto("UserAtualizado", "useratualizado@gmail.com", "1234567910", null,
                new AddressDataDto(null, null, null, null, null, null));
        Mockito.when(authenticationServices.getAuthenticatedUserId()).thenReturn(2L);
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));
        Mockito.when(userRepository.save(adminUser)).thenReturn(adminUser);

        User userUpdated = userService.update(null, userUpdateDto);

        Mockito.verify(userRepository, times(1)).save(Mockito.any(User.class));
        Mockito.verify(authenticationServices, times(1)).getAuthenticatedUserId();

        assertEquals("UserAtualizado", userUpdated.getUsername());
    }

    @Test
    void adminUpdatesAnotherUserSuccessfully() {
        var userUpdateDto = new UserUpdateDto("UserAtualizado", "useratualizado@gmail.com", "1234567910", null,
                new AddressDataDto(null, null, null, null, null, null));
        Mockito.when(authenticationServices.getAuthenticatedUserId()).thenReturn(2L);
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(commonUser));
        Mockito.when(userRepository.save(commonUser)).thenReturn(commonUser);

        User userUpdated = userService.update(1L, userUpdateDto);

        Mockito.verify(userRepository, times(1)).save(Mockito.any(User.class));
        Mockito.verify(authenticationServices, times(1)).getAuthenticatedUserId();

        assertEquals("UserAtualizado", userUpdated.getUsername());
    }

    @Test
    void throwsExceptionIfUserIdIsInvalidWhenAdminTriesToUpdate() {
        var userUpdateDto = new UserUpdateDto("UserAtualizado", "useratualizado@gmail.com", "1234567910", null,
                new AddressDataDto(null, null, null, null, null, null));

        assertThrows(EntityNotFoundException.class, () -> userService.update(100L, userUpdateDto));
    }

    @Test
    void throwsExceptionIfUserIsTryingToUpdateAnotherUserWithoutBeingAdmin() {
        var userUpdateDto = new UserUpdateDto("UserAtualizado", "useratualizado@gmail.com", "1234567910", null,
                new AddressDataDto(null, null, null, null, null, null));
        Mockito.when(authenticationServices.getAuthenticatedUserId()).thenReturn(1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(commonUser));
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.update(2L, userUpdateDto)
        );

        Mockito.verify(userRepository, times(2)).findById(Mockito.any());
        Mockito.verify(authenticationServices, times(1)).getAuthenticatedUserId();

        assertEquals("ID não corresponde a um usuário válido ou permissão insuficiente.", exception.getMessage());
    }

    @Test
    void throwsExceptionIfUserToBeUpdatedIsInactive() {
        var userUpdateDto = new UserUpdateDto("UserAtualizado", "useratualizado@gmail.com", "1234567910", null,
                new AddressDataDto(null, null, null, null, null, null));
        Mockito.when(authenticationServices.getAuthenticatedUserId()).thenReturn(2L);
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));
        Mockito.when(userRepository.findById(3L)).thenReturn(Optional.of(inactiveUser));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.update(3L, userUpdateDto)
        );

        Mockito.verify(userRepository, times(2)).findById(Mockito.any());
        Mockito.verify(authenticationServices, times(1)).getAuthenticatedUserId();

        assertEquals("ID não corresponde a um usuário válido ou permissão insuficiente.", exception.getMessage());
    }

    @Test
    void throwsExceptionIfUserWithoutPermissionTriesToUpdateInUpdateMethod() {
        Address address = new Address();
        var userUpdateDto = new UserUpdateDto("UserAtualizado", "useratualizado@gmail.com", "1234567910", null,
                new AddressDataDto(null, null, null, null, null, null));
        Mockito.when(authenticationServices.getAuthenticatedUserId()).thenReturn(1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(commonUser));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.update(null, userUpdateDto)
        );

        Mockito.verify(userRepository, times(1)).findById(Mockito.any());
        Mockito.verify(authenticationServices, times(1)).getAuthenticatedUserId();

        assertEquals("Usuário não tem permissão para atualizar ou está inativo.", exception.getMessage());
    }

    @Test
    void throwsExceptionIfInactiveUserTriesToUpdate() {
        var userUpdateDto = new UserUpdateDto("UserAtualizado", "useratualizado@gmail.com", "1234567910", null,
                new AddressDataDto(null, null, null, null, null, null));
        Mockito.when(authenticationServices.getAuthenticatedUserId()).thenReturn(3L);
        Mockito.when(userRepository.findById(3L)).thenReturn(Optional.of(inactiveUser));

        assertThrows(EntityNotFoundException.class, () -> userService.update(1L, userUpdateDto));
    }

    @Test
    void throwsExceptionIfStringFieldIsEmpty() {
        var userUpdateDto = new UserUpdateDto("", "useratualizado@gmail.com", "1234567910", null,
                new AddressDataDto(null, null, null, null, null, null));
        Mockito.when(authenticationServices.getAuthenticatedUserId()).thenReturn(2L);
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));

        assertThrows(IllegalArgumentException.class, () -> userService.update(null, userUpdateDto));
    }

    @Test
    void throwsExceptionIfEmailIsInvalid() {
        var userUpdateDto = new UserUpdateDto("UserAtualizado", "useratualizado@gttil.com", "1234567910", null,
                new AddressDataDto(null, null, null, null, null, null));
        Mockito.when(authenticationServices.getAuthenticatedUserId()).thenReturn(2L);
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));

        assertThrows(IllegalArgumentException.class, () -> userService.update(null, userUpdateDto));
    }

    @Test
    void throwsExceptionIfPasswordIsInvalid() {
        var userUpdateDto = new UserUpdateDto("UserAtualizado", "useratualizado@gmail.com", "12345", null,
                new AddressDataDto(null, null, null, null, null, null));
        Mockito.when(authenticationServices.getAuthenticatedUserId()).thenReturn(2L);
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));

        assertThrows(IllegalArgumentException.class, () -> userService.update(null, userUpdateDto));
    }

    @Test
    void throwsExceptionIfPhoneIsInvalid() {
        var userUpdateDto = new UserUpdateDto("UserAtualizado", "useratualizado@gmail.com", "1234567910", "(71) 92333-6300",
                new AddressDataDto(null, null, null, null, null, null));
        Mockito.when(authenticationServices.getAuthenticatedUserId()).thenReturn(2L);
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));

        assertThrows(IllegalArgumentException.class, () -> userService.update(null, userUpdateDto));
    }

    @Test
    void throwsExceptionIfPostalCodeIsInvalid() {
        var userUpdateDto = new UserUpdateDto("UserAtualizado", "useratualizado@gmail.com", "1234567910", null,
                new AddressDataDto(null, null, null, null, null, "4333-200"));
        Mockito.when(authenticationServices.getAuthenticatedUserId()).thenReturn(2L);
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));

        assertThrows(IllegalArgumentException.class, () -> userService.update(null, userUpdateDto));
    }

    @Test
    void throwsExceptionIfNoAuthenticatedUser() {
        var userUpdateDto = new UserUpdateDto("UserAtualizado", "useratualizado@gmail.com", "1234567910", null,
                new AddressDataDto(null, null, null, null, null, "4333200"));
        Mockito.when(authenticationServices.getAuthenticatedUserId()).thenReturn(100L);
        Mockito.when(userRepository.findById(100L)).thenReturn(null);

        assertThrows(NullPointerException.class, () -> userService.updateMyProfile(userUpdateDto));
    }

    @Test
    void shouldDeleteUserSuccessfully() {
        List<CartGameItem> cartGameItems = new ArrayList<>();
        Game game = new Game(1L, "GameTeste", "Descrição teste", GenderEnum.ACTION, null, PlatformEnum.MOBILE,
                1.0, cartGameItems, null, true);
        Cart cart = new Cart(1L, 2, 2.0, cartGameItems, commonUser);
        CartGameItem cartGameItem = new CartGameItem(new CartGameItemPK(cart, game), 2);

        cart.getCartGameItem().add(cartGameItem);
        game.getCartGameItem().add(cartGameItem);

        commonUser.setCart(cart);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(commonUser));

        Mockito.doNothing().when(userRepository).deleteById(1L);

        userService.delete(1L);

        Mockito.verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenUserIsInactive() {
        Mockito.when(userRepository.findById(3L)).thenReturn(Optional.of(inactiveUser));
        assertThrows(UserInactiveException.class, () -> userService.delete(3L));
    }

    @Test
    void shouldThrowExceptionWhenUserHasOrders() {
        Set<Order> orders = new HashSet<>();
        Order order = new Order();
        orders.add(order);
        commonUser.setOrders(orders);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(commonUser));

        assertThrows(DeletionNotAllowedException.class, () -> userService.delete(1L));
    }
}