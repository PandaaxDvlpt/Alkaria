package fr.izuka.alkaria;

import javax.swing.JFrame;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import fr.theshark34.swinger.Swinger;
import fr.theshark34.swinger.util.WindowMover;

public class LauncherFrame extends JFrame {
	private static LauncherFrame instance;
	private LauncherPanel launcherPanel;
	
	
	public LauncherFrame() {
		// Size Window Alkaria
		this.setSize(900,500);
		// Title Alkaria
		this.setTitle("Alkaria");
		// Window Background Settings Alkaria
		this.setUndecorated(true);
		this.setDefaultCloseOperation(3);
		this.setLocationRelativeTo(null);
		this.setIconImage(Swinger.getResource("favicon.png"));
		// Launcher Panel config
		this.launcherPanel = new LauncherPanel();
		this.setContentPane(this.launcherPanel = new LauncherPanel());
		// Window Mover
		WindowMover mover = new WindowMover(this);
		this.addMouseListener(mover);
		this.addMouseMotionListener(mover);		
		
		this.setVisible(true);
	}
	
	public static void main(String[] a) {
		Swinger.setSystemLookNFeel();
		Swinger.setResourcePath("/fr/izuka/alkaria/rc/");
		
		instance = new LauncherFrame();
		
		
        DiscordRPC lib = DiscordRPC.INSTANCE;
        String applicationId = "635540910310031361";
        String steamId = "";
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        handlers.ready = (DiscordUser) -> System.out.println("Ready!");
        lib.Discord_Initialize(applicationId, handlers, true, steamId);
        DiscordRichPresence presence = new DiscordRichPresence();
        presence.startTimestamp = System.currentTimeMillis() / 1000; // epoch second
        presence.details = "Actuellement sur le serveur";
        presence.state = "Rôleplay";
        presence.largeImageKey = "favicon";
        presence.largeImageText = "favicon";
        lib.Discord_UpdatePresence(presence);

        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                lib.Discord_RunCallbacks();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {}
            }
        }, "RPC-Callback-Handler").start();
    }
	
	public static LauncherFrame getInstance() {
		return instance;
	}

	public LauncherPanel getLauncherPanel() {
		return launcherPanel;
	}
}

