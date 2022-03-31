package services.entities;

public class IntData extends Data{
    Integer data;

    public IntData(Integer data) {
        super(data);
        this.data = data;
    }

    @Override
    public Integer getData() {
        return data;
    }
}
