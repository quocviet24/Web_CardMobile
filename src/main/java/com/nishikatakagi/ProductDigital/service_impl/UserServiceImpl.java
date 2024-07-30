package com.nishikatakagi.ProductDigital.service_impl;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.nishikatakagi.ProductDigital.dto.UserRegisterRequestDto;
import com.nishikatakagi.ProductDigital.dto.UserSessionDto;
import com.nishikatakagi.ProductDigital.model.User;
import com.nishikatakagi.ProductDigital.repository.UserRepository;
import com.nishikatakagi.ProductDigital.service.SecurityService;
import com.nishikatakagi.ProductDigital.service.UserService;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    SecurityService security;

    @Override
    public boolean checkEmailExist(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean checkUsernameExist(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public Object changePassword(int id, String newPassword) {
        // Kiểm tra xem người dùng có tồn tại không
        User user = userRepository.findById(id);
        if (user == null) {
            return null; // Người dùng không tồn tại
        } else {

            // Cập nhật mật khẩu mới cho người dùng
            user.setPassword(security.encode(newPassword));
            userRepository.save(user);
        }

        return user;
    }

    @Override
    public User saveUser(User user) {
        return userRepository
                .save(user);
    }

    @Override
    public void updateUser(UserSessionDto userDto, User user) {
        // Update user fields
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPhone(userDto.getPhone());
        user.setUpdatedBy(user.getId());
        // lấy thời gian hiện tại
        Date currentTime = Date.valueOf(LocalDateTime.now().toLocalDate());
        user.setLastUpdated(currentTime);
        // Save updated user to the database
        userRepository.save(user);
    }

    @Override
    public void updateUserEmail(User user, String email) {
        user.setEmail(email);
        userRepository.save(user);
    }

    @Override
    public User findById(int id) {
        return userRepository.findById(id);
    }

    @Override
    public boolean checkPassword(User user, String rawPassword) {
        return false;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public User findUserDBByUserSession(UserSessionDto userSessionDto) {
        String userName = userSessionDto.getUsername();
        User user = userRepository.findUserByUsername(userName);
        if (user != null) {
            return user;
        } else
            return null;

    }

    @Override
    public User findUserDBByUserEmail(String email) {
        User user = userRepository.findByEmail(email);
        return user;
    }

    @Override
    public List<User> findAllUser() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUserByAdmin(String username, UserSessionDto userSessionDto) {
        User user = userRepository.findUserByUsername(username);
        User deletedBy = userRepository.findUserByUsername(userSessionDto.getUsername());
        user.setDeleted(true);
        user.setDeletedDate(Date.valueOf(LocalDateTime.now().toLocalDate()));
        user.setDeletedBy(deletedBy.getId());
        userRepository.save(user);
    }

    @Override
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    @Override
    public void changePassword(User user, String password, User userSessionData) {
        user.setPassword(security.encode(password));
        user.setUpdatedBy(userSessionData.getId());
        Date currentTime = Date.valueOf(LocalDateTime.now().toLocalDate());
        user.setLastUpdated(currentTime);
        userRepository.save(user);
    }

    @Override
    public void saveUser(UserRegisterRequestDto user, User createdBy) {
        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(security.encode(user.getPassword()));
        newUser.setEmail(user.getEmail());
        newUser.setFirstName(user.getFirstname());
        newUser.setLastName(user.getLastname());
        newUser.setPhone(user.getPhone());
        newUser.setRoleId(2);
        newUser.setDeleted(false);
        newUser.setVerified(true);
        newUser.setCreatedBy(createdBy.getId());
        newUser.setCreatedDate(Date.valueOf(LocalDateTime.now().toLocalDate()));
        userRepository.save(newUser);
    }

    private Specification<User> hasRoleId(List<Integer> roleIds) {
        return (root, query, cb) -> root.get("roleId").in(roleIds);
    }

    private Specification<User> hasIsDeleted(List<Boolean> isDeleted) {
        return (root, query, cb) -> root.get("isDeleted").in(isDeleted);
    }

    private Specification<User> hasUsername(String username) {
        return (root, query, cb) -> cb.like(root.get("username"), "%" + username + "%");
    }

    @Override
    public Page<User> filterAccount(List<Integer> roleId, List<Integer> isDeleted, String username, Integer pageNo,
            Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        if (username == null) {
            username = "";
        }
        if (roleId == null) {
            roleId = new ArrayList<>();
            roleId.add(1);
            roleId.add(2);
        }
        if (isDeleted == null) {
            isDeleted = new ArrayList<>();
            isDeleted.add(0);
            isDeleted.add(1);
        }
        List<Boolean> isDeleteBooleans=new ArrayList<>();
        for (Integer integer : isDeleted) {
            if(integer==0){
                isDeleteBooleans.add(false);
            }else{
                isDeleteBooleans.add(true);
            }
        }
        Specification<User> spec = Specification.where(hasRoleId(roleId))
                .and(hasIsDeleted(isDeleteBooleans))
                .and(hasUsername(username));

        return userRepository.findAll(spec, pageable);

    }

    @Override
    public void activateAccount(User user, User userSessionData) {
        user.setDeleted(false);
        user.setUpdatedBy(userSessionData.getId());
        Date currentTime = Date.valueOf(LocalDateTime.now().toLocalDate());
        user.setLastUpdated(currentTime);
        userRepository.save(user);
    }

    @Override
    public Page<User> findAllUser(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return userRepository.findAll(pageable);
    }

}
