package com.example.artisanalfood.service;

import com.example.artisanalfood.model.User;
import com.example.artisanalfood.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User saveUser(User user) {
        if (user.getRole() == User.Role.PRODUCER && user.getRating() == 0.0) {
            user.setRating(5.0); // Seed with 5.0 rating for new producers
        }
        return userRepository.save(user);
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getProducers() {
        return userRepository.findByRole(User.Role.PRODUCER);
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
}
