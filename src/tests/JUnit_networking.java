package tests;

import actions.Actions;
import enums.LinkTypes;
import enums.PacketTypes;
import exceptions.BadCallException;
import hardware.client.StandardPC;
import hardware.hub.Standard24Hub;
import hardware.router.Standard2ETHRouter;
import hardware.server.StandardWEBServer;
import hardware.switchs.Standard24Switch;
import org.junit.Test;
import packet.IP;

import java.util.ArrayList;

public class JUnit_networking
{
    /**
     * A -- SRA -- central -- SRB -- B   (SR = switch)
     */
    @Test
    public void setUp()
    {
        try {
            Standard2ETHRouter central = new Standard2ETHRouter(new ArrayList<Integer>(){{add(1);add(2);}},
                new ArrayList<IP>(){{add(new IP(192,168,0,1));add(new IP(192,168,1,1));}},
                    new IP(192,168,0,2), 0);

            Standard2ETHRouter A = new Standard2ETHRouter(new ArrayList<Integer>(){{add(3);add(4);}},
                new ArrayList<IP>(){{add(new IP(192,168,0,2));add(new IP(192,168,3,1));}},
                    new IP(192,168,0,1), 0);

            Standard2ETHRouter B = new Standard2ETHRouter(new ArrayList<Integer>(){{add(5);add(6);}},
                new ArrayList<IP>(){{add(new IP(192,168,1,2));add(new IP(192,168,4,1));}},
                    new IP(192,168,1,1), 0);

            Standard2ETHRouter C = new Standard2ETHRouter(new ArrayList<Integer>(){{add(7);add(8);}},
                    new ArrayList<IP>(){{add(new IP(192,168,1,3));add(new IP(192,168,5,1));}},
                    new IP(192,168,1,1), 0);

            Standard2ETHRouter D = new Standard2ETHRouter(new ArrayList<Integer>(){{add(9);add(10);}},
                    new ArrayList<IP>(){{add(new IP(192,168,0,3));add(new IP(192,168,6,1));}},
                    new IP(192,168,0,1), 0);

            StandardPC jeanLuc_PC = new StandardPC(11, new IP(192,168,0,5), new IP(192,168,0,1), 0);
            StandardWEBServer WEB = new StandardWEBServer(12, new IP(192,168,1,5), new IP(192,168,1,1), 0);

            Standard24Switch srA = new Standard24Switch();
            Standard24Hub srB = new Standard24Hub();

            central.addRoutingRule(0, new IP(192,168,0,0), new IP(255,255,255,0), new IP(0,0,0,0), 1);
            central.addRoutingRule(1, new IP(192,168,1,0), new IP(255,255,255,0), new IP(0,0,0,0), 1);

            Actions.connect(A, srA, LinkTypes.ETH);
            Actions.connect(B, srB, LinkTypes.ETH);
            Actions.connect(C, srB, LinkTypes.ETH);
            Actions.connect(D, srA, LinkTypes.ETH);
            Actions.connect(central, srA, LinkTypes.ETH);
            Actions.connect(central, srB, LinkTypes.ETH);
            Actions.connect(jeanLuc_PC, srA, LinkTypes.ETH);
            Actions.connect(WEB, srB, LinkTypes.ETH);

            jeanLuc_PC.launchRequest(PacketTypes.WEB, new IP(192,168,1,5));

            while(true)
            {
                A.treat();
                B.treat();
                D.treat();
                C.treat();
                srA.treat();
                srB.treat();
                central.treat();
                jeanLuc_PC.treat();
                WEB.treat();

                A.validateStack();
                B.validateStack();
                D.validateStack();
                C.validateStack();
                srA.validateStack();
                srB.validateStack();
                central.validateStack();
                jeanLuc_PC.validateStack();
                WEB.validateStack();

                Thread.sleep(1000);
            }
        } catch (BadCallException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}