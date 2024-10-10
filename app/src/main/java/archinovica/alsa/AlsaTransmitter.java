package archinovica.alsa;

import dev.atsushieno.alsa.javacpp.global.Alsa;
import dev.atsushieno.alsa.javacpp.pollfd;
import dev.atsushieno.alsa.javacpp.snd_seq_t;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;

public class AlsaTransmitter extends Thread implements Transmitter {

    private static final short POLLIN = 1;

    private Receiver receiver;

    @Override
    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public Receiver getReceiver() {
        return receiver;
    }

    @Override
    public void close() {
        interrupt();
    }

    @Override
    public void run() {
        super.run();

        System.out.println("start transmitter");

        Alsa.snd_asoundlib_version().close();

        snd_seq_t seq = new snd_seq_t();
        Alsa.snd_seq_create_simple_port(
                seq,
                "archinovica",
                Alsa.SND_SEQ_PORT_CAP_WRITE | Alsa.SND_SEQ_PORT_CAP_SUBS_WRITE,
                Alsa.SND_SEQ_PORT_TYPE_MIDI_GENERIC | Alsa.SND_SEQ_PORT_TYPE_APPLICATION);

        System.out.println("created sequencer");

        while (!isInterrupted()) {
            int descriptorCount = Alsa.snd_seq_poll_descriptors_count(seq, POLLIN);
            pollfd fd = new pollfd(descriptorCount);
            int polledCount = Alsa.snd_seq_poll_descriptors(seq, fd, descriptorCount, POLLIN);
            short[] events = new short[polledCount];
            Alsa.snd_seq_poll_descriptors_revents(seq, fd, descriptorCount, events);

            if (receiver != null) {
                for (short event : events) {
                    try {
                        receiver.send(new ShortMessage(event), -1);
                    } catch (InvalidMidiDataException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        Alsa.snd_seq_close(seq);
    }
}
