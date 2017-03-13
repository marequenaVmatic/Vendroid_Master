package com.vendomatica.vendroid.connectivity.protocols.dex;

import android.os.Handler;
import android.os.Message;

import com.vendomatica.vendroid.connectivity.protocols.IProtocolsDataManagement;
import com.vendomatica.vendroid.connectivity.protocols.IonAuditState;
import com.vendomatica.vendroid.connectivity.protocols.ProtocolsBase;
import com.vendomatica.vendroid.connectivity.protocols.ProtocolsConstants;

import java.io.IOException;

public class DexProtocolReader extends ProtocolsBase {

    public DexProtocolReader(final IonAuditState stateCallback){

        IProtocolsDataManagement initialState = new InitialState<IProtocolsDataManagement>(this, this);
        IProtocolsDataManagement sendEnqState = new SendENQState<IProtocolsDataManagement>(this, this);
        IProtocolsDataManagement enqAckState = new ENQACKState<IProtocolsDataManagement>(this, this);
        IProtocolsDataManagement slaveState = new SlaveState<IProtocolsDataManagement>(this, this);
        IProtocolsDataManagement masterState = new MasterState<IProtocolsDataManagement>(this, this);
        IProtocolsDataManagement collectState = new CollectState<IProtocolsDataManagement>(this, this);
        IProtocolsDataManagement doneState = new DoneState<IProtocolsDataManagement>(this, this);


        addEdge(initialState, InitialState.START_PAIRING, sendEnqState);

        addEdge(sendEnqState, SendENQState.CHECK_ENQ, enqAckState);
        addEdge(sendEnqState, SendENQState.STOP, initialState);

        addEdge(enqAckState, ENQACKState.ENQ_STATE, sendEnqState);
        addEdge(enqAckState, ENQACKState.SLAVE_HANDSHAKE, slaveState);
        addEdge(enqAckState, ENQACKState.MASTER_HANDSHAKE, masterState);
        addEdge(enqAckState, ENQACKState.STOP, initialState);

        addEdge(slaveState, SlaveState.ENQ_STATE, sendEnqState);
        addEdge(slaveState, SlaveState.STATE, slaveState);
        addEdge(slaveState, SlaveState.COLLECT, collectState);
        addEdge(slaveState, SlaveState.STOP, initialState);

        addEdge(masterState, MasterState.ENQ_STATE, sendEnqState);
        addEdge(masterState, MasterState.STATE, masterState);
        addEdge(masterState, MasterState.COLLECT, collectState);
        addEdge(masterState, MasterState.STOP, initialState);

        addEdge(collectState, CollectState.ENQ_STATE, sendEnqState);
        addEdge(collectState, CollectState.STATE, collectState);
        addEdge(collectState, CollectState.DONE, doneState);

        addEdge(doneState, DoneState.INIT, initialState);

        state = initialState;

        DexInternalStorage.getInstance().handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (stateCallback == null) return;

                switch(msg.what){
                    case ProtocolsConstants.MSG_ACTION_AUDIT_DONE:
                        stateCallback.onAuditDone(msg.getData());
                        break;
                    case ProtocolsConstants.MSG_ACTION_AUDIT_DATA:
                        stateCallback.onAuditData(msg.getData());
                        break;
                    case ProtocolsConstants.MSG_ACTION_AUDIT_ERROR:
                        stateCallback.onAuditError(msg.getData());
                        break;
                    case ProtocolsConstants.MSG_ACTION_AUDIT_LOG:
                        stateCallback.onAuditLog(msg.getData());
                        break;
                    case ProtocolsConstants.MSG_ACTION_AUDIT_TIMEOUT:
                        stateCallback.onAuditTimeOut(msg.getData());
                        break;
                    case ProtocolsConstants.MSG_ACTION_AUDIT_UPDATE:
                        stateCallback.onAuditUpdate(msg.getData());
                        break;
                    case ProtocolsConstants.MSG_ACTION_AUDIT_DATA_READ:
                        stateCallback.onAuditDataRead(msg.getData());
                        break;
                }
            }
        };
    }

    @Override
    public boolean startAudit() {
        state.startAudit();
        return true;
    }

    @Override
    public void stopAudit() {
        state.stopAudit();
    }

    @Override
    public void update(int delta) {
        try {
            state.update(delta);
        }catch (IOException e){
            e.printStackTrace();
            stopAudit();
        }
    }

}

