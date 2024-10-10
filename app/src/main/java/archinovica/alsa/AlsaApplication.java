package archinovica.alsa;

import archinovica.Archinovica;
import archinovica.GUI;
import archinovica.LiveReceiver;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Synthesizer;

public class AlsaApplication {
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
            System.err.println("Could not get MidiSystem transmitter");
        }

        AlsaTransmitter alsaTransmitter = new AlsaTransmitter();
        alsaTransmitter.setReceiver(finalReceiver);
        alsaTransmitter.start();

        new GUI(archinovica, liveReceiver);
    }
}
