package com.qiguliuxing.dts.db.service;

import com.qiguliuxing.dts.db.domain.DtsAdmin;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

public class DtsAdminServiceTest {

    @Autowired
    private DtsAdminService adminService;

    @Test
    public void add() {
        DtsAdmin admin = new DtsAdmin();
        admin.setUsername("luomu");
        admin.setPassword("yujian520");
        String username = admin.getUsername();

        List<DtsAdmin> adminList = adminService.findAdmin(username);

        String rawPassword = admin.getPassword();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(rawPassword);
        admin.setPassword(encodedPassword);
        adminService.add(admin);
    }

    @Test
    public void add2() {
        DtsAdmin admin = new DtsAdmin();
        admin.setUsername("luomu");
        admin.setPassword("yujian520");

        String rawPassword = admin.getPassword();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(rawPassword);

    }
}