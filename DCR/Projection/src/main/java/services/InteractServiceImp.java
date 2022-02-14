package services;

import models.dcrGraph.DCRGraph;
import services.utilities.Message;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class InteractServiceImp extends UnicastRemoteObject implements IService {
    // configuration.
    private String role;
    private DCRGraph dcrGraph;
    private HashMap<String, Integer> portMap;
    private Registry registry;

    // states.
    private HashMap<String, String> values;

    public InteractServiceImp(String role, DCRGraph dcrGraph) throws RemoteException {
        this.role = role;
        this.dcrGraph = dcrGraph;
        registry = LocateRegistry.getRegistry(8088);
    }

    @Override
    public void interact(Message message) throws RemoteException {
        System.out.println("hello, "+ "I am " + role + ",\nI receive a message "+ message.getEvent() + " from " + message.getFrom());
        dcrGraph.execute(message.getEvent());

    }

    public void execute(Message msg) throws NotBoundException, RemoteException {
        // if the event is enabled?
        if(!dcrGraph.enabled(msg.getEvent())){
            System.out.println("event " + msg.getEvent() + " is not enabled by " + role);
            return;
        }
        dcrGraph.execute(msg.getEvent());

        // send the messages to the receivers.
        for(String receiver: msg.getTo()){
            IService server = (IService) registry.lookup(receiver);
            server.interact(msg);
        }
    }
}
