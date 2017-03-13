package com.vendomatica.vendroid.connectivity.protocols.ddcmp;

import com.vendomatica.vendroid.connectivity.protocols.IProtocolsDataManagement;
import com.vendomatica.vendroid.connectivity.protocols.statemachine.IEventSync;
import com.vendomatica.vendroid.connectivity.protocols.statemachine.StateBase;

import java.io.IOException;

public class DDCMPDoneState<AI extends IProtocolsDataManagement>
        extends StateBase<AI> implements IProtocolsDataManagement{


    public DDCMPDoneState(AI automation, IEventSync eventSync) {
        super(automation, eventSync);
    }

    @Override
    public boolean startAudit() {
        return false;
    }

    @Override
    public void stopAudit() {

    }

    @Override
    public void update(int deltaTime) throws IOException {

    }
}
