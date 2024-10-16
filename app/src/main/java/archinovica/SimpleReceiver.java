package archinovica;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Write a description of class archinovica.SimpleReceiver here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class SimpleReceiver implements Receiver {

    private final Archinovica archinovica;
    private final Receiver rec;

    public TimerTask timerTask;
    public Timer clock = new Timer();
    public LinkedBlockingQueue<ShortMessage> commands = new LinkedBlockingQueue<ShortMessage>();
    // transpose by 0 EQ'd semitones to begin
    public int transposition = 0;
    public int bendAdjustment = 0;
    public long delay = 30L;
    public boolean rightPedal = false;
    public boolean leftPedal = false;
    public boolean middlePedal = false;
    public boolean useDelay = true, sustain = true;
    public int[] chanTrans = new int[16];
    /*
     * In theory, it may be possible to avoid any delay window,
     * by sending the note on message immediately, followed by
     * all necessary pitch bends once a delay has passed (and chord is
     * detected)
     */

    public SimpleReceiver(Archinovica archinovica, Receiver receiver) {
        this.archinovica = archinovica;
        rec = receiver;
    }

    public void send(MidiMessage message, long timeStamp) {
        if (message instanceof ShortMessage) {
            ShortMessage sm = (ShortMessage) message;

            switch (sm.getCommand()) {
                case ShortMessage.NOTE_ON:

                    //determine appropriate parameters for message to send
                    int channel = sm.getData1() % 12; // why was there + 1?
                    if (channel == 9) {
                        channel = 13;
                    }

                    //synonym for note off (ignored)
                    if (sm.getData2() == 0) {
                        if (sustain) {
                            return;
                        } else {
                            try {
                                ShortMessage aSM = new ShortMessage(ShortMessage.NOTE_ON, channel, sm.getData1() + 2 * chanTrans[channel], sm.getData2());
                                rec.send(aSM, 0L);
                            } catch (InvalidMidiDataException e) {
                                JOptionPane.showMessageDialog(null,
                                        "Something went wrong!  I'm not sure what :-/\nMaybe you can figure it out?");
                            }
                        }
                    }

                    //it's really a noteON, so cancel any pending commands
                    if (timerTask != null) {
                        timerTask.cancel();
                    }

                    int type = ShortMessage.NOTE_ON;
                    try {
                        ShortMessage aSM = new ShortMessage(ShortMessage.NOTE_ON, channel, sm.getData1() + 2 * transposition, sm.getData2());
                        modifyCommands(false, aSM);
                        chanTrans[channel] = transposition;
                    } catch (InvalidMidiDataException e) {
                        JOptionPane.showMessageDialog(null,
                                "Something went wrong!  I'm not sure what :-/\nMaybe you can figure it out?");
                    }

                    timerTask = new TimerTask() {
                        public void run() {
                            modifyCommands(true, null);
                        }

                    };
                    clock.schedule(timerTask, delay);

                    break;
                case ShortMessage.NOTE_OFF:
                    if (sustain) {
                        return;
                    }
                    channel = sm.getData1() % 12; // why was there + 1?
                    if (channel == 9) {
                        channel = 13;
                    }
                    try {
                        ShortMessage aSM = new ShortMessage(ShortMessage.NOTE_OFF, channel, sm.getData1() + 2 * chanTrans[channel], sm.getData2());
                        rec.send(aSM, 0L);
                    } catch (InvalidMidiDataException e) {
                        JOptionPane.showMessageDialog(null,
                                "Something went wrong!  I'm not sure what :-/\nMaybe you can figure it out?");
                    }
                    break;
                case ShortMessage.CONTROL_CHANGE:
                    switch (sm.getData1()) {
                        case Constants.RIGHTPED:
                            int pedaling = 0;
                            if (leftPedal) {
                                pedaling += 1;
                            }
                            if (sm.getData2() > 0) {
                                pedaling += 2;
                            }

                            if (rightPedal != sm.getData2() > 0) {
                                archinovica.setPedaling(pedaling);
                            }
                            rightPedal = sm.getData2() > 0;
                            break;
                        case Constants.LEFTPED:
                            pedaling = 0;
                            if (sm.getData2() > 0) {
                                pedaling += 1;
                            }
                            if (rightPedal) {
                                pedaling += 2;
                            }
                            if (leftPedal != (sm.getData2() > 0)) {
                                archinovica.setPedaling(pedaling);
                            }

                            leftPedal = sm.getData2() > 0;

                            if (middlePedal && leftPedal) {
                                archinovica.undoProgression();
                            }
                            break;
                        case Constants.MIDPED:
                            //System.out.println(sm.getData2());
                            middlePedal = sm.getData2() > 0;
                            if (middlePedal) {
                                for (int chnl = 0; chnl < 14; chnl++) {
                                    try {
                                        rec.send(new ShortMessage(ShortMessage.CONTROL_CHANGE, chnl, 120, 0), 0);
                                    } catch (InvalidMidiDataException e) {
                                        JOptionPane.showMessageDialog(null,
                                                "Something went wrong!  I'm not sure what :-/\nMaybe you can figure it out?");
                                    }
                                }
                        /* try{
                        Synthesizer synth = MidiSystem.getSynthesizer();
                        MidiChannel[] chnls = synth.getChannels();
                        for(MidiChannel mc: chnls)
                        mc.allNotesOff();
                        System.out.println("SYNTH KILLED");
                        }
                        catch(Exception e){}
                        for(int chnl = 0; chnl < 15; chnl++)
                        for(int noteNum = 0; noteNum < 128; noteNum++){
                        try{
                        ShortMessage aSM = new ShortMessage(ShortMessage.NOTE_OFF, chnl, noteNum, 0);
                        rec.send(aSM, 0L);
                        }catch(InvalidMidiDataException e){
                        JOptionPane.showMessageDialog(null,
                        "Something went wrong!  I'm not sure what :-/\nMaybe you can figure it out?");
                        }
                        }
                         */
                            }
                            break;
                    }
                    break;
            }
        }

    }

    public synchronized void modifyCommands(boolean play, ShortMessage aSM) {
        if (play) {
            ArrayList<ShortMessage> playable = new ArrayList<ShortMessage>();
            boolean[] ps = new boolean[12];
            while (commands.size() > 0) {
                try {
                    ShortMessage sm = commands.take();
                    playable.add(sm);
                    ps[(sm.getData1() - transposition * 2) % 12] = true;
                } catch (InterruptedException e) {
                    JOptionPane.showMessageDialog(null,
                            "Something went wrong!  I'm not sure what :-/\nMaybe you can figure it out?");
                }
            }

            playable = updateIntonation(ps, playable);
            for (ShortMessage sm : playable) {
                rec.send(sm, 0L);
            }
            return;
        }
        try {
            commands.put(aSM);
        } catch (InterruptedException e) {
            JOptionPane.showMessageDialog(null,
                    "Something went wrong!  I'm not sure what :-/\nMaybe you can figure it out?");
        }

    }

    public void close() {
    }

    public ArrayList<ShortMessage> updateIntonation(boolean[] ps, ArrayList<ShortMessage> sms) {
        PitchClass[] pcs = archinovica.updateIntonation(ps);
        for (int i = 0; i < 12; i++) { // calculate the transposition of all pitches
            PitchClass p = pcs[i];
            if (p != null) {
                try {

                    //synth.getChannels()[channel].setPitchBend((int)(p.getMidiPb() * 128));
                    //System.out.println(p);

                    int bend = (int) p.getMidiPb();
                    int deltaTransposition = 0;
                    int originalTransposition = transposition;
                    while (bend + bendAdjustment > 127) {
                        bendAdjustment -= 64;
                        transposition++;
                        deltaTransposition++;
                    }
                    while (bend + bendAdjustment < 0) {
                        bendAdjustment += 64;
                        transposition--;
                        deltaTransposition--;
                    }
                    if (deltaTransposition != 0) {
                        for (ShortMessage sm : sms) {
                            int thisKey = sm.getData1();
                            int thisKeyTransIndex = sm.getData1() - originalTransposition;
                            //keyTransposition[sm.getData1() - originalTransposition] = transposition;
                            sm.setMessage(sm.getCommand(), sm.getChannel(),
                                    sm.getData1() + 2 * deltaTransposition, sm.getData2());
                        }
                    }
                } catch (Exception e) {
                }
            }
        }

        for (int i = 0; i < 12; i++) {//after values are stable (transpostion and bendAdjustment) send PB messages
            PitchClass p = pcs[i];
            if (p != null) {
                int channel = i;//IS THIS RIGHT????????
                if (channel == 9) {
                    channel = 13;
                }
                int bend = (int) p.getMidiPb();
                try {
                    ShortMessage pbM = new ShortMessage(ShortMessage.PITCH_BEND, channel, 127, bend + bendAdjustment);
                    rec.send(pbM, 0L);
                } catch (Exception e) {
                }
            }
        }
        return sms;
    }

}
