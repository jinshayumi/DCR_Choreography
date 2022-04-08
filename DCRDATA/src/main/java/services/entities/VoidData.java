package services.entities;

public class VoidData extends Data{
    public VoidData(Object data) {
        super(data);
    }

    @Override
    public Object getData() {
        return null;
    }
}
