package gamedata;

public abstract class DatafileMarkers {
    public static final int TYPE_ANIMATION = 0;
    public static final int TYPE_LETFANIM  = 1;
    public static final int TYPE_CHAMPION = 2;
    public static final int TYPE_STAGE = 3;
    public static final int TYPE_IMAGE = 4;
    public static final int NOTFOUND = 0xF0;
    public static final int INTERFILE = 0xFF;
    public static final int DESCRIPTORSTART = 0xFE;
    public static final int GENERICSEPARATOR = 0xFD;
    public static final int ANIMSPEED = 1;
    public static final int FRAMEINFO = 2;
    public static final int FRAMEDURATION = 0x20;
    public static final int FRAMEORIGIN = 0x21;
    public static final int FRAMEMOVEMENT = 0x22;
    public static final int HURTBOXINFO = 3;
    public static final int HURTBOXFRAMEINDEX = 0x31;
    public static final int HITBOXINFO = 4;
    public static final int HITBOXFRAMEINDEX = 0x41;
    public static final int MOVEINFO = 2;
    public static final int MOVELANDINGLAG = 0x20;
    public static final int MULTIMOVE = 3;
    public static final int MULTIMOVEEND = 0x30;
    public static final int PLATFORMINFO = 1;
}
