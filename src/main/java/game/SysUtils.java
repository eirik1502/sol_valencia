package game;

import engine.OnScreenSys;
import engine.UserCharacterInputSys;
import engine.UserInput;
import engine.WorldContainer;
import engine.audio.AudioSys;
import engine.character.CharacterSys;
import engine.character.UserInputToCharacterSys;
import engine.combat.DamageResolutionSys;
import engine.combat.abilities.AbilitySys;
import engine.combat.abilities.HitboxResolutionSys;
import engine.combat.abilities.ProjectileSys;
import engine.graphics.RenderSys;
import engine.graphics.view_.ViewControlSys;
import engine.network.TcpPacketInput;
import engine.network.TcpPacketOutput;
import engine.network.client.ClientNetworkInSys;
import engine.network.client.ClientNetworkOutSys;
import engine.network.client.InterpolationSys;
import engine.network.server.ServerNetworkSys;
import engine.physics.CollisionDetectionSys;
import engine.physics.HoleResolutionSys;
import engine.physics.NaturalResolutionSys;
import engine.physics.PhysicsSys;
import engine.visualEffect.VisualEffectSys;
import engine.window.Window;
import game.server.ServerClientHandler;

import java.util.List;

/**
 * Created by eirik on 29.07.2017.
 */
public class SysUtils {

    public static void addOfflineSystems(WorldContainer wc, Window window, UserInput userInput) {
        //control systems
        wc.addSystem(new UserCharacterInputSys(userInput));
        wc.addSystem(new UserInputToCharacterSys());

        //charcter systems
        wc.addSystem(new CharacterSys());

        //ability sysstems
        wc.addSystem(new AbilitySys());
        wc.addSystem(new ProjectileSys()); //<---   was moved

        //collision and resolution
        wc.addSystem(new CollisionDetectionSys());
        wc.addSystem(new HoleResolutionSys());
        wc.addSystem(new HitboxResolutionSys());
        wc.addSystem(new DamageResolutionSys());
        wc.addSystem(new NaturalResolutionSys());

        //physics
        wc.addSystem(new PhysicsSys());

        //updating the view
        wc.addSystem(new ViewControlSys());

        //visual effect starting and progression
        wc.addSystem(new VisualEffectSys());
        wc.addSystem(new AudioSys());

        //containing entities on screen
        wc.addSystem(new OnScreenSys(wc, 2));

        //render
        wc.addSystem(new RenderSys(window));
    }

    /**
     * Add server systems. window may be null
     * @param wc
     * @param window
     */
    public static void addServerSystems(WorldContainer wc, Window window,
                                        List<ServerClientHandler> clients) {
        wc.addSystem(new CharacterSys());

        wc.addSystem(new AbilitySys());
        wc.addSystem(new ProjectileSys()); //<---- moved

        wc.addSystem(new ServerNetworkSys( clients ));

        wc.addSystem(new CollisionDetectionSys());
        wc.addSystem(new HoleResolutionSys());
        wc.addSystem(new HitboxResolutionSys());
        wc.addSystem(new DamageResolutionSys());
        wc.addSystem(new NaturalResolutionSys());

        wc.addSystem(new PhysicsSys());

        if (window != null) {
            wc.addSystem(new RenderSys(window));
        }
    }

    public static void addClientSystems(WorldContainer wc, Window window, UserInput userInput,
                                         TcpPacketInput tcpPacketIn, TcpPacketOutput tcpPacketOut) {
        wc.addSystem(new UserCharacterInputSys(userInput));

        wc.addSystem(new ClientNetworkInSys(tcpPacketIn));
        wc.addSystem(new AbilitySys());
        wc.addSystem(new PhysicsSys());

        wc.addSystem(new InterpolationSys());

        wc.addSystem(new ProjectileSys());

        wc.addSystem(new ViewControlSys());

        wc.addSystem(new ClientNetworkOutSys(tcpPacketOut, userInput));

        wc.addSystem(new VisualEffectSys());
        wc.addSystem(new AudioSys());


        wc.addSystem(new OnScreenSys(wc, 2));

        wc.addSystem(new RenderSys(window));
    }


}
