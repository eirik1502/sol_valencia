package engine.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by eirik on 27.07.2017.
 */
public class NetworkDataInput {

    private DataInputStream in;


//    public NetworkDataInput(DataInputStream in) {
//        this.in = in;
//    }
//    public NetworkDataInput(InputStream in) {
//        this( new DataInputStream(in) );
//    }
//

    public NetworkDataInput(byte[] bytes) {
        in = new DataInputStream( new ByteArrayInputStream(bytes) );
    }

    public void close() {
        try {
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int available() {
        try {
            return in.available();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int intAvailable() {
        return available() / Integer.BYTES;
    }


    public byte readByte() {
        try {
            return in.readByte();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean readBoolean() {
        try {
            return in.readBoolean();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int readInt() {
        try {
            return in.readInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public float readFloat() {
        try {
            return in.readFloat();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public char readChar() {
        try {
            return in.readChar();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * reads first a byte that is the length of the string
     * then it reads that many characters
     * @return
     */
    public String readString() {
        byte strLen = readByte();
        StringBuilder s = new StringBuilder(strLen);

        for (int i = 0; i < strLen; i++) {
            s.append(readChar());
        }

        return s.toString();
    }
}
