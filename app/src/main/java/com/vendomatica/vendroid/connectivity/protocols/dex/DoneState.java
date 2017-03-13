package com.vendomatica.vendroid.connectivity.protocols.dex;


import android.os.Looper;

import com.vendomatica.vendroid.connectivity.protocols.IProtocolsDataManagement;
import com.vendomatica.vendroid.connectivity.protocols.statemachine.Event;
import com.vendomatica.vendroid.connectivity.protocols.statemachine.IEventSync;
import com.vendomatica.vendroid.connectivity.protocols.statemachine.StateBase;

import java.io.IOException;

public class DoneState <AI extends IProtocolsDataManagement>
        extends StateBase<AI> implements IProtocolsDataManagement{

    public final static Event INIT = new Event("INIT", 0);


    public DoneState(AI automation, IEventSync eventSync) {
        super(automation, eventSync);
    }

    @Override
    public boolean startAudit() {
        castEvent(INIT);
        return true;
    }

    @Override
    public void stopAudit() {
        castEvent(INIT);
    }

    @Override
    public void update(int deltaTime) throws IOException {
        Looper.prepare();
        onDone();
        castEvent(INIT);
    }


    private void onDone(){
    /*
        Handler handler = DexProtocolReader.getHandler();
        Message msg = handler.obtainMessage(ProtocolConstants.MSG_ACTION_AUDIT_DONE);
        handler.sendMessage(msg);
    */
    }
}
