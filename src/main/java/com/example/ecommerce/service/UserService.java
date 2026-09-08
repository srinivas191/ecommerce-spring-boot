package com.example.ecommerce.service;

import org.springframework.stereotype.Service;

import com.example.ecommerce.dto.AddressDTO.AddressRequestDTO;
import com.example.ecommerce.dto.AddressDTO.AddressResponseDTO;
import com.example.ecommerce.dto.UserDTO.UserRequestDTO;
import com.example.ecommerce.dto.UserDTO.UserResponseDTO;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.model.Address;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.exception.DuplicateResourceException;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponseDTO createUser(UserRequestDTO requestDTO) {

        if (userRepository.existsByEmail(requestDTO.getEmail())) {

            throw new DuplicateResourceException("User already exists with email: " + requestDTO.getEmail());
        }

        return mapToUserResponseDTO(userRepository.save(convertUserRequestDtoToUser(requestDTO)));
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToUserResponseDTO)
                .collect(Collectors.toList());
    }

    public UserResponseDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::mapToUserResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id:" + id));
    }

    public UserResponseDTO updateUser(long id, UserRequestDTO requestDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id:" + id));

        existingUser.setFirstName(requestDTO.getFirstName());
        existingUser.setLastName(requestDTO.getLastName());
        existingUser.setEmail(requestDTO.getEmail());
        existingUser.setAddress(convertAddressRequestDtoToAddress(requestDTO.getAddress()));
        userRepository.save(existingUser);

        return (mapToUserResponseDTO(existingUser));
    }

    public void deleteUser(long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id:" + id));

        userRepository.delete(user);
    }

    public UserResponseDTO mapToUserResponseDTO(User user) {

        UserResponseDTO userResponseDto = new UserResponseDTO();

        userResponseDto.setId(user.getId());
        userResponseDto.setFirstName(user.getFirstName());
        userResponseDto.setLastName(user.getLastName());
        userResponseDto.setEmail(user.getEmail());
        userResponseDto.setAddress(mapToAddressResponseDTO(user.getAddress()));

        return userResponseDto;
    }

    public User convertUserRequestDtoToUser(UserRequestDTO requestDTO) {

        User user = new User();

        user.setFirstName(requestDTO.getFirstName());
        user.setLastName(requestDTO.getLastName());
        user.setEmail(requestDTO.getEmail());
        user.setAddress(convertAddressRequestDtoToAddress(requestDTO.getAddress()));
        return user;
    }

    public Address convertAddressRequestDtoToAddress(AddressRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }
        Address address = new Address();

        address.setBuildingName(requestDTO.getBuildingName());
        address.setCity(requestDTO.getCity());
        address.setState(requestDTO.getState());
        address.setZipCode(requestDTO.getZipCode());

        return address;
    }

    public AddressResponseDTO mapToAddressResponseDTO(Address address) {
        if (address == null) {
            return null;
        }
        AddressResponseDTO addressResponseDTO = new AddressResponseDTO();

        addressResponseDTO.setId(address.getId());
        addressResponseDTO.setBuildingName(address.getBuildingName());
        addressResponseDTO.setCity(address.getCity());
        addressResponseDTO.setState(address.getState());
        addressResponseDTO.setZipCode(address.getZipCode());

        return addressResponseDTO;
    }
}
