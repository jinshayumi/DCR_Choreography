import java.io.*;
public class DeepCopyTest implements Serializable{
    public DeepCopyTest deepClone() throws IOException, ClassNotFoundException {
//将对象写到流里
        ByteArrayOutputStream bo=new ByteArrayOutputStream();
        ObjectOutputStream oo=new ObjectOutputStream(bo);
        oo.writeObject(this);
//从流里读出来
        ByteArrayInputStream bi=new ByteArrayInputStream(bo.toByteArray());
        ObjectInputStream oi=new ObjectInputStream(bi);
        return (DeepCopyTest) (oi.readObject());
    }
    public String name = "";
    public Integer age = 10;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        DeepCopyTest a = new DeepCopyTest();
        System.out.println(a.age);
        System.out.println(a.name);
        DeepCopyTest b = a.deepClone();
        b.age = 15;
        b.name = "b";
        System.out.println("after a name: " + a.name);
        System.out.println("after a age: " + a.age);
        System.out.println("after b name: " + b.name);
        System.out.println("after b age: " + b.age);

    }
}
