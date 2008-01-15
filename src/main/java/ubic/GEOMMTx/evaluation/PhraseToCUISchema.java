package ubic.GEOMMTx.evaluation;

public class PhraseToCUISchema extends SpreadSheetSchema {
    public PhraseToCUISchema() {
        super();
        positions.put( "CUI", 0 );
        positions.put( "SUI", 1 );
        positions.put( "phraseLabel", 2 );
        positions.put( "Reject", 3 );
        positions.put( "mentionLabel", 4 );
        positions.put( "Comment", 5 );
    }
}
