public class BitTwiddling {
    public static void main(String[] args) {
        int x = -79;
        int mask1 = x >> 31;
        int mask2 = -1 >>> 1;
        System.out.println((x + mask2) ^ mask2);
    }
}
