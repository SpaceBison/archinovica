package archinovica;

import javax.sound.midi.*;

public class LiveApplication {

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

        Receiver finalReceiver = receiver;
        Runtime.getRuntime().addShutdownHook(new Thread(finalReceiver::close));

        LiveReceiver liveReceiver = new LiveReceiver(archinovica, finalReceiver, synthesizer);

        try {
            MidiSystem.getTransmitter().setReceiver(liveReceiver);
        } catch (MidiUnavailableException e) {
            throw new RuntimeException(e);
        }

        new GUI(archinovica, liveReceiver);
    }

}
