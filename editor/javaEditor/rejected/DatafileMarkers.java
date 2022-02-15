package gamedata;

public abstract class DatafileMarkers {
    public static final byte TYPE_ANIMATION = 0;
    public static final byte TYPE_LETFANIM  = 1;
    public static final byte TYPE_CHAMPION = 2;
    public static final byte TYPE_STAGE = 3;
    public static final byte TYPE_IMAGE = 4;
    public static final byte NOTFOUND = (byte)0xF0;
    public static final byte INTERFILE = (byte)0xFF;
    public static final byte DESCRIPTORSTART = (byte)0xFE;
    public static final byte GENERICSEPARATOR = (byte)0xFD;
    public static final byte ANIMSPEED = 1;
    public static final byte FRAMEINFO = 2;
    public static final byte FRAMEDURATION = 0x20;
    public static final byte FRAMEORIGIN = 0x21;
    public static final byte FRAMEMOVEMENT = 0x22;
    public static final byte HURTBOXINFO = 3;
    public static final byte HURTBOXFRAMEINDEX = 0x31;
    public static final byte HITBOXINFO = 4;
    public static final byte HITBOXFRAMEINDEX = 0x41;
    public static final byte MOVEINFO = 2;
    public static final byte MOVELANDINGLAG = 0x20;
    public static final byte MULTIMOVE = 3;
    public static final byte MULTIMOVEEND = 0x30;
    public static final byte PLATFORMINFO = 1;

    public static final short MAX_VAL_SHORT = 32767;
}
