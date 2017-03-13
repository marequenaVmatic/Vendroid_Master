package com.vendomatica.vendroid.connectivity.protocols;

public class ProtocolsConstants {

    final public static byte NUL = 0x00;
    final public static byte SOH = 0x01;
    final public static byte STX = 0x02;
    final public static byte ETX = 0x03;
    final public static byte EOT = 0x04;
    final public static byte ENQ = 0x05;
    final public static byte ACK = 0x06;
    final public static byte DLE = 0x10;
    final public static byte NAK = 0x15;
    final public static byte SYN = 0x16;
    final public static byte ETB = 0x17;

    public final static int MSG_ACTION_AUDIT_DONE = 0x1111;
    public final static int MSG_ACTION_AUDIT_ERROR = 0x1112;
    public final static int MSG_ACTION_AUDIT_TIMEOUT = 0x1113;
    public final static int MSG_ACTION_AUDIT_UPDATE = 0x1114;
    public final static int MSG_ACTION_AUDIT_LOG = 0x1115;
    public final static int MSG_ACTION_AUDIT_DATA = 0x1116;
    public final static int MSG_ACTION_AUDIT_DATA_READ = 0x1117;
}
