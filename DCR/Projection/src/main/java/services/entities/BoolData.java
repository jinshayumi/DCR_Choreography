package services.entities;

public class BoolData extends Data{
    boolean data;

    public BoolData(boolean data) {
        super(data);
        this.data = data;
    }

    @Override
    public Object getData() {
        return data;
    }
}
