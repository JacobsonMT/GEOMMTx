package ubic.GEOMMTx.evaluation;

public class CUIIRIPair {
        public String IRI;
        public String CUI;

        public CUIIRIPair( String CUI, String IRI ) {
            this.CUI = CUI;
            this.IRI = IRI;
        }

        public String toString() {
            return CUI + " -> " + IRI;
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

