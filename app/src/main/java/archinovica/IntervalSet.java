package archinovica;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Aggiungi qui una descrizione della classe archinovica.IntervalSet
 *
 * @author (il tuo nome)
 * @version (un numero di versione o una data)
 */
public class IntervalSet extends SemioticGroup<Interval> {

    public PitchClass projectedReferencePitch;

    /*public archinovica.IntervalSet(archinovica.PitchClass[] pitches, int index)
    {
    super();
    System.out.println("CONSTRUCTING ISET");
    projectedReferencePitch = new archinovica.PitchClass(index);
    for(int n = 1; n < 12; n++){
    archinovica.PitchClass pitch = pitches[(n + index) % 12];
    if(pitch != null){
    add(pitches[index].getInterval(pitch));
    System.out.println("INTERVALADDED");
    }
    }

    }

    /*public archinovica.IntervalSet(archinovica.Interval i){
    super();
    System.out.println("CONSTRUNCTOR WITHOUT PROJECTED INTERVALS");
    i.setSearcher(this);
    add(i);
    }
     */

    /*public archinovica.IntervalSet(ArrayList<archinovica.Interval> intervals){
    super();
    System.out.println("CONSTRUNCTOR WITHOUT PROJECTED INTERVALS");
    }
     */

    public IntervalSet(ArrayList<PitchClass> pitches, int index) {
        super();
        projectedReferencePitch = pitches.get(index).clone();
        for (int n = index + 1; n < index + pitches.size(); n++) {
            add(pitches.get(index).getInterval(pitches.get(n % pitches.size())));
        }
        //System.out.print("pitches.size() " + pitches.size() + " number of interv " + size() + " starting index " + index);
    }

    /*public archinovica.IntervalSet(archinovica.Interval i){
    super();
    add(i);
    archinovica.PitchClass[] pitches = new archinovica.PitchClass[12];
    int[] origin = new int[]{0, 0};
    pitches[0] = new archinovica.PitchClass(origin);
    lastSet = new archinovica.PitchSet(new archinovica.PitchClass(this), pitches);

    }
     */

    public boolean overlays(IntervalSet subsumingSet) {
        if (size() == 0) {
            return true;
        }
        int n = 0;
        int start = -1;
        for (int i = 0; i < subsumingSet.size(); i++) {
            if (get(0).equals(subsumingSet.get(i))) {
                start = i;
                n++;
                break;
            }
        }
        if (start == -1) {
            return false;
        }
        for (int i = start + 1; i < subsumingSet.size() + start + 1; i++) {
            if (n == size()) {
                return true;
            }
            if (get(n).equals(subsumingSet.get(i % subsumingSet.size()))) {
                n++;
            }
        }
        return false;
    }

    public static PitchClass[] randomPitchBinary() {
        Archinovica.initializeSpace();
        PitchClass[] pitchBinary = new PitchClass[12];
        for (int i = 0; i < 12; i++) {
            if (Math.random() > 0.5) {
                pitchBinary[i] = new PitchClass(i);
            }
        }
        System.out.println(Arrays.asList(pitchBinary));
        return pitchBinary;
    }

    /*public static void printIntervalSet(archinovica.PitchClass[] pitchBinary, int i){
    ArrayList<archinovica.PitchClass> pitchSet = new ArrayList<archinovica.PitchClass>();
    for(archinovica.PitchClass p: pitchBinary)
    if(p != null){
    pitchSet.add(p);
    }
    System.out.println(new archinovica.IntervalSet(pitchSet, i));
    }*/

}
