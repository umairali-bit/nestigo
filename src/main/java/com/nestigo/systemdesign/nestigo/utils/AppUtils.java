package com.nestigo.systemdesign.nestigo.utils;

import com.nestigo.systemdesign.nestigo.entities.UserEntity;
import org.springframework.security.core.context.SecurityContextHolder;

public class AppUtils {

    public static UserEntity getCurrentUser() {

        return (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
