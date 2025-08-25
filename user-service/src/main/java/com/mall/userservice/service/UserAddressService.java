package com.mall.userservice.service;

import com.mall.userservice.UserAddress;
import com.mall.userservice.UserAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserAddressService {
    @Autowired
    private UserAddressRepository addressRepository;

    public List<UserAddress> getAddressesByUserId(Long userId) {
        return addressRepository.findByUserId(userId);
    }

    public Optional<UserAddress> getAddressById(Long id) {
        return addressRepository.findById(id);
    }

    public UserAddress saveAddress(UserAddress address) {
        // 如果设置为默认地址，先取消该用户其他默认
        if (Boolean.TRUE.equals(address.getIsDefault())) {
            List<UserAddress> list = addressRepository.findByUserId(address.getUserId());
            for (UserAddress addr : list) {
                if (Boolean.TRUE.equals(addr.getIsDefault())) {
                    addr.setIsDefault(false);
                    addressRepository.save(addr);
                }
            }
        }
        return addressRepository.save(address);
    }

    public void deleteAddress(Long id) {
        addressRepository.deleteById(id);
    }

    public UserAddress getDefaultAddress(Long userId) {
        return addressRepository.findByUserIdAndIsDefaultTrue(userId);
    }
} 