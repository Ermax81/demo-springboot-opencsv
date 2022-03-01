package com.example.demospringbootopencsv;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CreateCsvFileTest {

    @Test
    void createFileWithUsers() {

        List<User> users = new ArrayList<>();
        User user1 = new User(1, "john", "doe", "john.doe@nomail.com");
        User user2 = new User(2, "jane", "doe", "jane.doe@nomail.com");
        users.add(user1);
        users.add(user2);

        File csvFile = OpenCsvTools.exportToFile( users, "tmp.csv", User.class);
        Assertions.assertNotNull(csvFile);

        FileUtils.deleteQuietly(csvFile);
    }

}
