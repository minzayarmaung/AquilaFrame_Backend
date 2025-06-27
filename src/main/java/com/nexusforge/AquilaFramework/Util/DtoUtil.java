package com.nexusforge.AquilaFramework.Util;

import com.nexusforge.AquilaFramework.Dto.UserDTO;
import com.nexusforge.AquilaFramework.Entity.User;
import org.springframework.stereotype.Component;

@Component
public class DtoUtil {

    public UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setSyskey(user.getSyskey());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setStatus(user.getStatus());
        dto.setCreateddate(user.getCreateddate());
        dto.setN1(user.getN1());
        dto.setN2(user.getN2());
        dto.setN3(user.getN3());
        dto.setN4(user.getN4());
        dto.setN5(user.getN5());
        return dto;
    }
}
