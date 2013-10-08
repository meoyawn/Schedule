package com.stiggpwnz.schedule;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

/**
 * Created by adel on 10/8/13
 */
@RunWith(RobolectricGradleTestRunner.class)
public class BootCompletedReceiverTest {

    @Test
    public void testShouldForceNotify() throws Exception {
        assertTrue(1 == 1);
    }

    @Test
    public void testOnReceive() throws Exception {
        assertTrue(1 == 1);
    }
}
