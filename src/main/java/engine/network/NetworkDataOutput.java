package engine.network;

import java.io.*;

/**
 * Created by eirik on 27.07.2017.
 */
public class NetworkDataOutput {


    private ByteArrayOutputStream byteOut;
    private DataOutputStream out;


//    public NetworkDataOutput(OutputStream out) {
//        this.out = new DataOutputStream( out );
//    }

    public NetworkDataOutput() {
        byteOut = new ByteArrayOutputStream();
        out = new DataOutputStream( byteOut );
    }

    public void close() {
        try {
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] getBytes() {
        return byteOut.toByteArray();
    }

    public void writeByte(byte value) {
        try {
            out.writeByte(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeBoolean(boolean value) {
        try {
            out.writeBoolean(value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeInt(int value) {
        try {
            out.writeInt(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeFloat(float value) {
        try {
            out.writeFloat(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeChar(char value) {
        try {
            out.writeChar(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * first writes a byte that is the length of the string
     * then it writes that many characters
     * @return
     */
    public void writeString(String value) {
        if (value.length() > Byte.MAX_VALUE) throw new IllegalArgumentException("Trying to write a string larger than a byte");

        byte strLen = (byte)value.length();
        writeByte(strLen);

        for (int i = 0; i < strLen; i++) {
            writeChar(value.charAt(i));
        }
    }
}
