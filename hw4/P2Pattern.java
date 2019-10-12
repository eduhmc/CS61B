/** P2Pattern class
 *  @author Josh Hug & Vivant Sakore
 */

public class P2Pattern {
    /* Pattern to match a valid date of the form MM/DD/YYYY. Eg: 9/22/2019 */
    public static final String P1 = "^(0?[1-9]|1[0-2])[\\/](0?[1-9]|[12]\\d|3[01])[\\/](19|20)\\d{2}$"; //FIXME: Add your regex here

    /** Pattern to match 61b notation for literal IntLists. */
    public static final String P2 =  "\\(([0-9]+, +)+[0-9]+\\)"; //FIXME: Add your regex here

    /* Pattern to match a valid domain name. Eg: www.support.facebook-login.com */
    public static final String P3 = "[^-&^g&^\\.&^f].*"; //FIXME: Add your regex here

    /* Pattern to match a valid java variable name. Eg: _child13$ */
    public static final String P4 = "[^9].*"; //FIXME: Add your regex here

    /* Pattern to match a valid IPv4 address. Eg: 127.0.0.1 */
    public static final String P5 = "\\b((25[0-5]|2[0-4]\\d|[01]?\\d{1,2}|\\*)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d{1,2}|\\*)"; //FIXME: Add your regex here

}
