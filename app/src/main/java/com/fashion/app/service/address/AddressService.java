package com.fashion.app.service.address;

import com.fashion.app.dto.request.AddressRequestDTO;
import com.fashion.app.dto.response.AddressResponseDTO;
import com.fashion.app.dto.response.MessageResponseDTO;

import java.util.List;

public interface AddressService {
    List<AddressResponseDTO> getUserAddresses(Long userId);
    AddressResponseDTO getUserAddress(Long userId, Long addressId);
    AddressResponseDTO createUserAddress(Long userId, AddressRequestDTO dto);
    AddressResponseDTO updateUserAddress(Long userId, Long addressId, AddressRequestDTO dto);
    MessageResponseDTO deleteAddress(Long userId, Long addressId);
    AddressResponseDTO setDefaultAddress(Long userId, Long addressId);
}
