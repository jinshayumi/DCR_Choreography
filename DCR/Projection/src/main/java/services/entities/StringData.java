package services.entities;

public class StringData extends Data{
    String data;

    public StringData(String data) {
        super(data);
        this.data = data;
    }

    @Override
    public Object getData() {
        return null;
    }
}
