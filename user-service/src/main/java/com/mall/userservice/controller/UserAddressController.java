package com.mall.userservice.controller;

import com.mall.userservice.UserAddress;
import com.mall.userservice.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user/address")
public class UserAddressController {
    @Autowired
    private UserAddressService addressService;

    @GetMapping("/{userId}")
    public List<UserAddress> getUserAddresses(@PathVariable Long userId) {
        return addressService.getAddressesByUserId(userId);
    }

    @GetMapping("/detail/{id}")
    public Optional<UserAddress> getAddress(@PathVariable Long id) {
        return addressService.getAddressById(id);
    }

    @PostMapping
    public UserAddress addOrUpdateAddress(@RequestBody UserAddress address) {
        return addressService.saveAddress(address);
    }

    @DeleteMapping("/{id}")
    public void deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
    }

    @GetMapping("/default/{userId}")
    public UserAddress getDefaultAddress(@PathVariable Long userId) {
        return addressService.getDefaultAddress(userId);
    }

    @PutMapping("/{id}")
    public UserAddress updateAddress(@PathVariable Long id, @RequestBody UserAddress address) {
        address.setId(id);
        return addressService.saveAddress(address);
    }
} 