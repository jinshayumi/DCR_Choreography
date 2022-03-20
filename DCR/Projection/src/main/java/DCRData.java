import services.entities.Data;
import services.entities.IntData;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DCRData {
    public static void main(String[] args) throws ClassNotFoundException {
        Data data = new IntData(1);
        System.out.println(data.getClass().equals(Class.forName("services.entities.IntData")));
        System.out.println(data.getData());

        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;
        try {
            // 序列化
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(data);
            byte[] bytes = baos.toByteArray();
            ByteArrayInputStream bais = null;
            bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            System.out.println(ois.readObject().getClass());
        } catch (Exception e) {

        }

        try {
            // 反序列化

        } catch (Exception e) {

        }

    }
}
