package archinovica;

 

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Aggiungi qui una descrizione della classe archinovica.HorizontalSet
 *
 * @author (il tuo nome)
 * @version (un numero di versione o una data)
 */
public class HorizontalSet extends PitchSet {

    public ArrayList<PitchClass> registeredPitches;
    private Interval transposition;
    public int centralityIndex;

    public HorizontalSet(int i) {
        projectedPitches[i] = new PitchClass(i);
        registeredPitches = new ArrayList<PitchClass>();
    }

    public HorizontalSet(PitchClass[] pP,  RecursiveSearchPoint.GenerateNeighborsCallback callback) {
        super(pP);
        //System.out.println("H: constructing verticalSet");
        VerticalSet vs = new VerticalSet(pP);
        //System.out.println("H: verticalSet: " + vs);
        //System.out.println("H: constructing vSearch");
        VerticalSearcher vSearch = new VerticalSearcher(vs, callback);
        //System.out.println("H: limiting vs & adding all");
        addAll(vSearch.limitSet(0, callback));
        registeredPitches = new ArrayList<PitchClass>();
    }

    public HorizontalSet(PitchSet ps) {
        super(ps);
        registeredPitches = new ArrayList<PitchClass>();
    }

    public boolean isProjected(PitchClass projectedPitch) {
        return Arrays.asList(projectedPitches).contains(projectedPitch);
    }

    //@Precondition: contains(pc)
    public boolean registerPitch(PitchClass pc) {
        if (registeredPitches.contains(pc)) {
            return false;
        }
        registeredPitches.add(pc.clone());
        return true;
    }

    public boolean fullyRegistered() {
        return registeredPitches.size() == size();
    }

    public int getProjectionIndex(PitchClass pc) {
        return Arrays.asList(projectedPitches).indexOf(pc);
    }

    public PitchSet copyArray() {
        return new HorizontalSet(this);
    }

    public int getCentralityIndex() {
        return centralityIndex;
    }

    public void setCentralityIndex(int i) {
        centralityIndex = i;
    }

    @Override
    public String toString() {
        return super.toString() + " REGISTERED: " + registeredPitches;
    }

}
