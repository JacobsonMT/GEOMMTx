package ubic.GEOMMTx.evaluation;

public class CUISUIPair {
    public String SUI;
    public String CUI;

    public CUISUIPair( String CUI, String SUI ) {
        this.CUI = CUI;
        this.SUI = SUI;
    }

    public String toString() {
        return CUI + " -> " + SUI;
    }

    public int hashCode() {
        return this.toString().hashCode();
     }
    
    /*
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object o ) {
        if ( o instanceof CUISUIPair ) {
            return this.toString().equals( o.toString() );
        }
        return false;
    }
}