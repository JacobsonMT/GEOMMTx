package ubic.GEOMMTx;

public class UMLSSourceCode {
    private String source;
    private String code;

    public UMLSSourceCode( String source, String code ) {
        super();
        this.source = source;
        this.code = code;
    }

    public UMLSSourceCode( String source ) {
        super();
        this.source = source;
        this.code = null;
    }
    

    public boolean hasCode() {
        return code == null;
    }

    public String getCode() {
        return code;
    }

    public void setCode( String code ) {
        this.code = code;
    }

    public String getSource() {
        return source;
    }

    public void setSource( String source ) {
        this.source = source;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ( ( code == null ) ? 0 : code.hashCode() );
        result = PRIME * result + ( ( source == null ) ? 0 : source.hashCode() );
        return result;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final UMLSSourceCode other = ( UMLSSourceCode ) obj;
        if ( code == null ) {
            if ( other.code != null ) return false;
        } else if ( !code.equals( other.code ) ) return false;
        if ( source == null ) {
            if ( other.source != null ) return false;
        } else if ( !source.equals( other.source ) ) return false;
        return true;
    }
}
