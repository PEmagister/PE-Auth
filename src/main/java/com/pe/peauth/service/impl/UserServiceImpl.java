package com.pe.peauth.service.impl;

import com.pe.peauth.entity.User;
import com.pe.peauth.repository.UserRepository;
import com.pe.peauth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<User> getAllUser() {
        return userRepository.findAllWithMemberAccess();
    }

    @Override
    public User getUserById(Integer id) {
        Optional<User> user = userRepository.findById(id);

        return user.orElse(null);
    }

}
