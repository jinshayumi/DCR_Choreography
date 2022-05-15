package services.entities.data;

import services.entities.data.Data;

public class VoidData extends Data {
    public VoidData(Object data) {
        super(data);
    }

    @Override
    public Object getData() {
        return null;
    }
}
