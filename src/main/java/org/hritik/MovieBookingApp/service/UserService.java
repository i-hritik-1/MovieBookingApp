package org.hritik.MovieBookingApp.service;

import org.hritik.MovieBookingApp.dto.UserDto;
import org.hritik.MovieBookingApp.exception.ResourceNotFoundException;
import org.hritik.MovieBookingApp.model.User;
import org.hritik.MovieBookingApp.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    public UserDto createUser(UserDto userDto)
    {
        User user = mapToEntity(userDto);
        User savedUser = userRepo.save(user);

        return mapToDto(savedUser);
    }


    //Update User

    public UserDto updateUser(UserDto userDto, Long id)
    {
        User user = userRepo.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("User not found with id : "+id));

        User updatedUser = userRepo.save(mapToEntity(userDto));


        return mapToDto(updatedUser);

    }


    //Delete User

    public void deleteUser(Long id)
    {
        User user = userRepo.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("User not found with id : "+id));

        userRepo.delete(user);

    }




    public UserDto getUserById(Long id)
    {
        User user = userRepo.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("User not found with id : "+id));

        return mapToDto(user);
    }

    public List<UserDto> getAllUsers()
    {
        List<User> users = userRepo.findAll();

        return users.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }




    private UserDto mapToDto(User user)
    {
        UserDto userDto = new UserDto();

        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setPhoneNUmber(userDto.getPhoneNUmber());

        return userDto;

    }

    private User mapToEntity(UserDto userDto) {


        User user = new User();

        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPhone_number(userDto.getPhoneNUmber());

        return user;

    }

}

