package com.chen.util;

import java.io.*;

public class CloneUtil {
    public static <T> T deepClone(T object) {
        ObjectOutputStream oos;
        ByteArrayOutputStream bos;
        ObjectInputStream ois;
        ByteArrayInputStream bis;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(object);
            oos.flush();
            bis = new ByteArrayInputStream(bos.toByteArray());
            ois = new ObjectInputStream(bis);
            return (T) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
