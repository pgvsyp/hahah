package com.qiguliuxing.dts.core;

import com.qiguliuxing.dts.core.storage.AliyunStorage;
import com.qiguliuxing.dts.core.util.bcrypt.BCryptPasswordEncoder;
import com.qiguliuxing.dts.db.domain.DtsAdmin;
import com.qiguliuxing.dts.db.service.DtsAdminService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AliyunStorageTest.class)
public class AliyunStorageTest {
    @Autowired
    private AliyunStorage aliyunStorage;

    @Autowired
    private DtsAdminService adminService;



    @Test
    public void test() throws IOException {
        String test = getClass().getClassLoader().getResource("Dts.png").getFile();
        File testFile = new File(test);
        aliyunStorage.store(new FileInputStream(test), testFile.length(), "image/png", "Dts.png");
        Resource resource = aliyunStorage.loadAsResource("Dts.png");
        String url = aliyunStorage.generateUrl("Dts.png");
        System.out.println("test file " + test);
        System.out.println("store file " + resource.getURI());
        System.out.println("generate url " + url);

//        tencentOsService.delete("Dts.png");
    }

    @Test
    public void addAdmin() {
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
    public void addAdmin2() {
        DtsAdmin admin = new DtsAdmin();
        admin.setUsername("luomu");
        admin.setPassword("pzy");
        String username = admin.getUsername();
        List<DtsAdmin> adminList = adminService.findAdmin(username);

        String rawPassword = "yujian520";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(rawPassword);
        admin.setPassword(encodedPassword);
        adminService.add(admin);
    }

}