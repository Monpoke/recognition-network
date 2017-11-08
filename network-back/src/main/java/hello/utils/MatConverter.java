package hello.utils;

import org.bytedeco.javacpp.opencv_core;

import static org.bytedeco.javacpp.opencv_core.*;

public class MatConverter {
    public static byte[] convertMatToByte(Mat m){
        int sz = (int)(m.total() * m.channels());
        byte[] barr = new byte[sz];
        m.data().get(barr);
        return barr;
    }

}
