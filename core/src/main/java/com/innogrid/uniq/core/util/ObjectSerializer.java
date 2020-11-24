package com.innogrid.uniq.core.util;

import java.io.*;
import java.util.Base64;

public class ObjectSerializer {

    public static <T> String serializedData(T t) {
        byte[] serializedData;

        try {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                    oos.writeObject(t);
                    // serializedMember -> 직렬화된 member 객체
                    serializedData = baos.toByteArray();
                }
            }

            return Base64.getEncoder().encodeToString(serializedData);
        } catch (IOException ioe) {

        }

        return null;
    }

    public static <T> T deserializedData(String serializedData) {
        byte[] serializedMember = Base64.getDecoder().decode(serializedData);

        try {
            try (ByteArrayInputStream bais = new ByteArrayInputStream(serializedMember)) {
                try (ObjectInputStream ois = new ObjectInputStream(bais)) {
                    // 역직렬화된 Member 객체를 읽어온다.
                    Object objectMember = ois.readObject();
                    T data = (T) objectMember;
                    return data;
                }
            }
        } catch (IOException ioe) {

        } catch (ClassNotFoundException cnfe) {

        }

        return null;
    }
}
