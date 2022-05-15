package services.entities.data;

import services.entities.data.Data;

import java.util.Objects;

public class StringData extends Data {
    String data;

    public StringData(String data) {
        super(data);
        this.data = data;
    }

    @Override
    public String getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringData that = (StringData) o;
        return data.equals(that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }
}
