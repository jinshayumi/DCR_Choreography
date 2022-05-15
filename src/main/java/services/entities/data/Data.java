package services.entities.data;

import java.io.Serializable;

public abstract class Data implements Serializable {
    public Object data;

    public Data(Object data){
        this.data = data;
    }

    public abstract Object getData();
}
