package it.jpack;

import it.jpack.StructPointer;

/**
 *
 * @author list
 */
public interface TestPointer2 extends StructPointer<TestPointer2> {
    float getFloat();
    void setFloat(float value);
    /**
     * Lo StructPointer e' un oggetto thread unsafe per sua natura, 
     * quindi anche uno StructPointer interno non deve essere thread
     * safe. Quando creo uno StructPointer esterno, creo immediatamente anche le istanze
     * di StructPointer interno; ogni chiamata a getInner restituisce sempre lo
     * stesso valore. Il puntatore interno si sposta insieme all'indice
     * del puntatore esterno.
     * @return 
     */
    TestPointer1 getInner();
    void setInner(TestPointer1 value);
}
