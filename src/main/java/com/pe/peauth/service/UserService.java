package com.pe.peauth.service;

import com.pe.peauth.entity.User;

import java.util.List;

public interface UserService {

    User getUserById(Integer id);

    List<User> getAllUser();

}
