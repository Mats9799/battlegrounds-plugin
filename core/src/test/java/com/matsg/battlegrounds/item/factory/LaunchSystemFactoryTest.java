package com.matsg.battlegrounds.item.factory;

import com.matsg.battlegrounds.TaskRunner;
import com.matsg.battlegrounds.InternalsProvider;
import com.matsg.battlegrounds.item.mechanism.GrenadeLaunch;
import com.matsg.battlegrounds.item.mechanism.LaunchSystem;
import com.matsg.battlegrounds.item.mechanism.LaunchSystemType;
import com.matsg.battlegrounds.item.mechanism.RocketLaunch;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class LaunchSystemFactoryTest {

    private InternalsProvider internals;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        this.internals = mock(InternalsProvider.class);
        this.taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void makeGrenadeLaunchSystem() {
        LaunchSystemType launchSystemType = LaunchSystemType.GRENADE;

        LaunchSystemFactory factory = new LaunchSystemFactory(internals, taskRunner);
        LaunchSystem launchSystem = factory.make(launchSystemType, 0);

        assertTrue(launchSystem instanceof GrenadeLaunch);
    }

    @Test
    public void makeRocketLaunchSystem() {
        LaunchSystemType launchSystemType = LaunchSystemType.ROCKET;

        LaunchSystemFactory factory = new LaunchSystemFactory(internals, taskRunner);
        LaunchSystem launchSystem = factory.make(launchSystemType, 0);

        assertTrue(launchSystem instanceof RocketLaunch);
    }
}
