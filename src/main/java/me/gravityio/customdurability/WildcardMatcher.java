package me.gravityio.customdurability;

public class WildcardMatcher {

    /**
     * I wanted to figure out how to do wildcard pattern matching,
     * so this isn't spec perfect, but I've tested it enough to say it works
     * @param wildcard *.txt File?.txt minecraft:*
     * @param value The basic string
     * @return whether it matches
     */
    public static boolean matches(String wildcard, String value) {
        int al = wildcard.length();
        int bl = value.length();

        int ai = 0;
        int bi = 0;

        int ss = -1;
        while (ai < al) {
            char ac = wildcard.charAt(ai);

            if (ac == '*') {
                ss = ++ai;
                continue;
            }

            if (bi >= bl) break;
            char bc = value.charAt(bi);
            if (ac == '?' || ac == bc) {
                ++ai;
                ++bi;
            } else {
                if (ss == -1) {
                    return false;
                } else {
                    ai = ss;
                    bi++;
                }
            }
        }

        if (ss == -1 && al != bl) return false;

        char last = wildcard.charAt(ai - 1);
        if (last != '*' && last != '?' && bi < bl) {
            return false;
        }

        return ai == al;
    }

}
