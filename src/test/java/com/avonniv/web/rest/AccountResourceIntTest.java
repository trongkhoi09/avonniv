package com.avonniv.web.rest;

import com.avonniv.AvonnivApp;
import com.avonniv.domain.Authority;
import com.avonniv.domain.User;
import com.avonniv.repository.AuthorityRepository;
import com.avonniv.repository.UserRepository;
import com.avonniv.security.AuthoritiesConstants;
import com.avonniv.service.MailService;
import com.avonniv.service.UserService;
import com.avonniv.service.dto.UserDTO;
import com.avonniv.web.rest.vm.KeyAndPasswordVM;
import com.avonniv.web.rest.vm.ManagedUserVM;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the AccountResource REST controller.
 *
 * @see AccountResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AvonnivApp.class)
public class AccountResourceIntTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private HttpMessageConverter[] httpMessageConverters;

    @Mock
    private UserService mockUserService;

    @Mock
    private MailService mockMailService;

    private MockMvc restUserMockMvc;

    private MockMvc restMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        doNothing().when(mockMailService).sendActivationEmail(anyObject());

        AccountResource accountResource =
            new AccountResource(userRepository, userService, mockMailService);

        AccountResource accountUserMockResource =
            new AccountResource(userRepository, mockUserService, mockMailService);

        this.restMvc = MockMvcBuilders.standaloneSetup(accountResource)
            .setMessageConverters(httpMessageConverters)
            .build();
        this.restUserMockMvc = MockMvcBuilders.standaloneSetup(accountUserMockResource).build();
    }

    @Test
    public void testNonAuthenticatedUser() throws Exception {
        restUserMockMvc.perform(get("/api/authenticate")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(""));
    }

    @Test
    public void testAuthenticatedUser() throws Exception {
        restUserMockMvc.perform(get("/api/authenticate")
            .with(request -> {
                request.setRemoteUser("test");
                return request;
            })
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string("test"));
    }

    @Test
    public void testGetExistingAccount() throws Exception {
        Set<Authority> authorities = new HashSet<>();
        Authority authority = new Authority();
        authority.setName(AuthoritiesConstants.ADMIN);
        authorities.add(authority);

        User user = new User();
        user.setFirstName("john");
        user.setLastName("doe");
        user.setLogin("john.doe@jhipster.com");
        user.setImageUrl("http://placehold.it/50x50");
        user.setLangKey("en");
        user.setAuthorities(authorities);
        when(mockUserService.getUserWithAuthorities()).thenReturn(user);

        restUserMockMvc.perform(get("/api/account")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.firstName").value("john"))
            .andExpect(jsonPath("$.lastName").value("doe"))
            .andExpect(jsonPath("$.login").value("john.doe@jhipster.com"))
            .andExpect(jsonPath("$.imageUrl").value("http://placehold.it/50x50"))
            .andExpect(jsonPath("$.langKey").value("en"))
            .andExpect(jsonPath("$.authorities").value(AuthoritiesConstants.ADMIN));
    }

    @Test
    public void testGetUnknownAccount() throws Exception {
        when(mockUserService.getUserWithAuthorities()).thenReturn(null);

        restUserMockMvc.perform(get("/api/account")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError());
    }

    @Test
    @Transactional
    public void testRegisterValid() throws Exception {
        ManagedUserVM validUser = new ManagedUserVM(
            null,                   // id
            "joe@example.com",                  // login
            "password",             // password
            "Joe",                  // firstName
            "Shmoe",                // lastName
            true,                   // activated
            "http://placehold.it/50x50", //imageUrl
            "en",                   // langKey
            null,                   // createdBy
            null,                   // createdDate
            null,                   // lastModifiedBy
            null,                   // lastModifiedDate
            new HashSet<>(Collections.singletonList(AuthoritiesConstants.USER)));

        restMvc.perform(
            post("/api/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(validUser)))
            .andExpect(status().isCreated());

        Optional<User> user = userRepository.findOneByLogin("joe@example.com");
        assertThat(user.isPresent()).isTrue();
    }

    @Test
    @Transactional
    public void testRegisterInvalidLogin() throws Exception {
        ManagedUserVM invalidUser = new ManagedUserVM(
            null,                   // id
            "funky@example.com",          // login <-- invalid
            "password",             // password
            "Funky",                // firstName
            "One",                  // lastName
            true,                   // activated
            "http://placehold.it/50x50", //imageUrl
            "en",                   // langKey
            null,                   // createdBy
            null,                   // createdDate
            null,                   // lastModifiedBy
            null,                   // lastModifiedDate
            new HashSet<>(Collections.singletonList(AuthoritiesConstants.USER)));

        restUserMockMvc.perform(
            post("/api/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(invalidUser)))
            .andExpect(status().isBadRequest());

        Optional<User> user = userRepository.findOneByLogin("funky@example.com");
        assertThat(user.isPresent()).isFalse();
    }

//    @Test
//    @Transactional
//    public void testRegisterInvalidEmail() throws Exception {
//        ManagedUserVM invalidUser = new ManagedUserVM(
//            null,               // id
//            "invalid",              // login
//            "password",         // password
//            "Bob",              // firstName
//            "Green",            // lastName
//            true,               // activated
//            "http://placehold.it/50x50", //imageUrl
//            "en",                   // langKey
//            null,                   // createdBy
//            null,                   // createdDate
//            null,                   // lastModifiedBy
//            null,                   // lastModifiedDate
//            new HashSet<>(Collections.singletonList(AuthoritiesConstants.USER)));
//
//        restUserMockMvc.perform(
//            post("/api/register")
//                .contentType(TestUtil.APPLICATION_JSON_UTF8)
//                .content(TestUtil.convertObjectToJsonBytes(invalidUser)))
//            .andExpect(status().isBadRequest());
//
//        Optional<User> user = userRepository.findOneByLogin("invalid");
//        assertThat(user.isPresent()).isFalse();
//    }

    @Test
    @Transactional
    public void testRegisterInvalidPassword() throws Exception {
        ManagedUserVM invalidUser = new ManagedUserVM(
            null,               // id
            "bob@example.com",              // login
            "123",              // password with only 3 digits
            "Bob",              // firstName
            "Green",            // lastName
            true,               // activated
            "http://placehold.it/50x50", //imageUrl
            "en",                   // langKey
            null,                   // createdBy
            null,                   // createdDate
            null,                   // lastModifiedBy
            null,                   // lastModifiedDate
            new HashSet<>(Collections.singletonList(AuthoritiesConstants.USER)));

        restUserMockMvc.perform(
            post("/api/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(invalidUser)))
            .andExpect(status().isBadRequest());

        Optional<User> user = userRepository.findOneByLogin("bob@example.com");
        assertThat(user.isPresent()).isFalse();
    }

    @Test
    @Transactional
    public void testRegisterDuplicateLogin() throws Exception {
        // Good
        ManagedUserVM validUser = new ManagedUserVM(
            null,                   // id
            "alice@example.com",    // login
            "password",             // password
            "Alice",                // firstName
            "Something",            // lastName
            true,                   // activated
            "http://placehold.it/50x50", //imageUrl
            "en",                   // langKey
            null,                   // createdBy
            null,                   // createdDate
            null,                   // lastModifiedBy
            null,                   // lastModifiedDate
            new HashSet<>(Collections.singletonList(AuthoritiesConstants.USER)));

        // Duplicate login, different email
        ManagedUserVM duplicatedUser = new ManagedUserVM(validUser.getId(), "alicejr@example.com", validUser.getPassword(), validUser.getFirstName(), validUser.getLastName(),
             true, validUser.getImageUrl(), validUser.getLangKey(), validUser.getCreatedBy(), validUser.getCreatedDate(), validUser.getLastModifiedBy(), validUser.getLastModifiedDate(), validUser.getAuthorities());

        // Good user
        restMvc.perform(
            post("/api/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(validUser)))
            .andExpect(status().isCreated());

        // Duplicate login
        restMvc.perform(
            post("/api/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(duplicatedUser)))
            .andExpect(status().is4xxClientError());

        Optional<User> userDup = userRepository.findOneByLogin("alicejr@example.com");
        assertThat(userDup.isPresent()).isFalse();
    }

//    @Test
//    @Transactional
//    public void testRegisterDuplicateEmail() throws Exception {
//        // Good
//        ManagedUserVM validUser = new ManagedUserVM(
//            null,                   // id
//            "john",                 // login
//            "password",             // password
//            "John",                 // firstName
//            "Doe",                  // lastName
//            "john@example.com",     // email
//            true,                   // activated
//            "http://placehold.it/50x50", //imageUrl
//            "en",                   // langKey
//            null,                   // createdBy
//            null,                   // createdDate
//            null,                   // lastModifiedBy
//            null,                   // lastModifiedDate
//            new HashSet<>(Collections.singletonList(AuthoritiesConstants.USER)));
//
//        // Duplicate email, different login
//        ManagedUserVM duplicatedUser = new ManagedUserVM(validUser.getId(), "johnjr", validUser.getPassword(), validUser.getLogin(), validUser.getLastName(),
//            validUser.getEmail(), true, validUser.getImageUrl(), validUser.getLangKey(), validUser.getCreatedBy(), validUser.getCreatedDate(), validUser.getLastModifiedBy(), validUser.getLastModifiedDate(), validUser.getAuthorities());
//
//        // Good user
//        restMvc.perform(
//            post("/api/register")
//                .contentType(TestUtil.APPLICATION_JSON_UTF8)
//                .content(TestUtil.convertObjectToJsonBytes(validUser)))
//            .andExpect(status().isCreated());
//
//        // Duplicate email
//        restMvc.perform(
//            post("/api/register")
//                .contentType(TestUtil.APPLICATION_JSON_UTF8)
//                .content(TestUtil.convertObjectToJsonBytes(duplicatedUser)))
//            .andExpect(status().is4xxClientError());
//
//        Optional<User> userDup = userRepository.findOneByLogin("johnjr");
//        assertThat(userDup.isPresent()).isFalse();
//    }

    @Test
    @Transactional
    public void testRegisterAdminIsIgnored() throws Exception {
        ManagedUserVM validUser = new ManagedUserVM(
            null,                   // id
            "badguy@example.com",               // login
            "password",             // password
            "Bad",                  // firstName
            "Guy",                  // lastName
            true,                   // activated
            "http://placehold.it/50x50", //imageUrl
            "en",                   // langKey
            null,                   // createdBy
            null,                   // createdDate
            null,                   // lastModifiedBy
            null,                   // lastModifiedDate
            new HashSet<>(Collections.singletonList(AuthoritiesConstants.ADMIN)));

        restMvc.perform(
            post("/api/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(validUser)))
            .andExpect(status().isCreated());

        Optional<User> userDup = userRepository.findOneByLogin("badguy@example.com");
        assertThat(userDup.isPresent()).isTrue();
        assertThat(userDup.get().getAuthorities()).hasSize(1)
            .containsExactly(authorityRepository.findOne(AuthoritiesConstants.USER));
    }

    @Test
    @Transactional
    public void testActivateAccount() throws Exception {
        final String activationKey = "some activation key";
        User user = new User();
        user.setLogin("activate_account@example.com");
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(false);
        user.setActivationKey(activationKey);

        userRepository.saveAndFlush(user);

        restMvc.perform(get("/api/activate?key={activationKey}", activationKey))
            .andExpect(status().isOk());

        user = userRepository.findOneByLogin(user.getLogin()).orElse(null);
        assertThat(user.getActivated()).isTrue();
    }

    @Test
    @Transactional
    public void testActivateAccountWithWrongKey() throws Exception {
        restMvc.perform(get("/api/activate?key=wrongActivationKey"))
            .andExpect(status().isInternalServerError());
    }

    @Test
    @Transactional
    @WithMockUser("save_account")
    public void testSaveAccount() throws Exception {
        User user = new User();
        user.setLogin("save_account@example.com");
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(true);

        userRepository.saveAndFlush(user);

        UserDTO userDTO = new UserDTO(
            null,                   // id
            "save_account@example.com",          // login
            "firstname",                // firstName
            "lastname",                  // lastName
            false,                   // activated
            "http://placehold.it/50x50", //imageUrl
            "en",                   // langKey
            null,                   // createdBy
            null,                   // createdDate
            null,                   // lastModifiedBy
            null,                   // lastModifiedDate
            new HashSet<>(Collections.singletonList(AuthoritiesConstants.ADMIN))
        );

        restMvc.perform(
            post("/api/account")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(userDTO)))
            .andExpect(status().isOk());

        User updatedUser = userRepository.findOneByLogin(user.getLogin()).orElse(null);
        assertThat(updatedUser.getFirstName()).isEqualTo(userDTO.getFirstName());
        assertThat(updatedUser.getLastName()).isEqualTo(userDTO.getLastName());
        assertThat(updatedUser.getLangKey()).isEqualTo(userDTO.getLangKey());
        assertThat(updatedUser.getPassword()).isEqualTo(user.getPassword());
        assertThat(updatedUser.getImageUrl()).isEqualTo(userDTO.getImageUrl());
        assertThat(updatedUser.getActivated()).isEqualTo(true);
        assertThat(updatedUser.getAuthorities()).isEmpty();
    }

//    @Test
//    @Transactional
//    @WithMockUser("save_invalid_email")
//    public void testSaveInvalidEmail() throws Exception {
//        User user = new User();
//        user.setLogin("save_invalid_email@example.com");
//        user.setPassword(RandomStringUtils.random(60));
//        user.setActivated(true);
//
//        userRepository.saveAndFlush(user);
//
//        UserDTO userDTO = new UserDTO(
//            null,                   // id
//            "not_used",          // login
//            "firstname",                // firstName
//            "lastname",                  // lastName
//            "invalid email",    // email
//            false,                   // activated
//            "http://placehold.it/50x50", //imageUrl
//            "en",                   // langKey
//            null,                   // createdBy
//            null,                   // createdDate
//            null,                   // lastModifiedBy
//            null,                   // lastModifiedDate
//            new HashSet<>(Collections.singletonList(AuthoritiesConstants.ADMIN))
//        );
//
//        restMvc.perform(
//            post("/api/account")
//                .contentType(TestUtil.APPLICATION_JSON_UTF8)
//                .content(TestUtil.convertObjectToJsonBytes(userDTO)))
//            .andExpect(status().isBadRequest());
//
//        assertThat(userRepository.findOneByLogin("invalid email")).isNotPresent();
//    }

//    @Test
//    @Transactional
//    @WithMockUser("save_existing_email")
//    public void testSaveExistingEmail() throws Exception {
//        User user = new User();
//        user.setLogin("save_existing_email@example.com");
//        user.setPassword(RandomStringUtils.random(60));
//        user.setActivated(true);
//
//        userRepository.saveAndFlush(user);
//
//        User anotherUser = new User();
//        anotherUser.setLogin("save_existing_email2@example.com");
//        anotherUser.setPassword(RandomStringUtils.random(60));
//        anotherUser.setActivated(true);
//
//        userRepository.saveAndFlush(anotherUser);
//
//        UserDTO userDTO = new UserDTO(
//            null,                   // id
//            "not_used",          // login
//            "firstname",                // firstName
//            "lastname",                  // lastName
//            "save_existing_email2@example.com",    // email
//            false,                   // activated
//            "http://placehold.it/50x50", //imageUrl
//            "en",                   // langKey
//            null,                   // createdBy
//            null,                   // createdDate
//            null,                   // lastModifiedBy
//            null,                   // lastModifiedDate
//            new HashSet<>(Collections.singletonList(AuthoritiesConstants.ADMIN))
//        );
//
//        restMvc.perform(
//            post("/api/account")
//                .contentType(TestUtil.APPLICATION_JSON_UTF8)
//                .content(TestUtil.convertObjectToJsonBytes(userDTO)))
//            .andExpect(status().isBadRequest());
//
//        User updatedUser = userRepository.findOneByLogin("save_existing_email").orElse(null);
//        assertThat(updatedUser.getEmail()).isEqualTo("save_existing_email@example.com");
//    }

//    @Test
//    @Transactional
//    @WithMockUser("save_existing_email_and_login")
//    public void testSaveExistingEmailAndLogin() throws Exception {
//        User user = new User();
//        user.setLogin("save_existing_email_and_login");
//        user.setLogin("save_existing_email_and_login@example.com");
//        user.setPassword(RandomStringUtils.random(60));
//        user.setActivated(true);
//
//        userRepository.saveAndFlush(user);
//
//        User anotherUser = new User();
//        anotherUser.setLogin("save_existing_email_and_login");
//        anotherUser.setEmail("save_existing_email_and_login@example.com");
//        anotherUser.setPassword(RandomStringUtils.random(60));
//        anotherUser.setActivated(true);
//
//        UserDTO userDTO = new UserDTO(
//            null,                   // id
//            "not_used",          // login
//            "firstname",                // firstName
//            "lastname",                  // lastName
//            "save_existing_email_and_login@example.com",    // email
//            false,                   // activated
//            "http://placehold.it/50x50", //imageUrl
//            "en",                   // langKey
//            null,                   // createdBy
//            null,                   // createdDate
//            null,                   // lastModifiedBy
//            null,                   // lastModifiedDate
//            new HashSet<>(Collections.singletonList(AuthoritiesConstants.ADMIN))
//        );
//
//        restMvc.perform(
//            post("/api/account")
//                .contentType(TestUtil.APPLICATION_JSON_UTF8)
//                .content(TestUtil.convertObjectToJsonBytes(userDTO)))
//            .andExpect(status().isOk());
//
//        User updatedUser = userRepository.findOneByLogin("save_existing_email_and_login").orElse(null);
//        assertThat(updatedUser.getEmail()).isEqualTo("save_existing_email_and_login@example.com");
//    }

    @Test
    @Transactional
    @WithMockUser("change_password")
    public void testChangePassword() throws Exception {
        User user = new User();
        user.setPassword(RandomStringUtils.random(60));
        user.setLogin("change_password@example.com");
        userRepository.saveAndFlush(user);

        restMvc.perform(post("/api/account/change_password").content("new password"))
            .andExpect(status().isOk());

        User updatedUser = userRepository.findOneByLogin("change_password").orElse(null);
        assertThat(passwordEncoder.matches("new password", updatedUser.getPassword())).isTrue();
    }

    @Test
    @Transactional
    @WithMockUser("change_password_too_small")
    public void testChangePasswordTooSmall() throws Exception {
        User user = new User();
        user.setPassword(RandomStringUtils.random(60));
        user.setLogin("change_password_too_small@example.com");
        userRepository.saveAndFlush(user);

        restMvc.perform(post("/api/account/change_password").content("new"))
            .andExpect(status().isBadRequest());

        User updatedUser = userRepository.findOneByLogin("change_password_too_small").orElse(null);
        assertThat(updatedUser.getPassword()).isEqualTo(user.getPassword());
    }

    @Test
    @Transactional
    @WithMockUser("change_password_too_long")
    public void testChangePasswordTooLong() throws Exception {
        User user = new User();
        user.setPassword(RandomStringUtils.random(60));
        user.setLogin("change_password_too_long@example.com");
        userRepository.saveAndFlush(user);

        restMvc.perform(post("/api/account/change_password").content(RandomStringUtils.random(101)))
            .andExpect(status().isBadRequest());

        User updatedUser = userRepository.findOneByLogin("change_password_too_long").orElse(null);
        assertThat(updatedUser.getPassword()).isEqualTo(user.getPassword());
    }

    @Test
    @Transactional
    @WithMockUser("change_password_empty")
    public void testChangePasswordEmpty() throws Exception {
        User user = new User();
        user.setPassword(RandomStringUtils.random(60));
        user.setLogin("change_password_empty@example.com");
        userRepository.saveAndFlush(user);

        restMvc.perform(post("/api/account/change_password").content(RandomStringUtils.random(0)))
            .andExpect(status().isBadRequest());

        User updatedUser = userRepository.findOneByLogin("change_password_empty").orElse(null);
        assertThat(updatedUser.getPassword()).isEqualTo(user.getPassword());
    }

    @Test
    @Transactional
    public void testRequestPasswordReset() throws Exception {
        User user = new User();
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(true);
        user.setLogin("password_reset@example.com");
        userRepository.saveAndFlush(user);

        restMvc.perform(post("/api/account/reset_password/init")
            .content("password_reset@example.com"))
            .andExpect(status().isOk());
    }

    @Test
    public void testRequestPasswordResetWrongEmail() throws Exception {
        restMvc.perform(
            post("/api/account/reset_password/init")
                .content("password_reset_wrong_email@example.com"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void testFinishPasswordReset() throws Exception {
        User user = new User();
        user.setPassword(RandomStringUtils.random(60));
        user.setLogin("finish_password_reset@example.com");
        user.setResetDate(Instant.now().plusSeconds(60));
        user.setResetKey("reset key");
        userRepository.saveAndFlush(user);

        KeyAndPasswordVM keyAndPassword = new KeyAndPasswordVM();
        keyAndPassword.setKey(user.getResetKey());
        keyAndPassword.setNewPassword("new password");

        restMvc.perform(
            post("/api/account/reset_password/finish")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(keyAndPassword)))
            .andExpect(status().isOk());

        User updatedUser = userRepository.findOneByLogin(user.getLogin()).orElse(null);
        assertThat(passwordEncoder.matches(keyAndPassword.getNewPassword(), updatedUser.getPassword())).isTrue();
    }

    @Test
    @Transactional
    public void testFinishPasswordResetTooSmall() throws Exception {
        User user = new User();
        user.setPassword(RandomStringUtils.random(60));
        user.setLogin("finish_password_reset_too_small@example.com");
        user.setResetDate(Instant.now().plusSeconds(60));
        user.setResetKey("reset key too small");
        userRepository.saveAndFlush(user);

        KeyAndPasswordVM keyAndPassword = new KeyAndPasswordVM();
        keyAndPassword.setKey(user.getResetKey());
        keyAndPassword.setNewPassword("foo");

        restMvc.perform(
            post("/api/account/reset_password/finish")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(keyAndPassword)))
            .andExpect(status().isBadRequest());

        User updatedUser = userRepository.findOneByLogin(user.getLogin()).orElse(null);
        assertThat(passwordEncoder.matches(keyAndPassword.getNewPassword(), updatedUser.getPassword())).isFalse();
    }


    @Test
    @Transactional
    public void testFinishPasswordResetWrongKey() throws Exception {
        KeyAndPasswordVM keyAndPassword = new KeyAndPasswordVM();
        keyAndPassword.setKey("wrong reset key");
        keyAndPassword.setNewPassword("new password");

        restMvc.perform(
            post("/api/account/reset_password/finish")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(keyAndPassword)))
            .andExpect(status().isInternalServerError());
    }
}
