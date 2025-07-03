package com.mall.userservice.dto;

import java.time.LocalDateTime;

public class UserUpdateRequest {
    private String nickname;
    private String phone;
    private String avatar;
    private String gender;
    private LocalDateTime birthday;
    private String address;
    private String bio;

    // Getters and Setters
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public LocalDateTime getBirthday() { return birthday; }
    public void setBirthday(LocalDateTime birthday) { this.birthday = birthday; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
} 