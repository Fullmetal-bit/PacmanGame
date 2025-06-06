package util;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class SoundPlayer {

    public static void playSound(String resourcePath) {
        URL url = SoundPlayer.class.getResource(resourcePath);
        if (url == null) {
            System.err.println("❌ Sound file not found: " + resourcePath);
            return;
        }

        try (AudioInputStream audioIn = AudioSystem.getAudioInputStream(url)) {
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();

            // Optional: add a listener to close the clip after playback finishes
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });

        } catch (UnsupportedAudioFileException e) {
            System.err.println("❌ Unsupported audio file: " + resourcePath);
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("❌ IO error while playing sound: " + resourcePath);
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            System.err.println("❌ Audio line unavailable to play sound: " + resourcePath);
            e.printStackTrace();
        }
    }
}
