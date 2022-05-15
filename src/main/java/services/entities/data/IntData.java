package services.entities.data;

import services.entities.data.Data;

import java.util.Objects;

public class IntData extends Data {
    Integer data;

    public IntData(Integer data) {
        super(data);
        this.data = data;
    }

    @Override
    public Integer getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntData intData = (IntData) o;
        return data.equals(intData.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }
}
