public class LiveApplication {

    public static void main(String[] args) {
        Archinovica archinovica = new Archinovica();
        LiveReceiver liveReceiver = new LiveReceiver(archinovica);
        new GUI(archinovica, liveReceiver);
    }

}
