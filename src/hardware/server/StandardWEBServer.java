package hardware.server;

import enums.Bandwidth;
import enums.LinkTypes;
import enums.PacketTypes;
import exceptions.BadCallException;

/**
 * Classe définissant un serveur WEB classique
 */
public class StandardWEBServer extends AbstractServer
{

    /**
     * Constructeur
     *
     * @param MAC
     * @param IP
     * @param default_gateway
     * @param default_port
     */
    public StandardWEBServer(int MAC, packet.IP IP, packet.IP default_gateway, int default_port) throws BadCallException {
        super(LinkTypes.ETH, Bandwidth.ETH_1G, 60, MAC, IP, default_gateway, default_port, PacketTypes.WEB);
    }
}