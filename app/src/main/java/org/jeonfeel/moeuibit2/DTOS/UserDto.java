package org.jeonfeel.moeuibit2.DTOS;

public class UserDto {

    public long userId;
    public String userEmail;

    public UserDto(long userId, String userEmail) {
        this.userId = userId;
        this.userEmail = userEmail;
    }
}