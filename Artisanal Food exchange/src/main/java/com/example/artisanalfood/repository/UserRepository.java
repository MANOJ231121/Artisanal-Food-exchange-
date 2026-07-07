package com.example.artisanalfood.repository;

import com.example.artisanalfood.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {
    List<User> findByRole(User.Role role);
}
