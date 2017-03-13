package com.vendomatica.vendroid.connectivity.protocols;

import java.io.IOException;

public interface IProtocolsDataManagement {
        boolean startAudit();
        void stopAudit();
        void update(int deltaTime) throws IOException;
}
