package com.SaGa.Project.controller;

import com.SaGa.Project.model.Address;
import com.SaGa.Project.model.User;
import com.SaGa.Project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private UserService userService;

    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUserAddress(@PathVariable("userId") String userId, @RequestBody Address address){
        User updateUser = userService.updateUserAddress(userId, address);
        return ResponseEntity.ok(updateUser);
    }

    @GetMapping("/getAddress/{userId}")
    public ResponseEntity<Address> getUserAddress(@PathVariable("userId") String userId){
        Address address = userService.getUserAddress(userId);
        return ResponseEntity.ok(address);
    }
}
