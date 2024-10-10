package archinovica;

 
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Aggiungi qui una descrizione della classe archinovica.HorizontalSearcher
 *
 * @author (il tuo nome)
 * @version (un numero di versione o una data)
 */
public class HorizontalSearcher extends GenerativeSearcher {
    public ArrayList<HorizontalSet> potentialSets = new ArrayList<HorizontalSet>();
    public ArrayList<HorizontalSet> solutions = new ArrayList<HorizontalSet>();
    public HorizontalSet untransposedSet;

    public HorizontalSearcher(PitchSet source, HorizontalSet projection,  RecursiveSearchPoint.GenerateNeighborsCallback callback) {
        super(source, projection, callback);
        untransposedSet = projection;
    }

    @Override
    public PitchSet limitSet(int setting, RecursiveSearchPoint.GenerateNeighborsCallback callback) {

        while (solutions.size() < 4) {
            ArrayList<RecursiveSearchPoint> nextPitches = findNextPitches();
            ArrayList<HorizontalSet> foundThisIteration = new ArrayList<HorizontalSet>();
            // System.out.println("HS: nextPitches: " + nextPitches);
            for (RecursiveSearchPoint rsp : nextPitches) {
                boolean pitchRegistered = false;
                for (HorizontalSet potentialSet : potentialSets) {
                    if (potentialSet.contains(rsp)) {
                        //System.out.println(potentialSet + " contains " + rsp);
                        pitchRegistered = true;
                        if (potentialSet.registerPitch(rsp) && potentialSet.fullyRegistered()) {
                            if (!solutions.contains(potentialSet) && !foundThisIteration.contains(potentialSet)) {
                                int insertionIndex = Collections.binarySearch(solutions, potentialSet, new CentralityIndex());
                                insertionIndex = -insertionIndex - 1;
                                solutions.add(insertionIndex, potentialSet);
                                /*
                                int insertionIndex = Collections.binarySearch(foundThisIteration, potentialSet, new CentralityIndex());
                                insertionIndex = -insertionIndex  - 1;
                                foundThisIteration.add(potentialSet);
                                 */
                                //equals with respect to the distance implicetly considered in the search
                                //algorithm will be ordered with respect to centralityIndex
                            }
                            //System.out.println("FULLY REGISTERD: " + potentialSet);
                        }
                        break;
                    }
                }
                if (!pitchRegistered) {
                    HorizontalSet transposedSet = (HorizontalSet) untransposedSet.clone();
                    int projectionIndex = untransposedSet.getProjectionIndex(rsp);
                    Interval transposition = untransposedSet.getArray()[projectionIndex].getInterval(rsp);
                    transposedSet.transpose(transposition);
                    transposedSet.registerPitch(rsp);
                    if (setting == 0) {
                        return transposedSet;
                    }
                    transposedSet.setCentralityIndex(potentialSets.size());
                    if (transposedSet.fullyRegistered()
                            && !solutions.contains(transposedSet) && !foundThisIteration.contains(transposedSet)) {
                        int insertionIndex = Collections.binarySearch(solutions, transposedSet, new CentralityIndex());
                        insertionIndex = -insertionIndex - 1;
                        solutions.add(insertionIndex, transposedSet);

                        /*int insertionIndex = Collections.binarySearch(foundThisIteration, transposedSet, new CentralityIndex());
                        insertionIndex = -insertionIndex  - 1;
                        foundThisIteration.add(transposedSet);
                         */
                        //equals with respect to the distance implicetly considered in the search
                        //algorithm will be ordered with respect to centralityIndex
                    }
                    //System.out.println("FULLY REGISTERD: " + transposedSet);

                    potentialSets.add(transposedSet);
                }
            }
            //solutions.addAll(foundThisIteration);
        }
        //System.out.println("NUMBER OF HORIZONTAL SOLUTIONS: " + solutions.size());
        while (solutions.size() > 4) {
            solutions.remove(solutions.size() - 1);
        }
        /*System.out.println("SOLUTIONS ARRAY: ");
        for(archinovica.PitchSet s: solutions)
        System.out.println("        " + s);
         */
        solutions.remove(0); // the defalut, case 0 (already returned)
        Collections.sort(solutions, new Intonation());
        switch (setting) {
            case 1:
                return solutions.get(0);
            case 2:
                return solutions.get(2);
            case 3:
                return solutions.get(1);
        }
        // archinovica.VerticalSet vs = new archinovica.VerticalSet(solution);

        return null;
    }

