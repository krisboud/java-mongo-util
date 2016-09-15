package com.krisboudreau.util;

import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * Tests for the MongoDataUtil class.
 * Created by krisboud on 2016-09-14.
 */
public class MongoDataUtilTest {

    @Test
    public void testMongoInsertAndRetrieve()
    {
        String firstName = "Johnaa";
        TestUser user = new TestUser(firstName, "Doe");
        MongoDataUtil mongoUtil = new MongoDataUtil("myapp");

        try {
            mongoUtil.insertObjectInMongo(user);

            HashMap<String, String> map = new HashMap<String, String>();
            map.put("firstName", firstName);

            TestUser test = (TestUser) mongoUtil.getObjectFromCollection(TestUser.class, map);
            assertEquals(test.firstName, firstName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //assertEquals(2, myClass.add(x,y));
    }



}
