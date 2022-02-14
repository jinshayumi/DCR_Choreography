package services;

import services.utilities.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IService extends Remote {
    public void interact(Message message) throws RemoteException;
}
