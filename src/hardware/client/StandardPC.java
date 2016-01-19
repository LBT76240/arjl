package hardware.client;


import enums.Bandwidth;
import enums.LinkTypes;
import exceptions.BadCallException;

/**
 * Classe définissant un PC classique
 */
public class StandardPC extends AbstractClient
{
    /**
     * Constructeur à appeller
     *
     * @param MAC la MAC de l'appareil
     * @param IP l'IP statique
     * @param default_gateway la passerelle par défaut
     */
    public StandardPC(int MAC, packet.IP IP, packet.IP default_gateway) throws BadCallException {
        super(LinkTypes.ETH, Bandwidth.ETH_1G, 20, MAC, IP, default_gateway);
    }

    /**
     * Construteur pour configuration IP par DHCP
     * @param MAC la MAC de l'appareil
     */
    public StandardPC(int MAC) throws BadCallException {
        super(LinkTypes.ETH, Bandwidth.ETH_1G, 20, MAC);
    }
}