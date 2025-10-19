package ru.demo.service;

import ru.demo.Exception.UserUsernameExistException;
import ru.demo.repository.RoleRepository;
import ru.demo.repository.UserRepository;
import ru.demo.model.Role;
import ru.demo.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.PostConstruct;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Set;

@Transactional
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User passwordCoder(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return user;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User getById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));
    }

    @Override
    public void save(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new UserUsernameExistException("Username already exists");
        }
        userRepository.save(passwordCoder(user));
    }

    @Override
    public void update(User user) {
//        userRepository.save(user);

        User existingUser = getById(user.getUserId());

        if (!passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        existingUser.setName(user.getName());
        existingUser.setSurname(user.getSurname());
        existingUser.setAge(user.getAge());
        existingUser.setEmail(user.getEmail());
        existingUser.setRoles(user.getRoles());
        userRepository.save(existingUser);

    }

    @Override
    public void deleteById(long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @PostConstruct
    public void addDefaultUser() {
        if (userRepository.count() > 0) return; // проверка на существующих пользователей

        Role userRole = roleRepository.findById(1L).orElseThrow(() -> new RuntimeException("ROLE_USER not found"));
        Role adminRole = roleRepository.findById(2L).orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found"));

        User user = new User("Ivan", "Ivanov", (byte)45, "user@mail.com", "user", "12345", Set.of(userRole));
        User admin = new User("Nasty", "Killina", (byte)36, "admin@mail.com", "admin", "admin", Set.of(userRole, adminRole));

        save(user);
        save(admin);
        }
}