    public PitchSet getSearchableSources() {
        PitchSet searchableSources = mySourceSet.clone();
        PitchClass center = mySourceSet.getCenter();
        Collections.sort(searchableSources, new Centricity(center));
        /*System.out.println("SEARCHABLE SOURCES: ");
        for(archinovica.PitchClass pc: searchableSources)
            System.out.println("    " + pc + " (aka " + pc.signifier + ")");
            */

        //System.out.println("CENTRALIZED SET: " + searchableSources);
        return searchableSources;
    }

    class Intonation implements Comparator<HorizontalSet> {
        public int compare(HorizontalSet a, HorizontalSet b) {
            int difference = (int) (1000 * (getPbTotal(a) - getPbTotal(b)));
            if (difference == 0) {
                System.out.println("EQUAL INTONATION DIFFERENCES!!!  IT'S A ROUNDING ERROR MIRACLE!");
            }
            return difference;
        }

        public double getPbTotal(HorizontalSet hs) {
            int total = 0;
            for (PitchClass pc : hs) {
                total += pc.getMidiPb();
            }
            return total;
        }
    }

    class CentralityIndex implements Comparator<HorizontalSet> {
        public int compare(HorizontalSet a, HorizontalSet b) {
            int difference = a.getCentralityIndex() - b.getCentralityIndex();
            return difference;
        }
    }

    /*public archinovica.HorizontalSearcher(archinovica.PitchClass[] pP, archinovica.PitchSet lastSet){
    super(lastSet);
    centerSpace(lastSet.mySearcher.get(0));
    addAll(lastSet.mySearcher);
    resetSpace();
    for(SubSpace s: this){
    s.declareIndependence();
    s.setSearcher(this);
    }
    projectedPitches = pP;

    untransposedSet = new archinovica.PitchSet(pP);

    System.out.println(untransposedSet);
    System.out.println(untransposedSet.mySearcher);

    registeredPitches = new Hashtable<archinovica.PitchSet, Integer>();
    potentialSets = new ArrayList<archinovica.PitchSet>();
    solutions = new ArrayList<archinovica.PitchSet>();
    System.out.println(this);

    // archinovica.Archinovica.gui.displayPitches(myPitchSet.getArray());
    }
    public archinovica.PitchSet search(int setting){
    int limit = 1;
    if(setting > 0)
    limit = 4;
    while(solutions.size() < limit){
    //System.out.println("SOTIONSLU " + solutions.size());
    //System.out.println("POTENTIA" + potentialSets.size());
    if(potentialSets.size() > 0)
    System.out.println("REG" + registeredPitches.get(potentialSets.get(0)));
    generativeSearch();

    }
    archinovica.PitchSet solution = null;
    if(setting == 0){
    solution = solutions.get(0);
    return solution;
    }
    solutions.remove(0);
    switch(setting){
    case 1:
    solution = Collections.max(solutions);
    break;
    case 2:
    solution = Collections.min(solutions);
    break;
    case 3:
    solutions.remove(Collections.max(solutions));
    solutions.remove(Collections.min(solutions));
    solution = solutions.get(0);
    break;
    }
    return solution;
    }

    @Override
    public boolean pitchFound(archinovica.RecursiveSearchPoint s){
    // System.out.println("SUBSPACEFOUND? " + s);
    int projectedIndex = Arrays.asList(projectedPitches).indexOf(s);
    if(getSubSpaces(s) < size() || projectedIndex < 0){
    //System.out.println("NOT CONTAINED");
    return false;
    }
    //System.out.println("CONTAINED");
    for(archinovica.PitchSet ps: potentialSets){
    if(ps.contains(s)){
    if(ps.registerPitch(s)){
    if(ps.fullyRegistered())
    solutions.add(ps);
    return true;
    }
    else
    return false;
    }
    //System.out.println("archinovica.PitchSet size: " + ps.size());
    }
    archinovica.Interval transposition = untransposedSet.getArray()[projectedIndex].getInterval(s);
    archinovica.PitchSet ps = untransposedSet.getTransposedSet(transposition);
    ps.registerPitch(s);
    archinovica.Archinovica.gui.displayStaticSigns(ps);
    //registeredPitches.put(ps, 1);
    potentialSets.add(ps);
    return true;

    }

    @Override
    public String toString(){
    return "HORIZONTAL SEARCHER" + super.toString();
    }

    public void print(){
    System.out.println(this);
    }
     */
}
