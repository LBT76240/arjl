package hardware.hub;


import enums.Bandwidth;
import enums.ClockSpeed;
import enums.LinkTypes;
import exceptions.BadCallException;
import exceptions.OverflowException;
import hardware.AbstractHardware;
import packet.Packet;

import java.util.ArrayList;

/**
 * Classe abstraite définissant les hubs
 * @author J. Desvignes
 */
public abstract class AbstractHub extends AbstractHardware
{
    /**
     * Constructeur à appeller avec super()
     *
     * @param port_types     liste des types de liens connectables
     * @param port_bandwidth liste des bandes passantes (couplée avec port_types !)
     */
    public AbstractHub(ArrayList<LinkTypes> port_types, ArrayList<Bandwidth> port_bandwidth) throws BadCallException {
        /**
         * Le hub n'étant pas supposé avoir de file s'attente ou de temps de traitement, on lui donne
         * une file infinie et un temps de traitement nul
         */
        super(port_types, port_bandwidth, Integer.MAX_VALUE, ClockSpeed.INSTANT);
    }

    @Override
    public void receive(Packet packet, int port)
    {
        packet.lastPort = port;
        synchronized (this.ports) {
            this.stack.add(packet);
        }
    }

    @Override
    public void treat() throws BadCallException, OverflowException
    {
        if(stack.isEmpty())
            return;
        Packet p;
        synchronized (this.ports) {
            p = stack.get(0);
            stack.remove(0);
        }
        if(p.alreadyTreated) //Si c'était un paquet en attente de libération de la bande passante
        {
            p.alreadyTreated=false;
            this.send(p, p.destinationPort);
            return;
        }
        for(int i=0 ; i<ports.size() ; i++)
        {
            if (i != p.lastPort)
            {
                if (ports.get(i) != null)
                {
                    this.send(new Packet(p), i);
                }
            }
        }

    }
}