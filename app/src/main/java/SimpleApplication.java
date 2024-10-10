import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Synthesizer;
import javax.swing.*;

public class SimpleApplication {

    public static void main(String[] args) {
        Archinovica archinovica = new Archinovica();

        Synthesizer synthesizer = null;
        try {
            synthesizer = MidiSystem.getSynthesizer();
        } catch (MidiUnavailableException e) {
            throw new RuntimeException(e);
        }

        Receiver receiver = null;
        try {
            receiver = MidiSystem.getReceiver();
        } catch (MidiUnavailableException e) {
            receiver = new LiveReceiver.Piano(synthesizer);
        }

        SimpleReceiver simpleReceiver = new SimpleReceiver(archinovica, receiver);

        try {
            MidiSystem.getTransmitter().setReceiver(simpleReceiver);
        } catch (MidiUnavailableException e) {
            JOptionPane.showMessageDialog(null,
                    "No MIDI device found. Don't panic! If you're trying to perform\nlive on the Archinoivca, please connect a MIDI keyboard to the \ncomputer, and then restart the software.");
        }

        new GUI(archinovica, null);
    }

}
