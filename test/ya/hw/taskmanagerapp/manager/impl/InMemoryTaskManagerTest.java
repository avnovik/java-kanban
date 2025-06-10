package ya.hw.taskmanagerapp.manager.impl;

import ya.hw.taskmanagerapp.manager.Managers;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createManager() {
        return new InMemoryTaskManager(Managers.getDefaultHistory());
    }
}
