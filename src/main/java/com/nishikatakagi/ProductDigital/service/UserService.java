package com.nishikatakagi.ProductDigital.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.nishikatakagi.ProductDigital.dto.UserRegisterRequestDto;
import com.nishikatakagi.ProductDigital.dto.UserSessionDto;
import com.nishikatakagi.ProductDigital.model.User;

public interface UserService {
    boolean checkEmailExist(String email);
    //ngoc
    List<User> findAllUser();
    boolean checkUsernameExist(String username);
    void deleteUserByAdmin(String username, UserSessionDto userSessionDto);
    void changePassword(User user,String password,User userSessionData);
    void saveUser(UserRegisterRequestDto user, User createdBy);
    Page<User> filterAccount(List<Integer> roleId, List<Integer> isDeleted, String username,Integer pageNo,Integer pageSize);
    void activateAccount(User user,User userSessionData);
    Page<User> findAllUser(Integer pageNo, Integer pageSize);
    Object changePassword(int id, String newPassword);

    User saveUser(User user);

    User findById(int id);

    //huy
    boolean checkPassword(User user, String rawPassword);
    User findByUsername(String username);
    void deleteUser(User user);

    //viet
    void updateUser(UserSessionDto userDto, User user);
    void updateUserEmail(User user, String email);
    User findUserDBByUserSession(UserSessionDto userSessionDto);
    User findUserDBByUserEmail(String email);

}
