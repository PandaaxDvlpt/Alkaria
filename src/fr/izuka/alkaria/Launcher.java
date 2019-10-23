package fr.izuka.alkaria;

import java.io.File;
import java.util.Arrays;

import fr.litarvan.openauth.AuthPoints;
import fr.litarvan.openauth.AuthenticationException;
import fr.litarvan.openauth.Authenticator;
import fr.litarvan.openauth.model.AuthAgent;
import fr.litarvan.openauth.model.response.AuthResponse;
import fr.theshark34.openlauncherlib.LaunchException;
import fr.theshark34.openlauncherlib.external.ExternalLaunchProfile;
import fr.theshark34.openlauncherlib.external.ExternalLauncher;
import fr.theshark34.openlauncherlib.minecraft.AuthInfos;
import fr.theshark34.openlauncherlib.minecraft.GameFolder;
import fr.theshark34.openlauncherlib.minecraft.GameInfos;
import fr.theshark34.openlauncherlib.minecraft.GameTweak;
import fr.theshark34.openlauncherlib.minecraft.GameType;
import fr.theshark34.openlauncherlib.minecraft.GameVersion;
import fr.theshark34.openlauncherlib.minecraft.MinecraftLauncher;
import fr.theshark34.supdate.BarAPI;
import fr.theshark34.supdate.SUpdate;
import fr.theshark34.supdate.application.integrated.FileDeleter;
import fr.theshark34.swinger.Swinger;

public class Launcher {
	// Auths & Update
	private static AuthInfos authInfos;
    private static Thread updateThread;
    //GameInfos setDir etc
 public static final GameVersion AL_VERSION = new GameVersion("1.12.2", GameType.V1_8_HIGHER);
 public static final GameInfos AL_INFOS = new GameInfos("Alkaria", AL_VERSION, new GameTweak[] {GameTweak.FORGE});
 public static final File AL_DIR = AL_INFOS.getGameDir();
 public static void auth(String username, String password) throws AuthenticationException {
     Authenticator authenticator = new Authenticator("https://authserver.mojang.com/",
             AuthPoints.NORMAL_AUTH_POINTS);
     AuthResponse response = authenticator.authenticate(AuthAgent.MINECRAFT, username, password, "");
     authInfos = new AuthInfos(response.getSelectedProfile().getName(), response.getAccessToken(),
             response.getSelectedProfile().getId());
 }
 public static void update() throws Exception {
     SUpdate su = new SUpdate("http://uvoniaz.000webhostapp.com/supdate/", AL_DIR);
     su.addApplication(new FileDeleter());
     su.getServerRequester().setRewriteEnabled(true);

     updateThread = new Thread() {
         private int val;
         private int max;

         public void run() {
             while (!isInterrupted()) {
                 if (BarAPI.getNumberOfFileToDownload() == 0) {
                     LauncherFrame.getInstance().getLauncherPanel().setInfoText("Vérification des fichiers");
                 } else {
                     this.val = ((int) (BarAPI.getNumberOfTotalDownloadedBytes() / 1000L));
                     this.max = ((int) (BarAPI.getNumberOfTotalBytesToDownload() / 1000L));
                     LauncherFrame.getInstance().getLauncherPanel().getProgressBar().setMaximum(this.max);
                     LauncherFrame.getInstance().getLauncherPanel().getProgressBar().setValue(this.val);

                     LauncherFrame.getInstance().getLauncherPanel()
                             .setInfoText("Téléchargement des fichiers " + BarAPI.getNumberOfDownloadedFiles()
                                     + "/" + BarAPI.getNumberOfFileToDownload() + " "
                                     + Swinger.percentage(this.val, this.max) + "%");
                 }
             }
         }
     };
     updateThread.start();

     su.start();
     updateThread.interrupt();
 }

 public static void launch() throws LaunchException {
     ExternalLaunchProfile profile = MinecraftLauncher.createExternalProfile(AL_INFOS, GameFolder.BASIC, authInfos);
     profile.getVmArgs().addAll(
             Arrays.asList(LauncherFrame.getInstance().getLauncherPanel().getRamSelector().getRamArguments()));
     ExternalLauncher launcher = new ExternalLauncher(profile);

     LauncherFrame.getInstance().setVisible(false);

     launcher.launch();
 }

 public static void interruptThread() {
     updateThread.interrupt();
 }
}
