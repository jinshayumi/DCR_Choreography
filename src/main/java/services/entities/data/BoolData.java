package services.entities.data;

import java.util.Objects;

public class BoolData extends Data {
    boolean data;

    public BoolData(boolean data) {
        super(data);
        this.data = data;
    }

    @Override
    public Boolean getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoolData boolData = (BoolData) o;
        return data == boolData.data;
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }
}
