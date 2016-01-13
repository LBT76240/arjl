package hardware.router;


import enums.Bandwidth;
import enums.LinkTypes;
import enums.PacketTypes;
import exceptions.BadCallException;
import hardware.ARPTable;
import hardware.AbstractHardware;
import hardware.Link;
import packet.IP;
import packet.Packet;

import java.util.ArrayList;

public abstract class AbstractRouter extends AbstractHardware
{

    protected RoutingTable routingTable;
    protected ARPTable arp = new ARPTable();

    protected ArrayList<Packet> waitingForARP = new ArrayList<Packet>();

    protected ArrayList<Integer> MACinterfaces;
    protected ArrayList<IP> IPinterfaces;

    /**
     * Constructeur à appeller avec super()
     *
     * @param port_types     liste des types de liens connectables
     * @param port_bandwidth liste des bandes passantes (couplée avec port_types !)
     */
    public AbstractRouter(ArrayList<LinkTypes> port_types, ArrayList<Bandwidth> port_bandwidth,
                          int overflow, ArrayList<Integer> MACinterfaces,
                          ArrayList<IP> IPinterfaces, IP default_gateway, int default_port) throws BadCallException {
        super(port_types, port_bandwidth, overflow);
        this.MACinterfaces = MACinterfaces;
        this.IPinterfaces = IPinterfaces;
        this.routingTable = new RoutingTable(default_port, default_gateway);
    }


    @Override
    public void receive(Packet packet, int port)
    {
        packet.lastPort = port;
        this.futureStack.add(packet);
    }

    @Override
    public void send(Packet packet, int port) throws BadCallException
    {
        if(packet.TTLdown())
            return;
        Link link = ports.get(port);
        link.getOtherHardware(this).receive(packet, ports.get(port).getOtherHardware(this).whichPort(link));
        if(packet.tracked)
            System.out.println(this.IPinterfaces.get(port).toString()+" : sent "+packet.getType()+" to "+packet.dst_addr+" with NHR="+packet.getNHR()+" isResponse="+packet.isResponse);
    }

    @Override
    public void treat() throws BadCallException {
        ArrayList<Packet> newStack = new ArrayList<>(); //Permet de garder les paquets non envoyables
        int[] packetsSent = new int[ports.size()]; //Permet de compter les paquets pour simuler la bande passante
        for(Integer i : packetsSent) //On initialise la liste à 0
            i = 0;

        ArrayList<Object> route;
        int mac;
        for(Packet p : stack)
        {

            if(p.getType() == PacketTypes.ARP)
            {
                if(p.isResponse)
                {
                    for(int i=0 ; i<waitingForARP.size() ; i++)
                    {
                        if(p.src_addr.equals(waitingForARP.get(i).getNHR()))
                        {
                            arp.addRule(p.src_addr, p.src_mac);
                            futureStack.add(waitingForARP.get(i));
                            waitingForARP.remove(i);
                            i--;
                        }
                    }
                }
                else
                {
                    for(IP i : IPinterfaces)
                    {
                        if(i.equals(p.dst_addr))
                        {
                            this.send(new Packet(p.src_addr,
                                    i, MACinterfaces.get(IPinterfaces.indexOf(i)), p.src_mac, PacketTypes.ARP, true, false), p.lastPort);
                            break;
                        }
                    }
                }
                continue;
            }

            if(!MACinterfaces.contains(p.dst_mac) && !MACinterfaces.contains(p.src_mac) ) //S'il ne m'est pas destiné, je l'ignore
                continue;

            if(routingTable.isBroadcast(p)) //Si c'est un broadcast, je l'ignore
                continue;

            if(IPinterfaces.contains(p.dst_addr)) //S'il m'est destiné (en bout de chaîne)
            {
                this.treatData(p);
                continue;
            }

            route = routingTable.routeMe(p.dst_addr); //route={port de redirection ; IP du NHR}
            IP ip;
            if(route.get(1).equals(new IP(0,0,0,0))) //Si le gateway vaut 0.0.0.0 c'est qu'on est arrivé
            {
                mac = arp.findARP(p.dst_addr);
                ip = p.dst_addr;
            }
            else
            {
                mac = arp.findARP((IP) route.get(1));
                ip = (IP)route.get(1);
            }

            p.setNHR(ip);
            if(mac == -1)
            {
                waitingForARP.add(p);
                if(route.get(1).equals(new IP(0,0,0,0))) //Si le gateway vaut 0.0.0.0 c'est qu'on est arrivé
                    ip = p.dst_addr;
                else
                    ip = (IP)route.get(1);
                this.send(new Packet(ip,
                        IPinterfaces.get((int)route.get(0)),
                        MACinterfaces.get((int)route.get(0)), -1, PacketTypes.ARP, false, false), (int)route.get(0));
                continue;
            }

            p.dst_mac = mac;
            p.src_mac = MACinterfaces.get((int)route.get(0));
            this.send(p, (int)route.get(0));

        }
        if(!newStack.isEmpty())
            futureStack.addAll(0, newStack);
    }

    protected void treatData(Packet p) throws BadCallException {
        return;
    }

    public void addRoutingRule(int port_number, IP subnet, IP mask, IP gateway, int metric)
    {
        routingTable.addRule(port_number, subnet, mask, gateway, metric);
    }
}