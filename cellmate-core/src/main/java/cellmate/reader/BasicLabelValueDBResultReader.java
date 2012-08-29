package cellmate.reader;

import cellmate.tuple.TupleBag;

/**
 * User: bfemiano
 * Date: 8/28/12
 * Time: 11:51 PM
 */
public class BasicLabelValueDBResultReader<I,C> extends TupleBagDBResultReader<I, C> {

    public BasicLabelValueDBResultReader(TupleBuilder<I, C> builder){
        this.tupleBuilder = builder;
    }


    @Override
    protected void applyTupleToBag(I record, TupleBag<C> cTupleBag) {
        cTupleBag.addCellTuple(tupleBuilder.getTuple(record));
    }
}
